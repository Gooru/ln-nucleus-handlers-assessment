package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by ashish on 11/1/16.
 */
class AddQuestionToAssessmentHandler implements DBHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
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
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.question.id")),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty() || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous user attempting to edit assessment");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    // Payload should not be empty
    if (context.request() == null || context.request().isEmpty()) {
      LOGGER.warn("Empty payload supplied to edit assessment");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    // Our validators should certify this
    JsonObject errors = new DefaultPayloadValidator()
      .validatePayload(context.request(), AJEntityAssessment.addQuestionFieldSelector(), AJEntityAssessment.getValidatorRegistry());
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
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("assessment.id") + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    this.assessment = assessments.get(0);
    return AuthorizerBuilder.buildAddQuestionToAssessmentAuthorizer(this.context).authorize(this.assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    try {
      Object sequence = Base.firstCell(AJEntityQuestion.MAX_QUESTION_SEQUENCE_QUERY, this.context.assessmentId());
      int sequenceId = 1;
      if (sequence != null) {
        int currentSequence = Integer.valueOf(sequence.toString());
        sequenceId = currentSequence + 1;
      }
      long count = Base
        .exec(AJEntityQuestion.ADD_QUESTION_QUERY, this.context.assessmentId(), this.context.userId(), sequenceId, this.context.questionId(),
          this.context.userId());

      if (count == 1) {
        return updateGrading();
      }
      LOGGER.error("Something is wrong. Adding question '{}' to assessment '{}' updated '{}' rows", this.context.questionId(),
        this.context.assessmentId(), count);
      return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("unexpected.count.updated.in.store")),
        ExecutionResult.ExecutionStatus.FAILED);

    } catch (DBException e) {
      LOGGER.error("Not able to add question '{}' to assessment '{}'", this.context.questionId(), this.context.assessmentId(), e);
      return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
        ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

  private ExecutionResult<MessageResponse> updateGrading() {
    String currentGrading = this.assessment.getString(AJEntityAssessment.GRADING);
    boolean assessmentUpdated = false;
    if (!currentGrading.equalsIgnoreCase(AJEntityAssessment.GRADING_TYPE_TEACHER)) {
      try {
        long count = Base.count(AJEntityQuestion.TABLE_QUESTION, AJEntityQuestion.OPEN_ENDED_QUESTION_FILTER, this.context.assessmentId());
        if (count > 0) {
          this.assessment.setGrading(AJEntityAssessment.GRADING_TYPE_TEACHER);
          assessmentUpdated = true;
          if (!this.assessment.save()) {
            LOGGER.error("Assessment '{}' grading type change failed", this.context.assessmentId());
            if (this.assessment.hasErrors()) {
              Map<String, String> map = assessment.errors();
              JsonObject errors = new JsonObject();
              map.forEach(errors::put);
              return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
            }
          }
        }
      } catch (DBException e) {
        LOGGER.error("Assessment '{}' grading type change lookup failed", this.context.assessmentId(), e);
        return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
      }
    }

    if (!assessmentUpdated) {
      // Need to update the time stamp
      this.assessment.setTimestamp(AJEntityAssessment.UPDATED_AT, new Timestamp(System.currentTimeMillis()));
      boolean result = this.assessment.save();
      if (!result) {
        LOGGER.error("Assessment with id '{}' failed to save modified time stamp", context.assessmentId());
        if (this.assessment.hasErrors()) {
          Map<String, String> map = this.assessment.errors();
          JsonObject errors = new JsonObject();
          map.forEach(errors::put);
          return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
        }
      }
    }

    return new ExecutionResult<>(MessageResponseFactory
      .createNoContentResponse("Question added", EventBuilderFactory.getAddQuestionToAssessmentEventBuilder(context.assessmentId())),
      ExecutionResult.ExecutionStatus.SUCCESSFUL);

  }

  private static class DefaultPayloadValidator implements PayloadValidator {
  }
}
