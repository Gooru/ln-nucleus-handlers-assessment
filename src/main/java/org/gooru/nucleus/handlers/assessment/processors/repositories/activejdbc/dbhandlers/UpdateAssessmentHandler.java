package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 11/1/16.
 */
class UpdateAssessmentHandler implements DBHandler {
  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAssessmentHandler.class);
  private AJEntityAssessment assessment;

  public UpdateAssessmentHandler(ProcessorContext context) {
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
      LOGGER.warn("Anonymous user attempting to edit assessment");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Payload should not be empty
    if (context.request() == null || context.request().isEmpty()) {
      LOGGER.warn("Empty payload supplied to upload assessment");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Empty payload"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Our validators should certify this
    JsonObject errors = new PayloadValidator() {
    }.validatePayload(context.request(), AJEntityAssessment.editFieldSelector(), AJEntityAssessment.getValidatorRegistry());
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
      AJEntityAssessment.findBySQL(AJEntityAssessment.VALIDATE_FOR_DELETE, AJEntityAssessment.ASSESSMENT, context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessment = assessments.get(0);
    return new AuthorizerBuilder().buildUpdateAuthorizer(this.context).authorize(assessment);
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

  private boolean authorized() {
    String owner_id = assessment.getString(AJEntityAssessment.OWNER_ID);
    String course_id = assessment.getString(AJEntityAssessment.COURSE_ID);
    long authRecordCount;
    // If this assessment is not part of course, then user should be either owner or collaborator on course
    if (course_id != null) {
      authRecordCount = Base.count(AJEntityAssessment.TABLE_COURSE, AJEntityAssessment.AUTH_FILTER, course_id, context.userId(), context.userId());
      if (authRecordCount >= 1) {
        return true;
      }
    } else {
      // Assessment is not part of course, hence we need user to be either owner or collaborator on assessment
      if (context.userId().equalsIgnoreCase(owner_id)) {
        // Owner is fine
        return true;
      } else {
        String collaborators = assessment.getString(AJEntityAssessment.COLLABORATOR);
        if (collaborators == null || collaborators.isEmpty()) {
          return false;
        } else {
          JsonArray collaboratorsArray = new JsonArray(collaborators);
          if (collaboratorsArray.contains(context.userId())) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
