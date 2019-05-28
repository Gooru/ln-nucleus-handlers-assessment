package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import java.util.Map;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 25/3/16.
 */
class DeleteExternalAssessmentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DeleteExternalAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;

  public DeleteExternalAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment id");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createNotFoundResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty()
        || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous user attempting to delete assessment");
      return new ExecutionResult<>(
          MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    // Fetch the assessment where type is external_assessment and it is not
    // deleted already and id is specified id

    LazyList<AJEntityAssessment> assessments = AJEntityAssessment
        .findBySQL(AssessmentDao.AUTHORIZER_QUERY,
            AJEntityAssessment.ASSESSMENT_EXTERNAL, context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(
          MessageResponseFactory
              .createNotFoundResponse(
                  RESOURCE_BUNDLE.getString("assessment.id") + context.assessmentId()),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    AJEntityAssessment assessment = assessments.get(0);
    // Log a warning is assessment to be deleted is published
    if (assessment.getPublishDate() != null) {
      LOGGER.warn("Assessment with id '{}' is published assessment and is being deleted",
          context.assessmentId());
    }
    return AuthorizerBuilder.buildDeleteAuthorizer(this.context).authorize(assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // Update assessment, we need to set the deleted flag and user who is
    // deleting it but We do not reset the sequence id right now
    AJEntityAssessment assessmentToDelete = new AJEntityAssessment();
    assessmentToDelete.setIdWithConverter(context.assessmentId());
    assessmentToDelete.setIsDeleted(true);
    assessmentToDelete.setModifierId(context.userId());

    boolean result = assessmentToDelete.save();
    if (!result) {
      LOGGER.error("Assessment with id '{}' failed to delete", context.assessmentId());
      if (assessmentToDelete.hasErrors()) {
        Map<String, String> map = assessmentToDelete.errors();
        JsonObject errors = new JsonObject();
        map.forEach(errors::put);
        return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
            ExecutionResult.ExecutionStatus.FAILED);
      }
    }
    return new ExecutionResult<>(
        MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("deleted"),
            EventBuilderFactory.getDeleteExAssessmentEventBuilder(context.assessmentId())),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
