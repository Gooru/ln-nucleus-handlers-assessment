package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentQuestionRepo;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public class AJAssessmentQuestionRepo implements AssessmentQuestionRepo {
  private final ProcessorContext context;

  public AJAssessmentQuestionRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse addQuestionToAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");

  }

  @Override
  public MessageResponse removeQuestionFromAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public MessageResponse copyQuestionToAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");
  }

  @Override
  public MessageResponse updateQuestionInAssessment() {
    // TODO: Provide a concrete implementation
    throw new IllegalStateException("Not implemented yet");  }
}
