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
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_UPDATE:
          result = processAssessmentUpdateQuestion();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_REMOVE:
          result = processAssessmentRemoveQuestion();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_COPY:
          result = processAssessmentCopyQuestion();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_QUESTION_REORDER:
          result = processAssessmentQuestionReorder();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_GET:
          result = processAssessmentCollaboratorGet();
          break;
        case MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE:
          result = processAssessmentCollaboratorUpdate();
          break;
        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          return MessageResponseFactory.createInvalidRequestResponse("Invalid operation");
      }
      return result;
    } catch (Throwable e) {
      LOGGER.error("Unhandled exception in processing", e);
      return MessageResponseFactory.createInternalErrorResponse();
    }
  }

  private MessageResponse processAssessmentUpdateQuestion() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty() || context.questionId() == null || context.questionId().isEmpty()) {
      LOGGER.error("Invalid request, either assessment id or question id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment/question id");
    }

    return new RepoBuilder().buildAssessmentQuestionRepo(context).updateQuestionInAssessment();
  }

  private MessageResponse processAssessmentQuestionReorder() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");

    }
    return new RepoBuilder().buildAssessmentRepo(context).reorderQuestionInAssessment();
  }

  private MessageResponse processAssessmentCopyQuestion() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentQuestionRepo(context).copyQuestionToAssessment();
  }

  private MessageResponse processAssessmentCollaboratorUpdate() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentCollaboratorRepo(context).updateCollaborator();
  }

  private MessageResponse processAssessmentCollaboratorGet() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentCollaboratorRepo(context).fetchCollaborator();
  }

  private MessageResponse processAssessmentRemoveQuestion() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty() || context.questionId() == null || context.questionId().isEmpty()) {
      LOGGER.error("Invalid request, either assessment id or question id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment/question id");
    }
    return new RepoBuilder().buildAssessmentQuestionRepo(context).removeQuestionFromAssessment();
  }

  private MessageResponse processAssessmentAddQuestion() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentQuestionRepo(context).addQuestionToAssessment();
  }

  private MessageResponse processAssessmentDelete() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentRepo(context).deleteAssessment();
  }

  private MessageResponse processAssessmentUpdate() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentRepo(context).updateAssessment();
  }

  private MessageResponse processAssessmentGet() {
    ProcessorContext context = createContext();
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.error("Invalid request, assessment id not available. Aborting");
      return MessageResponseFactory.createInvalidRequestResponse("Invalid assessment id");
    }
    return new RepoBuilder().buildAssessmentRepo(context).fetchAssessment();
  }

  private MessageResponse processAssessmentCreate() {
    ProcessorContext context = createContext();

    return new RepoBuilder().buildAssessmentRepo(context).createAssessment();
  }

  private ProcessorContext createContext() {
    String assessmentId = message.headers().get(MessageConstants.ASSESSMENT_ID);
    String questionId = message.headers().get(MessageConstants.QUESTION_ID);

    return new ProcessorContext(userId, prefs, request, assessmentId, questionId);
  }

  private ExecutionResult<MessageResponse> validateAndInitialize() {
    if (message == null || !(message.body() instanceof JsonObject)) {
      LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    userId = ((JsonObject) message.body()).getString(MessageConstants.MSG_USER_ID);
    if (userId == null) {
      LOGGER.error("Invalid user id passed. Not authorized.");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }
    prefs = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_KEY_PREFS);
    request = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_HTTP_BODY);

    if (prefs == null || prefs.isEmpty()) {
      LOGGER.error("Invalid preferences obtained, probably not authorized properly");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    if (request == null) {
      LOGGER.error("Invalid JSON payload on Message Bus");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    // All is well, continue processing
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }
}
