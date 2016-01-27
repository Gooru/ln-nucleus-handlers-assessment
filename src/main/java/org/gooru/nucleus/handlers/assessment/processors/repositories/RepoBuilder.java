package org.gooru.nucleus.handlers.assessment.processors.repositories;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.AJRepoBuilder;

/**
 * Created by ashish on 7/1/16.
 */
public class RepoBuilder {

  public AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildAssessmentRepo(context);
  }

}
