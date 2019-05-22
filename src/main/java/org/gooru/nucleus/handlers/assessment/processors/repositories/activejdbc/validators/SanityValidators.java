package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators;

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
    validateUser(context.userId());
  }

  private static void validateUser(String userId) {
    if ((userId == null) || userId.isEmpty() || MessageConstants.MSG_USER_ANONYMOUS
        .equalsIgnoreCase(userId)) {
      LOGGER.warn("Invalid user");
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")));
    }
  }

  public static void validateUser(OAProcessorContext context) {
    validateUser(context.userId());
  }


  public static void validateOAId(OAProcessorContext context) {
    validateValuePresence(context.oaId(), "missing.oa.id");
  }

  public static void validateOATaskId(OAProcessorContext context) {
    validateValuePresence(context.oaTaskId(), "missing.oatask.id");
  }

  public static void validateOATaskSubmissionId(OAProcessorContext context) {
    validateValuePresence(context.oaTaskSubmissionId(), "missing.oatasksubmission.id");
  }

  public static void validateOARefId(OAProcessorContext context) {
    validateValuePresence(context.oaRefId(), "missing.oaref.id");
    try {
      Long.parseLong(context.oaRefId());
    } catch (NumberFormatException e) {
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createInvalidRequestResponse(
              RESOURCE_BUNDLE.getString("invalid.value") + context.oaRefId()));
    }
  }

  public static void validateOARubricId(OAProcessorContext context) {
    validateValuePresence(context.oaRubricId(), "missing.oarubric.id");
  }

  private static void validateValuePresence(String value, String messageKey) {
    if (value == null || value.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createNotFoundResponse(RESOURCE_BUNDLE.getString(messageKey)));
    }
  }

}
