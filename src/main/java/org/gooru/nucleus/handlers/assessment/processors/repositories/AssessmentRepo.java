package org.gooru.nucleus.handlers.assessment.processors.repositories;

import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public interface AssessmentRepo {

  MessageResponse createAssessment();

  MessageResponse updateAssessment();

  MessageResponse deleteAssessment();

  MessageResponse fetchAssessment();

  MessageResponse reorderQuestionInAssessment();

  MessageResponse addQuestionToAssessment();

  MessageResponse updateCollaborator();

  MessageResponse deleteExternalAssessment();

  MessageResponse updateExternalAssessment();

  MessageResponse fetchExternalAssessment();

  MessageResponse createExternalAssessment();

  MessageResponse getAssessmentMasteryAccrual();

  MessageResponse oaCreate();

  MessageResponse oaDelete();

  MessageResponse oaFetchDetail();

  MessageResponse oaFetchSummary();

  MessageResponse oaRefCreate();

  MessageResponse oaRefDelete();

  MessageResponse oaRefRubricStudentAssociate();

  MessageResponse oaRefRubricTeacherAssociate();

  MessageResponse oaTaskCreate();

  MessageResponse oaTaskDelete();

  MessageResponse oaTaskSubmissionCreate();

  MessageResponse oaTaskSubmissionDelete();

  MessageResponse oaTaskUpdate();

  MessageResponse oaUpdateCollaborator();

  MessageResponse oaUpdate();
}
