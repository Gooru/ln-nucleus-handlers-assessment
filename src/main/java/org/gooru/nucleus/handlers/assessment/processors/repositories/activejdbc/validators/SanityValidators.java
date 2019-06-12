package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators;

import io.vertx.core.json.JsonObject;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class SanityValidators {

  private static final Logger LOGGER = LoggerFactory.getLogger(SanityValidators.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private SanityValidators() {
    throw new AssertionError();
  }

  public static void validateUser(ProcessorContext context) {
    validateUser(context.userId(), true);
  }

  private static void validateUser(String userId, boolean allowAnonymous) {
    if ((userId == null) || userId.isEmpty() || (
        MessageConstants.MSG_USER_ANONYMOUS.equalsIgnoreCase(userId) && !allowAnonymous)) {
      LOGGER.warn("Invalid user");
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")));
    }
  }

  public static void validateUser(OAProcessorContext context) {
    validateUser(context.userId(), false);
  }

  public static void validateUserAllowAnonymous(OAProcessorContext context) {
    validateUser(context.userId(), true);
  }

  public static void validateAssessmentId(String assessmentId) {
    if (assessmentId == null || assessmentId.isEmpty()) {
      LOGGER.warn("Missing assessment id");
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")));
    }
  }

  public static void validateOAId(OAProcessorContext context) {
    validateValuePresence(context.oaId(), "missing.oa.id");
  }

  public static void validateOATaskId(OAProcessorContext context) {
    validateValuePresence(context.oaTaskId(), "missing.oatask.id");
    validateLong(context.oaTaskId());
  }

  public static void validateOATaskSubmissionId(OAProcessorContext context) {
    validateValuePresence(context.oaTaskSubmissionId(), "missing.oatasksubmission.id");
    validateLong(context.oaTaskSubmissionId());
  }

  public static void validateOARefId(OAProcessorContext context) {
    validateValuePresence(context.oaRefId(), "missing.oaref.id");
    validateLong(context.oaRefId());
  }

  public static void validatePayloadNotEmpty(JsonObject request) {
    if (request == null || request.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")));
    }
  }

  private static void validateLong(String value) {
    try {
      Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createInvalidRequestResponse(
              RESOURCE_BUNDLE.getString("invalid.value") + value));
    }
  }

  public static void validateOARubricId(OAProcessorContext context) {
    validateValuePresence(context.oaRubricId(), "missing.oarubric.id");
  }

  public static void validateWithDefaultPayloadValidator(JsonObject request,
      FieldSelector fieldSelector, ValidatorRegistry validatorRegistry) {
    JsonObject errors = new DefaultPayloadValidator()
        .validatePayload(request, fieldSelector, validatorRegistry);
    if (errors != null && !errors.isEmpty()) {
      LOGGER.warn("Validation errors for request");
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(errors));
    }
  }

  private static void validateValuePresence(String value, String messageKey) {
    if (value == null || value.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createNotFoundResponse(RESOURCE_BUNDLE.getString(messageKey)));
    }
  }

  private static class DefaultPayloadValidator implements PayloadValidator {

  }


}
