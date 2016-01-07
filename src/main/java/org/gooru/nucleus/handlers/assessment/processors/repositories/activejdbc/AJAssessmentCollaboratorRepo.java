package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentCollaboratorRepo;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public class AJAssessmentCollaboratorRepo implements AssessmentCollaboratorRepo {
  private final ProcessorContext context;

  public AJAssessmentCollaboratorRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse fetchCollaborator() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");

  }

  @Override
  public MessageResponse updateCollaborator() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }
}
