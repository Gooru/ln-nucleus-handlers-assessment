package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentRepo;

/**
 * Created by ashish on 7/1/16.
 */
public class AJRepoBuilder {

  public AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return new AJAssessmentRepo(context);
  }


}
