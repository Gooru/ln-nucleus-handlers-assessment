package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

/**
 * Created by ashish on 7/1/16.
 */
class AJAssessmentRepo implements AssessmentRepo {

  private final ProcessorContext context;

  public AJAssessmentRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse createAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildCreateAssessmentHandler(context));
  }

  @Override
  public MessageResponse updateAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildUpdateAssessmentHandler(context));
  }

  @Override
  public MessageResponse deleteAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildDeleteAssessmentHandler(context));
  }

  @Override
  public MessageResponse fetchAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildFetchAssessmentHandler(context));

  }

  @Override
  public MessageResponse reorderQuestionInAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildReorderAssessmentHandler(context));
  }

  @Override
  public MessageResponse addQuestionToAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildAddQuestionToAssessmentHandler(context));

  }

  @Override
  public MessageResponse updateCollaborator() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildUpdateCollaboratorHandler(context));
  }

  @Override
  public MessageResponse deleteExternalAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildDeleteExternalAssessmentHandler(context));
  }

  @Override
  public MessageResponse updateExternalAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildUpdateExternalAssessmentHandler(context));
  }

  @Override
  public MessageResponse fetchExternalAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildFetchExternalAssessmentHandler(context));
  }

  @Override
  public MessageResponse createExternalAssessment() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildCreateExternalAssessmentHandler(context));
  }

  @Override
  public MessageResponse getAssessmentMasteryAccrual() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildGetMasteryAccrualForAssessmentHandler(context));
  }

  @Override
  public MessageResponse oaCreate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOACreateHandler(context));
  }

  @Override
  public MessageResponse oaDelete() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOADeleteHandler(context));
  }

  @Override
  public MessageResponse oaFetchDetail() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOAFetchDetailHandler(context));
  }

  @Override
  public MessageResponse oaFetchSummary() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOAFetchSummaryHandler(context));
  }

  @Override
  public MessageResponse oaRefCreate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOARefCreateHandler(context));
  }

  @Override
  public MessageResponse oaRefDelete() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOARefDeleteHandler(context));
  }

  @Override
  public MessageResponse oaRefRubricStudentAssociate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOARubricStudentAssociateHandler(context));
  }

  @Override
  public MessageResponse oaRefRubricTeacherAssociate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOARubricTeacherAssociateHandler(context));
  }

  @Override
  public MessageResponse oaTaskCreate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOATaskCreateHandler(context));
  }

  @Override
  public MessageResponse oaTaskDelete() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOATaskDeleteHandler(context));
  }

  @Override
  public MessageResponse oaTaskSubmissionCreate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOATaskSubmissionCreateHandler(context));
  }

  @Override
  public MessageResponse oaTaskSubmissionDelete() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOATaskSubmissionDeleteHandler(context));
  }

  @Override
  public MessageResponse oaTaskUpdate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOATaskUpdateHandler(context));
  }

  @Override
  public MessageResponse oaUpdateCollaborator() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOAUpdateCollaboratorHandler(context));
  }

  @Override
  public MessageResponse oaUpdate() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOAUpdateHandler(context));
  }

  @Override
  public MessageResponse oaSubtypesList() {
    return TransactionExecutor
        .executeTransaction(DBHandlerBuilder.buildOASubtypesListHandler(context));
  }
}
