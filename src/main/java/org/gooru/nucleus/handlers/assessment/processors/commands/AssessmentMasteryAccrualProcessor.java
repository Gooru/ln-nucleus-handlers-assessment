package org.gooru.nucleus.handlers.assessment.processors.commands;

import static org.gooru.nucleus.handlers.assessment.processors.utils.ValidationUtils.validateAssessmentMasteryAccrualContext;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

class AssessmentMasteryAccrualProcessor extends AbstractCommandProcessor {

  public AssessmentMasteryAccrualProcessor(ProcessorContext context) {
    super(context);
  }

  @Override
  protected void setDeprecatedVersions() {

  }

  @Override
  protected MessageResponse processCommand() {
    if (!validateAssessmentMasteryAccrualContext(context)) {
      return MessageResponseFactory
          .createInvalidRequestResponse(
              RESOURCE_BUNDLE.getString("invalid.assessment.id"));
    }
    return RepoBuilder.buildAssessmentRepo(context).getAssessmentMasteryAccrual();
  }

}
