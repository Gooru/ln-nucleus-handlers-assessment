package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityCULC;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ashish on 11/1/16.
 */
class DeleteAssessmentHandler implements DBHandler {
  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAssessmentHandler.class);

  public DeleteAssessmentHandler(ProcessorContext context) {
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
        AJEntityAssessment.SELECT_FOR_VALIDATE,
        AJEntityAssessment.ASSESSMENT,
        context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("assessment id: " + context.assessmentId()),
        ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessmentToDelete = assessments.get(0);
    // The user should be owner of the assessment, collaborator will not do
    if (!(assessmentToDelete.getString(AJEntityAssessment.CREATOR_ID)).equalsIgnoreCase(context.userId())) {
      LOGGER.warn("User: '{}' is not owner of assessment", context.userId());
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // This should not be published
    if (assessmentToDelete.getDate(AJEntityAssessment.PUBLISH_DATE) != null) {
      LOGGER.warn("Assessment with id '{}' is published assessment so should not be deleted", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Assessment is published"), ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // Update assessment, we need to set the deleted flag and user who is deleting it but We do not reset the sequence id right now
    AJEntityAssessment assessmentToDelete = new AJEntityAssessment();
    assessmentToDelete.setId(context.assessmentId());
    assessmentToDelete.setBoolean(AJEntityAssessment.IS_DELETED, true);
    assessmentToDelete.setString(AJEntityAssessment.MODIFIER_ID, context.userId());
    boolean result = assessmentToDelete.save();
    if (!result) {
      LOGGER.error("Assessment with id '{}' failed to delete", context.assessmentId());
      if (assessmentToDelete.hasErrors()) {
        Map<String, String> map = assessmentToDelete.errors();
        JsonObject errors = new JsonObject();
        map.forEach(errors::put);
        return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
      }
    }
    // If the assessment is present in CULC table, we do similar thing there, except for modifier_id as this field is not needed in CULC entity

    LazyList<AJEntityCULC> culcToDeleteList = AJEntityCULC.findBySQL(
      AJEntityCULC.SELECT_FOR_DELETE,
      context.assessmentId(), false);
    int numberOfEntries = culcToDeleteList.size();
    if (numberOfEntries == 1) {
      AJEntityCULC entityCULC = culcToDeleteList.get(0);
      // We have a record and we have to delete it
      entityCULC.setBoolean("is_deleted", true);
      result = assessmentToDelete.save();
      if (!result) {
        LOGGER.error("Failed to delete CULC record for assessment '{}'", context.assessmentId());
        if (entityCULC.hasErrors()) {
          Map<String, String> map = entityCULC.errors();
          JsonObject errors = new JsonObject();
          map.forEach(errors::put);
          return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
        }
      }
    } else if (numberOfEntries > 1) {
      // There are multiple records. Not sure which one we want to delete
      LOGGER.error("Multiple CULC record for assessment '{}'", context.assessmentId());
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Multiple child records found. Cannot delete"),
        ExecutionResult.ExecutionStatus.FAILED);
    } else {
      // Nothing to do. We do not have a live record.
      LOGGER.debug("No record in CULC for assessment '{}' to be deleted", context.assessmentId());
    }
    return new ExecutionResult<>(
      MessageResponseFactory.createNoContentResponse("Deleted", EventBuilderFactory.getDeleteAssessmentEventBuilder(context.assessmentId())),
      ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    // This operation is not read only and so is transaction
    return false;
  }
}
