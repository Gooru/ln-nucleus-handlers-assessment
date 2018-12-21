package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityDiagnosticAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityGradeMaster;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

class FetchDiagnosticAssessmentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FetchDiagnosticAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private static final String FW_CODE = "fw_code";
  private final ProcessorContext context;
  private Long gradeId;
  private String fwCode;

  FetchDiagnosticAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    ExecutionResult<MessageResponse> result = initializeGradeAndFwCode();
    if (result.hasFailed()) {
      return result;
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    List<AJEntityGradeMaster> gradeMasterList = AJEntityGradeMaster.getById(gradeId);
    if (gradeMasterList == null || gradeMasterList.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory
          .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("grade.invalid")),
          ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // TODO: Bring in language angle here to build fallback
    AJEntityDiagnosticAssessment diagnosticAssessment = AJEntityDiagnosticAssessment
        .fetchDiagAsmtByGradeAndFw(gradeId, fwCode);
    if (diagnosticAssessment == null) {
      LOGGER.info("Diagnostic assessment not found for grade: '{}', fw: '{}'", gradeId, fwCode);
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found")),
          ExecutionStatus.FAILED);
    }
    String assessmentId = diagnosticAssessment.fetchAssessmentId().toString();
    LazyList<AJEntityAssessment> assessments =
        AJEntityAssessment
            .findBySQL(AJEntityAssessment.FETCH_ASSESSMENT_QUERY, assessmentId);
    if (assessments.isEmpty()) {
      LOGGER.warn("Not able to find assessment '{}'", assessmentId);
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessment = assessments.get(0);
    JsonObject response = new JsonObject(
        JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityAssessment.FETCH_QUERY_FIELD_LIST)
            .toJson(assessment));
    LazyList<AJEntityQuestion> questions =
        AJEntityQuestion.findBySQL(AJEntityQuestion.FETCH_QUESTION_SUMMARY_QUERY, assessmentId);
    if (!questions.isEmpty()) {
      response.put(AJEntityQuestion.QUESTION, new JsonArray(
          JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityQuestion.FETCH_QUESTION_SUMMARY_FIELDS)
              .toJson(questions)));
    } else {
      response.put(AJEntityQuestion.QUESTION, new JsonArray());
    }
    return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(response),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private ExecutionResult<MessageResponse> initializeGradeAndFwCode() {
    try {
      String gradeString = context.request().getString(MessageConstants.ID_GRADE);
      this.gradeId = Long.parseLong(gradeString);
      JsonArray fwCodes = context.request().getJsonArray(FW_CODE);
      this.fwCode = (fwCodes != null && !fwCodes.isEmpty()) ? fwCodes.getString(0) : null;
      LOGGER.debug("Fetching diagnostic for gradeId: '{}' and FW: '{}'", gradeId, fwCode);
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    } catch (NumberFormatException e) {
      return new ExecutionResult<>(MessageResponseFactory
          .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("grade.invalid")),
          ExecutionStatus.FAILED);
    }
  }
}
