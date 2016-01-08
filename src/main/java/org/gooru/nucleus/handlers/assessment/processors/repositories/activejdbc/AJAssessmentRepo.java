package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public class AJAssessmentRepo implements AssessmentRepo {
  private final ProcessorContext context;

  public AJAssessmentRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse createAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");

  }

  @Override
  public MessageResponse updateAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public MessageResponse deleteAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public MessageResponse fetchAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public MessageResponse reorderQuestionInAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");  }
}
