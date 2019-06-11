package org.gooru.nucleus.handlers.assessment.processors.exceptions;

import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

public class MessageResponseWrapperException extends RuntimeException {

  private final MessageResponse messageResponse;

  public MessageResponseWrapperException(MessageResponse messageResponse) {
    this.messageResponse = messageResponse;
  }


  public MessageResponse getMessageResponse() {
    return messageResponse;
  }
}
