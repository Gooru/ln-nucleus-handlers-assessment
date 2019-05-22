package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import io.vertx.core.json.JsonArray;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 31/1/16.
 */
class AddQuestionToAssessmentAuthorizer implements Authorizer<AJEntityAssessment> {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(Authorizer.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  public AddQuestionToAssessmentAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityAssessment assessment) {
    String ownerId = assessment.getOwnerId();
    String courseId = assessment.getCourseId();
    long authRecordCount;
    // If this assessment is not part of course, then user should be either
    // owner or collaborator on course
    if (courseId != null) {
      try {
        authRecordCount = Base
            .count(AJEntityAssessment.TABLE_COURSE, AJEntityAssessment.AUTH_FILTER, courseId,
                context.userId(), context.userId());
        if (authRecordCount >= 1) {
          return authorizeForQuestion(assessment);
        }
      } catch (DBException e) {
        LOGGER.error("Error checking authorization for update for Assessment '{}' for course '{}'",
            context.assessmentId(), courseId, e);
        return new ExecutionResult<>(
            MessageResponseFactory
                .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
            ExecutionResult.ExecutionStatus.FAILED);
      }
    } else {
      // Assessment is not part of course, hence we need user to be either
      // owner or collaborator on assessment
      if (context.userId().equalsIgnoreCase(ownerId)) {
        // Owner is fine
        return authorizeForQuestion(assessment);
      } else {
        String collaborators = assessment.getString(AJEntityAssessment.COLLABORATOR);
        if (collaborators != null && !collaborators.isEmpty()) {
          JsonArray collaboratorsArray = new JsonArray(collaborators);
          if (collaboratorsArray.contains(context.userId())) {
            return authorizeForQuestion(assessment);
          }
        }
      }
    }
    LOGGER.warn(
        "User: '{}' is not owner/collaborator of assessment: '{}' or owner/collaborator on course",
        context.userId(), context.assessmentId());
    return new ExecutionResult<>(
        MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
        ExecutionResult.ExecutionStatus.FAILED);
  }

  private ExecutionResult<MessageResponse> authorizeForQuestion(AJEntityAssessment assessment) {
    // return new ExecutionResult<>(null,
    // ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    try {
      long count = Base
          .count(AJEntityQuestion.TABLE_QUESTION, AJEntityQuestion.QUESTION_FOR_ADD_FILTER,
              context.questionId(), context.userId());
      if (count == 1) {
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
      }
    } catch (DBException e) {
      LOGGER.error("Error querying question '{}' availability for associating in assessment '{}'",
          context.questionId(), context.assessmentId(), e);
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(
        MessageResponseFactory
            .createInvalidRequestResponse(
                RESOURCE_BUNDLE.getString("question.not.available.for.association")),
        ExecutionResult.ExecutionStatus.FAILED);
  }
}
