package org.gooru.nucleus.handlers.assessment.processors.commands;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * @author ashish.
 */
class OASubtypesListProcessor extends AbstractCommandProcessor {

  public OASubtypesListProcessor(ProcessorContext context) {
    super(context);
  }

  @Override
  protected void setDeprecatedVersions() {

  }

  @Override
  protected MessageResponse processCommand() {

    return RepoBuilder.buildAssessmentRepo(context).oaSubtypesList();
  }
}
