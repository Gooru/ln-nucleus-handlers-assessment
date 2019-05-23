package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import java.util.Map;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.CollaboratorHelper;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entitybuilders.EntityBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.SanityValidators;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 11/1/16.
 */
class UpdateCollaboratorHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCollaboratorHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;
  private AJEntityAssessment assessment;
  private JsonObject diffCollaborators;

  public UpdateCollaboratorHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    try {

      SanityValidators.validateUser(context);
      SanityValidators.validateAssessmentId(context.assessmentId());
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
      this.assessment = AssessmentDao.fetchAssessmentWithAuthorizerQuery(context.assessmentId());
      CollaboratorHelper.validateOAIsStandalone(assessment.getCourseId(), context.assessmentId());
      diffCollaborators = CollaboratorHelper.calculateDiffOfCollaborators(
          context.request().getJsonArray(AJEntityAssessment.COLLABORATOR),
          this.assessment.getCollaborators());

      return CollaboratorHelper.doAuthorization(context, assessment,
          CollaboratorHelper.getAddedCollaboratorsFromDiff(diffCollaborators));
    } catch (MessageResponseWrapperException e) {
      return new ExecutionResult<>(e.getMessageResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    AJEntityAssessment assessment = new AJEntityAssessment();
    assessment.setIdWithConverter(context.assessmentId());
    assessment.setModifierId(context.userId());
    // Now auto populate is done, we need to setup the converter machinery
    new EntityBuilder<AJEntityAssessment>() {
    }
        .build(assessment, context.request(), AssessmentDao.getConverterRegistry());

    boolean result = assessment.save();
    if (!result) {
      LOGGER.error("Assessment with id '{}' failed to save", context.assessmentId());
      if (assessment.hasErrors()) {
        Map<String, String> map = assessment.errors();
        JsonObject errors = new JsonObject();
        map.forEach(errors::put);
        return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
            ExecutionResult.ExecutionStatus.FAILED);
      }
    }
    return new ExecutionResult<>(MessageResponseFactory
        .createNoContentResponse(RESOURCE_BUNDLE.getString("updated"), EventBuilderFactory
            .getUpdateCollaboratorForAssessmentEventBuilder(context.assessmentId(),
                diffCollaborators)),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

}
