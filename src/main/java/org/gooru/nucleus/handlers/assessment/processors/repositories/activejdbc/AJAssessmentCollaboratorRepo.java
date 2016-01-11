package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentCollaboratorRepo;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
public class AJAssessmentCollaboratorRepo implements AssessmentCollaboratorRepo {
  private final ProcessorContext context;

  public AJAssessmentCollaboratorRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse fetchCollaborator() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildFetchCollaboratorHandler(context));

  }

  @Override
  public MessageResponse updateCollaborator() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildUpdateCollaboratorHandler(context));
  }
}
