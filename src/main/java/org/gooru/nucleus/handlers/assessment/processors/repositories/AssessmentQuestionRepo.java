package org.gooru.nucleus.handlers.assessment.processors.repositories;

import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public interface AssessmentQuestionRepo {
  MessageResponse addQuestionToAssessment();
  MessageResponse removeQuestionFromAssessment();
  MessageResponse copyQuestionToAssessment();
  MessageResponse updateQuestionInAssessment();
}
