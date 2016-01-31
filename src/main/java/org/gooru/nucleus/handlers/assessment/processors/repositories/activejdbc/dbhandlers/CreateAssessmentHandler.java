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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ashish on 11/1/16.
 */
class CreateAssessmentHandler implements DBHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateAssessmentHandler.class);
  private final ProcessorContext context;
  private AJEntityAssessment assessment;


  public CreateAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty() || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous or invalid user attempting to create assessment");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("Not allowed"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Payload should not be empty
    if (context.request() == null || context.request().isEmpty()) {
      LOGGER.warn("Empty payload supplied to create assessment");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Empty payload"), ExecutionResult.ExecutionStatus.FAILED);
    }
    // Our validators should certify this
    JsonObject errors = new PayloadValidator() {
    }.validatePayload(context.request(), AJEntityAssessment.createFieldSelector(), AJEntityAssessment.getValidatorRegistry());
    if (errors != null && !errors.isEmpty()) {
      LOGGER.warn("Validation errors for request");
      return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors), ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    // Only thing to do here is to authorize
    return new AuthorizerBuilder().buildCreateAuthorizer(context).authorize(this.assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    AJEntityAssessment assessment = new AJEntityAssessment();
    // First time creation is standalone, no course exists. It will be associated later, if the need arises. So all user ids are same
    assessment.setModifierId(context.userId());
    assessment.setOwnerId(context.userId());
    assessment.setCreatorId(context.userId());
    assessment.setTypeAssessment();
    // Now auto populate is done, we need to setup the converter machinery
    new EntityBuilder<AJEntityAssessment>() {
    }.build(assessment, context.request(), AJEntityAssessment.getConverterRegistry());

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
      .createNoContentResponse("Created", EventBuilderFactory.getDeleteAssessmentEventBuilder(assessment.getString(AJEntityAssessment.ID))),
      ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

}
