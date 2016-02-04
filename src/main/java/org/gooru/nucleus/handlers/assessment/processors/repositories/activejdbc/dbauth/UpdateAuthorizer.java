package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import io.vertx.core.json.JsonArray;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * Created by ashish on 29/1/16.
 */
class UpdateAuthorizer implements Authorizer<AJEntityAssessment> {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(Authorizer.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  UpdateAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityAssessment assessment) {
    String owner_id = assessment.getString(AJEntityAssessment.OWNER_ID);
    String course_id = assessment.getString(AJEntityAssessment.COURSE_ID);
    long authRecordCount;
    // If this assessment is not part of course, then user should be either owner or collaborator on course
    if (course_id != null) {
      try {
        authRecordCount = Base.count(AJEntityAssessment.TABLE_COURSE, AJEntityAssessment.AUTH_FILTER, course_id, context.userId(), context.userId());
        if (authRecordCount >= 1) {
          return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
        }
      } catch (DBException e) {
        LOGGER.error("Error checking authorization for update for Assessment '{}' for course '{}'", context.assessmentId(), course_id, e);
        return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
      }
    } else {
      // Assessment is not part of course, hence we need user to be either owner or collaborator on assessment
      if (context.userId().equalsIgnoreCase(owner_id)) {
        // Owner is fine
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
      } else {
        String collaborators = assessment.getString(AJEntityAssessment.COLLABORATOR);
        if (collaborators != null && !collaborators.isEmpty()) {
          JsonArray collaboratorsArray = new JsonArray(collaborators);
          if (collaboratorsArray.contains(context.userId())) {
            return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
          }
        }
      }
    }
    LOGGER.warn("User: '{}' is not owner/collaborator of assessment: '{}' or owner/collaborator on course", context.userId(), context.assessmentId());
    return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
      ExecutionResult.ExecutionStatus.FAILED);
  }
}
