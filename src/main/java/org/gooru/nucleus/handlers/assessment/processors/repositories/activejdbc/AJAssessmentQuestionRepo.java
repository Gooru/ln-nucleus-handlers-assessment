package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentQuestionRepo;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.transactions.TransactionExecutor;
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
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildAddQuestionToAssessmentHandler(context));

  }

  @Override
  public MessageResponse removeQuestionFromAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildRemoveQuestionFromAssessmentHandler(context));

  }

  @Override
  public MessageResponse copyQuestionToAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildCopyQuestionToAssessmentHandler(context));

  }

  @Override
  public MessageResponse updateQuestionInAssessment() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildUpdateQuestionInAssessmentHandler(context));

  }
}
