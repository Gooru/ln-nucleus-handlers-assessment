package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskdelete;

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

public class OATaskDeleteHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OATaskDeleteHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final OAProcessorContext context;
  private AJEntityAssessment offlineActivity;
  private AJEntityOATask oaTask;
  private Long oaTaskId;


  public OATaskDeleteHandler(ProcessorContext context) {
    this.context = new OAProcessorContext(context);
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {
      SanityValidators.validateUser(context);
      SanityValidators.validateOAId(context);
      SanityValidators.validateOATaskId(context);
      oaTaskId = Long.parseLong(context.oaTaskId());
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
      oaTask = OATaskDao.fetchOATaskByOAIdAndTaskId(context.oaId(), oaTaskId);
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return AuthorizerBuilder.buildDeleteAuthorizer(this.context.processorContext())
        .authorize(offlineActivity);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    OATaskDao.deleteTaskById(oaTaskId);
    return new ExecutionResult<>(
        MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("deleted"),
            EventBuilderFactory.getUpdateOAEventBuilder(context.oaId())),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
