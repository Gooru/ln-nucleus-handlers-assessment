package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.transactions.exceptionhandlers;

import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

public interface ExceptionHandler {
  
  ExecutionResult<MessageResponse> handleError(Throwable e);

}
