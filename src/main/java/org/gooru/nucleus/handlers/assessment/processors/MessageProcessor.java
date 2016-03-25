package org.gooru.nucleus.handlers.assessment.processors;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;
import java.util.UUID;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final Message<Object> message;
  private String userId;
  private JsonObject prefs;
  private JsonObject request;

  public MessageProcessor(Message<Object> message) {
    this.message = message;
  }

  @Override
  public MessageResponse process() {
    MessageResponse result;
    try {
      // Validate the message itself
      ExecutionResult<MessageResponse> validateResult = validateAndInitialize();
      if (validateResult.isCompleted()) {
        return validateResult.result();
      }

      final String msgOp = message.headers().get(MessageConstants.MSG_HEADER_OP);
      switch (msgOp) {
        case MessageConstants.MSG_OP_ASSESSMENT_GET:
          result = processAssessmentGet();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_CREATE:
          result = processAssessmentCreate();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_UPDATE:
          result = processAssessmentUpdate();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_DELETE:
          result = processAssessmentDelete();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_ADD:
          result = processAssessmentAddQuestion();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_REORDER:
          result = processAssessmentQuestionReorder();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE:
          result = processAssessmentCollaboratorUpdate();
          break;
        case MessageConstants.MSG_OP_EXT_ASSESSMENT_GET:
          result = processExternalAssessmentGet();
          break;
        case MessageConstants.MSG_OP_EXT_ASSESSMENT_CREATE:
          result = processExternalAssessmentCreate();
          break;
        case MessageConstants.MSG_OP_EXT_ASSESSMENT_UPDATE:
          result = processExternalAssessmentUpdate();
          break;
        case MessageConstants.MSG_OP_EXT_ASSESSMENT_DELETE:
          result = processExternalAssessmentDelete();
          break;
        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.operation"));
      }
      return result;
    } catch (Throwable e) {
      LOGGER.error("Unhandled exception in processing", e);
      return MessageResponseFactory.createInternalErrorResponse();
    }
  }

  private MessageResponse processAssessmentQuestionReorder() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).reorderQuestionInAssessment();
  }

  private MessageResponse processAssessmentCollaboratorUpdate() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).updateCollaborator();
  }

  private MessageResponse processAssessmentAddQuestion() {
    ProcessorContext context = createContext();
    if (!validateContext(context, true)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.question.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).addQuestionToAssessment();
  }

  private MessageResponse processAssessmentDelete() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).deleteAssessment();
  }

  private MessageResponse processAssessmentUpdate() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).updateAssessment();
  }

  private MessageResponse processAssessmentGet() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).fetchAssessment();
  }

  private MessageResponse processAssessmentCreate() {
    ProcessorContext context = createContext();

    return RepoBuilder.buildAssessmentRepo(context).createAssessment();
  }

  private MessageResponse processExternalAssessmentDelete() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).deleteExternalAssessment();
  }

  private MessageResponse processExternalAssessmentUpdate() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).updateExternalAssessment();
  }

  private MessageResponse processExternalAssessmentGet() {
    ProcessorContext context = createContext();
    if (!validateContext(context)) {
      return MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).fetchExternalAssessment();
  }

  private MessageResponse processExternalAssessmentCreate() {
    ProcessorContext context = createContext();

    return RepoBuilder.buildAssessmentRepo(context).createExternalAssessment();
  }

  private ProcessorContext createContext() {
    String assessmentId = message.headers().get(MessageConstants.ASSESSMENT_ID);
    String questionId = request.getString(MessageConstants.ID);

    return new ProcessorContext(userId, prefs, request, assessmentId, questionId);
  }

  private ExecutionResult<MessageResponse> validateAndInitialize() {
    if (message == null || !(message.body() instanceof JsonObject)) {
      LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.message")),
        ExecutionResult.ExecutionStatus.FAILED);
    }

    userId = ((JsonObject) message.body()).getString(MessageConstants.MSG_USER_ID);
    if (!validateUser(userId)) {
      LOGGER.error("Invalid user id passed. Not authorized.");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("missing.user")),
        ExecutionResult.ExecutionStatus.FAILED);
    }

    prefs = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_KEY_PREFS);
    request = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_HTTP_BODY);

    if (prefs == null || prefs.isEmpty()) {
      LOGGER.error("Invalid preferences obtained, probably not authorized properly");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("missing.preferences")),
        ExecutionResult.ExecutionStatus.FAILED);
    }

    if (request == null) {
      LOGGER.error("Invalid JSON payload on Message Bus");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.payload")),
        ExecutionResult.ExecutionStatus.FAILED);
    }

    // All is well, continue processing
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }


  private boolean validateContext(ProcessorContext context) {
    return validateContext(context, false);
  }

  private boolean validateContext(ProcessorContext context, boolean shouldHaveQuestion) {
    if (!validateId(context.assessmentId())) {
      LOGGER.error("Invalid request, assessment id not available/incorrect format. Aborting");
      return false;
    }
    if (shouldHaveQuestion) {
      if (!validateId(context.questionId())) {
        LOGGER.error("Invalid request, question id not available/incorrect format. Aborting");
        return false;
      }
    }
    return true;
  }


  private boolean validateUser(String userId) {
    return !(userId == null || userId.isEmpty()) && (userId.equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS) || validateUuid(userId));
  }

  private boolean validateId(String id) {
    return !(id == null || id.isEmpty()) && validateUuid(id);
  }

  private boolean validateUuid(String uuidString) {
    try {
      UUID uuid = UUID.fromString(uuidString);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (Exception e) {
      return false;
    }
  }

}
