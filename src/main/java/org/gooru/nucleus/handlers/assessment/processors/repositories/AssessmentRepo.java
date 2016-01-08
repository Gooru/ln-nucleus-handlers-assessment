package org.gooru.nucleus.handlers.assessment.processors.repositories;

import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public interface AssessmentRepo {

  public MessageResponse createAssessment();
  public MessageResponse updateAssessment();
  public MessageResponse deleteAssessment();
  public MessageResponse fetchAssessment();
  public MessageResponse reorderQuestionInAssessment();
}
