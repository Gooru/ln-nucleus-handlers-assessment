package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;

/**
 * Created by ashish on 11/1/16.
 */
public class DBHandlerBuilder {
  public DBHandler buildReorderAssessmentHandler(ProcessorContext context) {
    return new ReorderAssessmentHandler(context);
  }

  public DBHandler buildFetchAssessmentHandler(ProcessorContext context) {
    return new FetchAssessmentHandler(context);

  }

  public DBHandler buildDeleteAssessmentHandler(ProcessorContext context) {
    return new DeleteAssessmentHandler(context);
  }

  public DBHandler buildUpdateAssessmentHandler(ProcessorContext context) {
    return new UpdateAssessmentHandler(context);

  }

  public DBHandler buildCreateAssessmentHandler(ProcessorContext context) {
    return new CreateAssessmentHandler(context);

  }

  public DBHandler buildAddQuestionToAssessmentHandler(ProcessorContext context) {
    return new AddQuestionToAssessmentHandler(context);

  }

  public DBHandler buildRemoveQuestionFromAssessmentHandler(ProcessorContext context) {
    return new RemoveQuestionFromAssessmentHandler(context);

  }

  public DBHandler buildCopyQuestionToAssessmentHandler(ProcessorContext context) {
    return new CopyQuestionToAssessmentHandler(context);

  }

  public DBHandler buildUpdateQuestionInAssessmentHandler(ProcessorContext context) {
    return new UpdateQuestionInAssessmentHandler(context);

  }

  public DBHandler buildUpdateCollaboratorHandler(ProcessorContext context) {
    return new UpdateCollaboratorHandler(context);

  }

  public DBHandler buildFetchCollaboratorHandler(ProcessorContext context) {
    return new FetchCollaboratorHandler(context);

  }
}
