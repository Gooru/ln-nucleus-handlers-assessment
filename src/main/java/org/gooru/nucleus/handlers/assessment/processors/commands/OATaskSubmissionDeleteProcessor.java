package org.gooru.nucleus.handlers.assessment.processors.commands;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * @author ashish on 30/12/16.
 */
class OATaskSubmissionDeleteProcessor extends AbstractCommandProcessor {

  public OATaskSubmissionDeleteProcessor(ProcessorContext context) {
    super(context);
  }

  @Override
  protected void setDeprecatedVersions() {

  }

  @Override
  protected MessageResponse processCommand() {

    return RepoBuilder.buildAssessmentRepo(context).oaTaskSubmissionDelete();
  }
}
