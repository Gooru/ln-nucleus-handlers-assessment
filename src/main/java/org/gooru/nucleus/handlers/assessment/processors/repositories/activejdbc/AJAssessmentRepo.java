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
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildCreateAssessmentHandler(context));
  }

  @Override
  public MessageResponse updateAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildUpdateAssessmentHandler(context));
  }

  @Override
  public MessageResponse deleteAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildDeleteAssessmentHandler(context));
  }

  @Override
  public MessageResponse fetchAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildFetchAssessmentHandler(context));

  }

  @Override
  public MessageResponse reorderQuestionInAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildReorderAssessmentHandler(context));
  }

  @Override
  public MessageResponse updateCollaborator() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildUpdateCollaboratorHandler(context));
  }
  
  @Override
  public MessageResponse addQuestionToAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildAddQuestionToAssessmentHandler(context));

  }

}
