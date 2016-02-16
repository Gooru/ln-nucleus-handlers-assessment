package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entitybuilders.EntityBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by ashish on 11/1/16.
 */
class UpdateAssessmentHandler implements DBHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;

  public UpdateAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
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
      .validatePayload(context.request(), AJEntityAssessment.editFieldSelector(), AJEntityAssessment.getValidatorRegistry());
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
    AJEntityAssessment assessment = assessments.get(0);
    return AuthorizerBuilder.buildUpdateAuthorizer(this.context).authorize(assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    AJEntityAssessment assessment = new AJEntityAssessment();
    assessment.setIdWithConverter(context.assessmentId());
    assessment.setModifierId(context.userId());
    // Now auto populate is done, we need to setup the converter machinery
    new DefaultAJEntityAssessmentEntityBuilder().build(assessment, context.request(), AJEntityAssessment.getConverterRegistry());

    boolean result = assessment.save();
    if (!result) {
      LOGGER.error("Assessment with id '{}' failed to save", context.assessmentId());
      if (assessment.hasErrors()) {
        Map<String, String> map = assessment.errors();
        JsonObject errors = new JsonObject();
        map.forEach(errors::put);
        return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
      }
    }
    return new ExecutionResult<>(MessageResponseFactory
      .createNoContentResponse(RESOURCE_BUNDLE.getString("updated"), EventBuilderFactory.getUpdateAssessmentEventBuilder(context.assessmentId())),
      ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

  private static class DefaultPayloadValidator implements PayloadValidator {
  }

  private static class DefaultAJEntityAssessmentEntityBuilder implements EntityBuilder<AJEntityAssessment> {
  }
}
