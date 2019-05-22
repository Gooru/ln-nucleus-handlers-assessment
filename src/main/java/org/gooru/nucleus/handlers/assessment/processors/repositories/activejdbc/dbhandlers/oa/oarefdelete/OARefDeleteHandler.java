package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefdelete;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityOAReference;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OAReferenceDao;
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

public class OARefDeleteHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OARefDeleteHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final OAProcessorContext context;
  private AJEntityAssessment offlineActivity;
  private AJEntityOAReference oaReference;
  private Long oaRefId;


  public OARefDeleteHandler(ProcessorContext context) {
    this.context = new OAProcessorContext(context);
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {
      SanityValidators.validateUser(context);
      SanityValidators.validateOAId(context);
      SanityValidators.validateOARefId(context);
      oaRefId = Long.parseLong(context.oaRefId());
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
      oaReference = OAReferenceDao.fetchOARefByOAIdAndRefId(context.oaId(), oaRefId);
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    return AuthorizerBuilder.buildDeleteAuthorizer(this.context.processorContext())
        .authorize(offlineActivity);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    OAReferenceDao.deleteRefById(oaRefId);
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
