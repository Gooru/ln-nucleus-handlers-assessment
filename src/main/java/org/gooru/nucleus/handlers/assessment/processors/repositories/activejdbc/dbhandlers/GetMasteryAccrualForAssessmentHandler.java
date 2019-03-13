package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.DbHelperUtil;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

class GetMasteryAccrualForAssessmentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(GetMasteryAccrualForAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;
  private AJEntityAssessment assessment;

  GetMasteryAccrualForAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    if (context.userId() == null || context.userId().isEmpty()) {
      LOGGER.warn("Invalid user");
      return new ExecutionResult<>(
          MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    LazyList<AJEntityAssessment> assessments =
        AJEntityAssessment
            .findBySQL(AJEntityAssessment.FETCH_ASSESSMENT_EXTERNAL_ASMT_QUERY,
                context.assessmentId());
    if (assessments.isEmpty()) {
      LOGGER.warn("Not able to find assessment '{}'", this.context.assessmentId());
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    this.assessment = assessments.get(0);
    return AuthorizerBuilder.buildTenantAuthorizer(this.context).authorize(assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // TODO: Implement this
    // Gut codes column should be not null
    // It should contain at least one competency
    // We do not verify the competency is leaf node as that info is stashed in dsdb
    try {
      List<String> gutCodes = this.assessment.getGutCodes();
      JsonArray masteryAccrualCompetencies = new JsonArray();
      if (gutCodes != null && !gutCodes.isEmpty()) {
        String gutCodesString = DbHelperUtil.toPostgresArrayString(gutCodes);
        List competencies = Base
            .firstColumn(FILTER_COMPS_FROM_GUT_CODES, gutCodesString, gutCodesString);
        if (competencies != null && !competencies.isEmpty()) {
          for (Object competency : competencies) {
            masteryAccrualCompetencies.add(competency);
          }
        }
      }
      JsonObject response = new JsonObject()
          .put("masteryAccrualCompetencies", masteryAccrualCompetencies);

      return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(response),
          ExecutionResult.ExecutionStatus.SUCCESSFUL);
    } catch (SQLException e) {
      LOGGER.warn("Caught exception", e);
      return new ExecutionResult<>(MessageResponseFactory
          .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private static final String FILTER_COMPS_FROM_GUT_CODES =
      "select id from taxonomy_code where id =  any(?::text[])  and (code_type = 'standard_level_1' or code_type = 'standard_level_2') "
          + " and standard_framework_id = 'GDT' and id not in (select parent_taxonomy_code_id where parent_taxonomy_code_id = any(?::text[]))";
}
