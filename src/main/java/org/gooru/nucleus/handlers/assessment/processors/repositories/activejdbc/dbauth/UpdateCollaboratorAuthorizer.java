package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 31/1/16.
 */
class UpdateCollaboratorAuthorizer implements Authorizer<AJEntityAssessment> {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(Authorizer.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  UpdateCollaboratorAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityAssessment assessment) {
    String ownerId = assessment.getOwnerId();
    if (context.userId().equalsIgnoreCase(ownerId)) {
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }
    LOGGER.warn("User: '{}' is not owner of assessment: '{}'", context.userId(),
        context.assessmentId());
    return new ExecutionResult<>(
        MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
        ExecutionResult.ExecutionStatus.FAILED);
  }
}
