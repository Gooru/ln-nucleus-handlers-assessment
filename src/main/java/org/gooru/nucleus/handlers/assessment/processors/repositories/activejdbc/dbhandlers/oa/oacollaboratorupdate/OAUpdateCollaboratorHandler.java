package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oacollaboratorupdate;

import io.vertx.core.json.JsonObject;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.CollaboratorHelper;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.OfflineActivityDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entitybuilders.EntityBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.SanityValidators;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public class OAUpdateCollaboratorHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAUpdateCollaboratorHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private AJEntityAssessment offlineActivity;
  private JsonObject diffCollaborators;
  private final OAProcessorContext context;


  public OAUpdateCollaboratorHandler(ProcessorContext context) {
    this.context = new OAProcessorContext(context);
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {

      SanityValidators.validateUser(context);
      SanityValidators.validateOAId(context);
      JsonObject request = context.request();
      SanityValidators.validatePayloadNotEmpty(request);
      SanityValidators.validateWithDefaultPayloadValidator(request,
          AssessmentDao.editCollaboratorFieldSelector(), AssessmentDao.getValidatorRegistry());
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
      CollaboratorHelper.validateOAIsStandalone(offlineActivity.getCourseId(), context.oaId());
      diffCollaborators = CollaboratorHelper.calculateDiffOfCollaborators(
          context.request().getJsonArray(AJEntityAssessment.COLLABORATOR),
          offlineActivity.getCollaborators());

      return CollaboratorHelper.doAuthorization(context.processorContext(), offlineActivity,
          CollaboratorHelper.getAddedCollaboratorsFromDiff(diffCollaborators));
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    AJEntityAssessment assessment = new AJEntityAssessment();
    assessment.setIdWithConverter(context.oaId());
    assessment.setModifierId(context.userId());

    new EntityBuilder<AJEntityAssessment>() {
    }
        .build(assessment, context.request(), AssessmentDao.getConverterRegistry());

    boolean result = assessment.save();
    if (!result) {
      LOGGER.error("Assessment with id '{}' failed to save", context.oaId());
      if (assessment.hasErrors()) {
        return ModelErrorFormatter.formattedErrorResponse(assessment);
      }
    }
    return new ExecutionResult<>(MessageResponseFactory
        .createNoContentResponse(RESOURCE_BUNDLE.getString("updated"), EventBuilderFactory
            .getUpdateCollaboratorForOAEventBuilder(context.oaId(), diffCollaborators)),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }


  @Override
  public boolean handlerReadOnly() {
    return false;
  }
}
