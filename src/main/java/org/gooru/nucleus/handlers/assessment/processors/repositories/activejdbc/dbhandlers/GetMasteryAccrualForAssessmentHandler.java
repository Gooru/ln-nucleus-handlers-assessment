package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.DbHelperUtil;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentExDao;
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
  private List<AJEntityAssessment> assessments;
  private JsonArray assessmentIds;
  private final Set<String> assessmentsGutCodes = new HashSet<>();

  GetMasteryAccrualForAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    assessmentIds = context.request().getJsonArray(MessageConstants.ASSESSMENT_IDS);
    if (assessmentIds == null || assessmentIds.isEmpty()) {
      LOGGER.warn("Missing assessment Ids");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
          RESOURCE_BUNDLE.getString("missing.assessment.ids")),
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
    LazyList<AJEntityAssessment> assessmentList =
        AJEntityAssessment.findBySQL(AssessmentExDao.FETCH_ASSESSMENTS_EXTERNAL_ASMT_QUERY,
            DbHelperUtil.toPostgresArrayString(assessmentIds.getList()));
    if (assessments != null && assessments.isEmpty()) {
      LOGGER.warn("Not able to find assessments '{}'", assessmentIds);
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    this.assessments = assessmentList;
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);

  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    try {
      getAssessmentsGutCodes();
      JsonObject response =
          new JsonObject().put("masteryAccrualCompetencies", mapAssessmentWithMasteryAccrualComp());

      return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(response),
          ExecutionResult.ExecutionStatus.SUCCESSFUL);
    } catch (SQLException e) {
      LOGGER.warn("Caught exception", e);
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

  }

  private void getAssessmentsGutCodes() throws SQLException {
    for (AJEntityAssessment assessment : this.assessments) {
      assessmentsGutCodes.addAll(assessment.getGutCodes());
    }
  }

  private JsonArray mapAssessmentWithMasteryAccrualComp() throws SQLException {
    final JsonArray assessmentswithMasteryAccrualComp = new JsonArray();
    if (assessmentsGutCodes != null && !assessmentsGutCodes.isEmpty()) {
      String gutCodesString = DbHelperUtil.toPostgresArrayString(assessmentsGutCodes);
      List competencies = Base
          .firstColumn(FILTER_COMPS_FROM_GUT_CODES, gutCodesString, gutCodesString);
      if (competencies != null && !competencies.isEmpty()) {
        for (AJEntityAssessment assessment : assessments) {
          final List<String> gutCodes = assessment.getGutCodes();
          if (gutCodes != null && !gutCodes.isEmpty()) {
            final JsonArray masteryAccrualCompetencies = new JsonArray();
            gutCodes.forEach(gutCode -> {
              if (competencies.contains(gutCode)) {
                masteryAccrualCompetencies.add(gutCode);
              }
            });
            if (!masteryAccrualCompetencies.isEmpty()) {
              JsonObject assessmentWithMasteryAccrualComp = new JsonObject();
              assessmentWithMasteryAccrualComp.put(assessment.getString(MessageConstants.ID),
                  masteryAccrualCompetencies);
              assessmentswithMasteryAccrualComp.add(assessmentWithMasteryAccrualComp);
            }
          }
        }
      }
    }
    return assessmentswithMasteryAccrualComp;
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private static final String FILTER_COMPS_FROM_GUT_CODES =
      "select id from taxonomy_code where id =  any(?::text[])  and (code_type = 'standard_level_1' or code_type = 'standard_level_2') "
          + " and standard_framework_id = 'GDT' and id not in (select parent_taxonomy_code_id where parent_taxonomy_code_id = any(?::text[]))";
}
