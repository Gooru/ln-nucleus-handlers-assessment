package org.gooru.nucleus.handlers.assessment.processors;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.InvalidUserException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  private Message<Object> message;
  String userId;
  JsonObject prefs;
  JsonObject request;
  
  public MessageProcessor(Message<Object> message) {
    this.message = message;
  }
  
  @Override
  public MessageResponse process() {
    MessageResponse result = null;
    try {
      if (message == null || !(message.body() instanceof JsonObject)) {
        LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
        return MessageResponseFactory.createInvalidRequestResponse();
      }
      
      final String msgOp = message.headers().get(MessageConstants.MSG_HEADER_OP);
      userId = ((JsonObject)message.body()).getString(MessageConstants.MSG_USER_ID);
      if (userId == null) {
        LOGGER.error("Invalid user id passed. Not authorized.");
        return MessageResponseFactory.createForbiddenResponse();
      }
      prefs = ((JsonObject)message.body()).getJsonObject(MessageConstants.MSG_KEY_PREFS);
      request = ((JsonObject)message.body()).getJsonObject(MessageConstants.MSG_HTTP_BODY);
      switch (msgOp) {
      case MessageConstants.MSG_OP_ASSESSMENT_CREATE:
        result = processAssessmentCreate();
        break;
      case MessageConstants.MSG_OP_ASSESSMENT_GET:
        result = processAssessmentGet();
        break;
      case MessageConstants.MSG_OP_ASSESSMENT_UPDATE:
        result = processAssessmentUpdate();
        break;
      case MessageConstants.MSG_OP_ASSESSMENT_DELETE:
      case MessageConstants.MSG_OP_ASSESSMENT_ADD_QUESTION:
      case MessageConstants.MSG_OP_ASSESSMENT_COPY_QUESTION:
      case MessageConstants.MSG_OP_ASSESSMENT_REMOVE_QUESTION:
      case MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_GET:
      case MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE:
        break;
      default:
        LOGGER.error("Invalid operation type passed in, not able to handle");
        return MessageResponseFactory.createInvalidRequestResponse();
      }
      return result;
    } catch (Throwable e) {
      return MessageResponseFactory.createInternalErrorResponse();
    }
  }

  private MessageResponse processAssessmentUpdate() {
    // TODO Auto-generated method stub
    String assessmentId = message.headers().get(MessageConstants.ASSESSMENT_ID);
    
    return null;    
  }

  private MessageResponse processAssessmentGet() {
    // TODO Auto-generated method stub
    String assessmentId = message.headers().get(MessageConstants.ASSESSMENT_ID);
    
    return null;
  }

  private MessageResponse processAssessmentCreate() {
    // TODO Auto-generated method stub
    
    return null;    
  }

}
