package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefcreate;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * @author ashish.
 */

public class OARefCreateHandler implements DBHandler {

  private final ProcessorContext context;

  public OARefCreateHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    throw new AssertionError("Not Implemented");
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    throw new AssertionError("Not Implemented");
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    throw new AssertionError("Not Implemented");
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
