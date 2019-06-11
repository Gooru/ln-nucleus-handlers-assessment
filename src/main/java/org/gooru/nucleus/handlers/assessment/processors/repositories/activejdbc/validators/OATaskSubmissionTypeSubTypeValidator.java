package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

public final class OATaskSubmissionTypeSubTypeValidator {

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private static final String UPLOADED = "uploaded";
  private static final String REMOTE = "remote";
  private static final List<String> VALID_TYPES = Collections
      .unmodifiableList(Arrays.asList(UPLOADED, REMOTE));
  private static final List<String> VALID_SUBTYPES_UPLOADED = Collections
      .unmodifiableList(Arrays.asList("image", "pdf", "presentation", "document", "others"));
  private static final List<String> VALID_SUBTYPES_REMOTE = Collections.singletonList("url");

  private OATaskSubmissionTypeSubTypeValidator() {
    throw new AssertionError();
  }

  public static void validateTypeSubtypeValues(String type, String subtype) {
    validateType(type);
    validateTypeSubtypeAssocation(type, subtype);
  }

  private static void validateTypeSubtypeAssocation(String type, String subtype) {
    switch (type) {
      case UPLOADED:
        if (VALID_SUBTYPES_UPLOADED.contains(subtype)) {
          return;
        }
      case REMOTE:
        if (VALID_SUBTYPES_REMOTE.contains(subtype)) {
          return;
        }
    }
    throw new MessageResponseWrapperException(
        MessageResponseFactory
            .createInvalidRequestResponse("subtype:" + RESOURCE_BUNDLE.getString("invalid.value")));
  }

  private static void validateType(String type) {
    if (!VALID_TYPES.contains(type)) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse("type:" + RESOURCE_BUNDLE.getString("invalid.value")));
    }
  }

}
