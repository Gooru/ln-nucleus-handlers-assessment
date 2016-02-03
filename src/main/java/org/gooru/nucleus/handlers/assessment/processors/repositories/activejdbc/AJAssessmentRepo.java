package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.transactions.TransactionExecutor;
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
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildCreateAssessmentHandler(context));
  }

  @Override
  public MessageResponse updateAssessment() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildUpdateAssessmentHandler(context));
  }

  @Override
  public MessageResponse deleteAssessment() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildDeleteAssessmentHandler(context));
  }

  @Override
  public MessageResponse fetchAssessment() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildFetchAssessmentHandler(context));

  }

  @Override
  public MessageResponse reorderQuestionInAssessment() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildReorderAssessmentHandler(context));
  }

  @Override
  public MessageResponse addQuestionToAssessment() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildAddQuestionToAssessmentHandler(context));

  }

  @Override
  public MessageResponse updateCollaborator() {
    return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildUpdateCollaboratorHandler(context));
  }

}
