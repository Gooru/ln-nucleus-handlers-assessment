package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 11/1/16.
 */
class AddQuestionToAssessmentHandler implements DBHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAssessmentHandler.class);
  private final ProcessorContext context;
  private AJEntityAssessment assessment;

  public AddQuestionToAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty() || context.questionId() == null || context.questionId().isEmpty()) {
      LOGGER.warn("Missing assessment/question id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Missing assessment/question id"),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty() || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous user attempting to edit assessment");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Payload should not be empty
    if (context.request() == null || context.request().isEmpty()) {
      LOGGER.warn("Empty payload supplied to edit assessment");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Empty payload"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Our validators should certify this
    JsonObject errors = new PayloadValidator() {
    }.validatePayload(context.request(), AJEntityAssessment.addQuestionFieldSelector(), AJEntityAssessment.getValidatorRegistry());
    if (errors != null && !errors.isEmpty()) {
      LOGGER.warn("Validation errors for request");
      return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);

  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    // Fetch the assessment where type is assessment and it is not deleted already and id is specified id

    LazyList<AJEntityAssessment> assessments =
      AJEntityAssessment.findBySQL(AJEntityAssessment.AUTHORIZER_QUERY, AJEntityAssessment.ASSESSMENT, context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessment = assessments.get(0);
    return new AuthorizerBuilder().buildAddQuestionToAssessmentAuthorizer(this.context).authorize(assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    try {
      Object sequence = Base.firstCell(AJEntityAssessment.MAX_QUESTION_SEQUENCE_QUERY, this.context.assessmentId());
      int sequenceId = 1;
      if (sequence != null) {
        int currentSequence = Integer.valueOf(sequence.toString());
        sequenceId = currentSequence + 1;
      }
      long count = Base
        .exec(AJEntityAssessment.ADD_QUESTION_QUERY, this.context.assessmentId(), this.context.userId(), sequenceId, this.context.questionId(),
          this.context.userId());

      if (count == 1) {
        return new ExecutionResult<>(MessageResponseFactory
          .createNoContentResponse("Question added", EventBuilderFactory.getAddQuestionToAssessmentEventBuilder(context.assessmentId())),
          ExecutionResult.ExecutionStatus.SUCCESSFUL);
      }
      LOGGER.error("Something is wrong. Adding question '{}' to assessment '{}' updated '{}' rows", this.context.questionId(),
        this.context.assessmentId(), count);

    } catch (DBException e) {
      LOGGER.error("Not able to add question '{}' to assessment '{}'", this.context.questionId(), this.context.assessmentId(), e);
    }
    return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("Unable to add question"),
      ExecutionResult.ExecutionStatus.FAILED);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
