package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 11/1/16.
 */
class FetchCollaboratorHandler implements DBHandler {
  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(FetchCollaboratorHandler.class);
  public FetchCollaboratorHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // Fetch the assessment where type is assessment and it is not deleted already and id is specified id

    LazyList<AJEntityAssessment> assessments = AJEntityAssessment
      .findBySQL(
        AJEntityAssessment.SELECT_FOR_VALIDATE,
        AJEntityAssessment.ASSESSMENT,
        context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    // Nothing to validate, so we are all set
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    LazyList<AJEntityAssessment> assessments = AJEntityAssessment.findBySQL(
      AJEntityAssessment.SELECT_COLLABORATOR,
      context.assessmentId()
    );
    if (assessments.size() == 0 || assessments.isEmpty()) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    } else if (assessments.size() > 1) {
      LOGGER.warn("Assessment id: {} present multiple times, not sure which one is being looked for", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    } else {
      AJEntityAssessment assessment = assessments.get(0);
      String response = assessment.toJson(false, AJEntityAssessment.COLLABORATOR);
      return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(new JsonObject(response)), ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }
}