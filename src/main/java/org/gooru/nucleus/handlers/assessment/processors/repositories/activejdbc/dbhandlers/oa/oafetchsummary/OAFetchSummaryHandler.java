package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oafetchsummary;

import io.vertx.core.json.JsonObject;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.CollaboratorHelper;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OAReferenceDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OATaskDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OfflineActivityDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.SanityValidators;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public class OAFetchSummaryHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAFetchSummaryHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final OAProcessorContext context;
  private AJEntityAssessment offlineActivity;


  public OAFetchSummaryHandler(ProcessorContext context) {
    this.context = new OAProcessorContext(context);
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {
      SanityValidators.validateUser(context);
      SanityValidators.validateOAId(context);
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    } catch (MessageResponseWrapperException mrwe) {
      return new ExecutionResult<>(mrwe.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    try {
      offlineActivity = OfflineActivityDao.fetchOfflineActivityById(context.oaId());
      return AuthorizerBuilder.buildTenantAuthorizer(this.context.processorContext())
          .authorize(offlineActivity);
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    JsonObject response = new JsonObject(
        JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, OfflineActivityDao.FETCH_FIELD_LIST)
            .toJson(this.offlineActivity));

    response.put(OATaskDao.OA_TASKS, OATaskDao.fetchTasksForActivityAsJson(context.oaId()));
    response.put(OAReferenceDao.OA_REFERENCES,
        OAReferenceDao.fetchRefsForActivityAsJson(context.oaId()));
    response.put(AJEntityAssessment.COLLABORATOR,
        CollaboratorHelper.fetchCollaboratorsForAssessment(offlineActivity));

    return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(response),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);

  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }
}
