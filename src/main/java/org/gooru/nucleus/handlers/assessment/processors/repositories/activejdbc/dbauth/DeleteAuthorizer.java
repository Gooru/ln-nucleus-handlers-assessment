package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 29/1/16.
 */
class DeleteAuthorizer implements Authorizer<AJEntityAssessment> {
  private final ProcessorContext context;
  private final Logger LOGGER = LoggerFactory.getLogger(Authorizer.class);

  DeleteAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityAssessment assessment) {
    String owner_id = assessment.getString(AJEntityAssessment.OWNER_ID);
    String course_id = assessment.getString(AJEntityAssessment.COURSE_ID);
    long authRecordCount;
    // If this assessment is part of course, then user should be either owner or collaborator on course
    if (course_id != null) {
      try {
        authRecordCount = Base.count(AJEntityAssessment.TABLE_COURSE, AJEntityAssessment.AUTH_FILTER, course_id, context.userId(), context.userId());
        if (authRecordCount >= 1) {
          return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
        }
      } catch (DBException e) {
        LOGGER.error("Error checking authorization for delete for Assessment '{}' for course '{}'", context.assessmentId(), course_id, e);
        return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("Not able to delete questions for this assessment"),
          ExecutionResult.ExecutionStatus.FAILED);
      }
    } else {
      // Assessment is not part of course, hence we need user to be owner
      if (context.userId().equalsIgnoreCase(owner_id)) {
        // Owner is fine
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
      }
    }
    LOGGER.warn("User: '{}' is not owner of assessment: '{}' or owner/collaborator on course", context.userId(), context.assessmentId());
    return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
  }
}
