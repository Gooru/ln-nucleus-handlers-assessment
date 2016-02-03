package org.gooru.nucleus.handlers.assessment.processors.repositories;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.AJRepoBuilder;

/**
 * Created by ashish on 7/1/16.
 */
public final class RepoBuilder {

  private RepoBuilder() {
    throw new AssertionError();
  }

  public static AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return AJRepoBuilder.buildAssessmentRepo(context);
  }

}
