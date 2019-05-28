package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter;

import io.vertx.core.json.JsonObject;
import java.util.Map;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Model;

/**
 * @author ashish.
 */

public final class ModelErrorFormatter {

  private ModelErrorFormatter() {
    throw new AssertionError();
  }

  public static ExecutionResult<MessageResponse> formattedErrorResponse(Model model) {
    if (model.hasErrors()) {
      Map<String, String> map = model.errors();
      JsonObject errors = new JsonObject();
      map.forEach(errors::put);
      return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return null;
  }

  public static JsonObject formattedError(Model model) {
    if (model.hasErrors()) {
      Map<String, String> map = model.errors();
      JsonObject errors = new JsonObject();
      map.forEach(errors::put);
      return errors;
    }
    return null;
  }

}
