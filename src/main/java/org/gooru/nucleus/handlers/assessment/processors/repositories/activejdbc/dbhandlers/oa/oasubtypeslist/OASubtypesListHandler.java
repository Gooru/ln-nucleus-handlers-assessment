package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oasubtypeslist;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.SanityValidators;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;

/**
 * @author ashish.
 */

public class OASubtypesListHandler implements DBHandler {

  private final ProcessorContext context;

  public OASubtypesListHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {
      SanityValidators.validateUser(context);
    } catch (MessageResponseWrapperException mrwe) {
      return new ExecutionResult<>(mrwe.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildAuthenticatedAuthorizer(this.context)
        .authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    List names = Base.firstColumn("select name from collection_subformat_type");
    JsonArray values = new JsonArray();
    for (Object name : names) {
      values.add((String) name);
    }
    JsonObject result = new JsonObject().put("collection_subformat_type", values);
    return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }
}
