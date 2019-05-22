package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(Authorizer.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  DeleteAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityAssessment assessment) {
    String ownerId = assessment.getOwnerId();
    String courseId = assessment.getCourseId();
    long authRecordCount;
    // If this assessment is part of course, then user should be either
    // owner or collaborator on course
    if (courseId != null) {
      try {
        authRecordCount = Base
            .count(AJEntityAssessment.TABLE_COURSE, AssessmentDao.AUTH_FILTER, courseId,
                context.userId(), context.userId());
        if (authRecordCount >= 1) {
          return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
        }
      } catch (DBException e) {
        LOGGER.error("Error checking authorization for delete for Assessment '{}' for course '{}'",
            context.assessmentId(), courseId, e);
        return new ExecutionResult<>(
            MessageResponseFactory
                .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
            ExecutionResult.ExecutionStatus.FAILED);
      }
    } else {
      // Assessment is not part of course, hence we need user to be owner
      if (context.userId().equalsIgnoreCase(ownerId)) {
        // Owner is fine
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
      }
    }
    LOGGER.warn("User: '{}' is not owner of assessment: '{}' or owner/collaborator on course",
        context.userId(),
        context.assessmentId());
    return new ExecutionResult<>(
        MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
        ExecutionResult.ExecutionStatus.FAILED);
  }
}
