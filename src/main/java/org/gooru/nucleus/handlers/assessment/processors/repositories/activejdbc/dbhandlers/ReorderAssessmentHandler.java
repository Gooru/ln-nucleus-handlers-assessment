package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
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
class ReorderAssessmentHandler implements DBHandler {
  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ReorderAssessmentHandler.class);

  public ReorderAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Missing assessment id"),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty() || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous user attempting to delete assessment");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);

  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    // Fetch the assessment where type is assessment and it is not deleted already and id is specified id
    LazyList<AJEntityAssessment> assessments = AJEntityAssessment
      .findBySQL(
        AJEntityAssessment.AUTHORIZER_QUERY,
        AJEntityAssessment.ASSESSMENT,
        context.assessmentId(), false, context.userId(),context.userId());
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessment = assessments.get(0);
    if (true) return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("This is awesome"),
      ExecutionResult.ExecutionStatus.FAILED);
    // The user should be owner or collaborator of the assessment
    // FIXME: 21/1/16 : Need to verify if the user is part of collaborator or owner of course where this assessment may be contained
    // PGobject pGobject =  (PGobject) assessment.get(AJEntityAssessment.COLLABORATOR);
    // JsonArray array = new JsonArray(pGobject.getValue());
    // System.out.println(array);
    if (!(assessment.getString(AJEntityAssessment.CREATOR_ID)).equalsIgnoreCase(context.userId())) {
      LOGGER.warn("User: '{}' is not owner of assessment", context.userId());
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // This should not be published
    if (assessment.getDate(AJEntityAssessment.PUBLISH_DATE) != null) {
      LOGGER.warn("Assessment with id '{}' is published assessment so should not be deleted", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Assessment is published"), ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

}
