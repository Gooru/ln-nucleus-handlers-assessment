package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskcreate;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityOATask;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OATaskDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OfflineActivityDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.SanityValidators;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public class OATaskCreateHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OATaskCreateHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final OAProcessorContext context;
  private AJEntityAssessment offlineActivity;
  private AJEntityOATask oaTask;
  private OATaskCreateCommand command;

  public OATaskCreateHandler(ProcessorContext context) {
    this.context = new OAProcessorContext(context);
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {
      SanityValidators.validateUser(context);
      SanityValidators.validateOAId(context);
      command = OATaskCreateCommand.build(context);
    } catch (MessageResponseWrapperException mrwe) {
      return new ExecutionResult<>(mrwe.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    try {
      offlineActivity = OfflineActivityDao.fetchOfflineActivityById(context.oaId());
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return AuthorizerBuilder.buildUpdateAuthorizer(this.context.processorContext())
        .authorize(offlineActivity);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    try {
      Long taskId = OATaskDao.createTask(command);
      return new ExecutionResult<>(
          MessageResponseFactory.createCreatedResponse(String.valueOf(taskId),
              EventBuilderFactory.getUpdateOAEventBuilder(context.oaId())),
          ExecutionResult.ExecutionStatus.SUCCESSFUL);
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
