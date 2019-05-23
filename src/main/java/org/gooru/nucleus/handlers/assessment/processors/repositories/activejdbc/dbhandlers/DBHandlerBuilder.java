package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oacollaboratorupdate.OAUpdateCollaboratorHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oacreate.OACreateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oadelete.OADeleteHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oafetchdetail.OAFetchDetailHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oafetchsummary.OAFetchSummaryHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefcreate.OARefCreateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefdelete.OARefDeleteHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarubricstudent.OARubricStudentAssociateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarubricteacher.OARubricTeacherAssociateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oasubtypeslist.OASubtypesListHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskcreate.OATaskCreateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskdelete.OATaskDeleteHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oatasksubmissioncreate.OATaskSubmissionCreateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oatasksubmissiondelete.OATaskSubmissionDeleteHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskupdate.OATaskUpdateHandler;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oaupdate.OAUpdateHandler;

/**
 * Created by ashish on 11/1/16.
 */
public final class DBHandlerBuilder {

  private DBHandlerBuilder() {
    throw new AssertionError();
  }

  public static DBHandler buildReorderAssessmentHandler(ProcessorContext context) {
    return new ReorderAssessmentHandler(context);
  }

  public static DBHandler buildFetchAssessmentHandler(ProcessorContext context) {
    return new FetchAssessmentHandler(context);

  }

  public static DBHandler buildDeleteAssessmentHandler(ProcessorContext context) {
    return new DeleteAssessmentHandler(context);
  }

  public static DBHandler buildUpdateAssessmentHandler(ProcessorContext context) {
    return new UpdateAssessmentHandler(context);

  }

  public static DBHandler buildCreateAssessmentHandler(ProcessorContext context) {
    return new CreateAssessmentHandler(context);

  }

  public static DBHandler buildAddQuestionToAssessmentHandler(ProcessorContext context) {
    return new AddQuestionToAssessmentHandler(context);

  }

  public static DBHandler buildUpdateCollaboratorHandler(ProcessorContext context) {
    return new UpdateCollaboratorHandler(context);

  }

  public static DBHandler buildDeleteExternalAssessmentHandler(ProcessorContext context) {
    return new DeleteExternalAssessmentHandler(context);
  }

  public static DBHandler buildUpdateExternalAssessmentHandler(ProcessorContext context) {
    return new UpdateExternalAssessmentHandler(context);
  }

  public static DBHandler buildFetchExternalAssessmentHandler(ProcessorContext context) {
    return new FetchExternalAssessmentHandler(context);
  }

  public static DBHandler buildCreateExternalAssessmentHandler(ProcessorContext context) {
    return new CreateExternalAssessmentHandler(context);
  }

  public static DBHandler buildGetMasteryAccrualForAssessmentHandler(ProcessorContext context) {
    return new GetMasteryAccrualForAssessmentHandler(context);
  }

  public static DBHandler buildOACreateHandler(ProcessorContext context) {
    return new OACreateHandler(context);
  }

  public static DBHandler buildOADeleteHandler(ProcessorContext context) {
    return new OADeleteHandler(context);
  }

  public static DBHandler buildOAFetchDetailHandler(ProcessorContext context) {
    return new OAFetchDetailHandler(context);
  }

  public static DBHandler buildOAFetchSummaryHandler(ProcessorContext context) {
    return new OAFetchSummaryHandler(context);
  }

  public static DBHandler buildOARefCreateHandler(ProcessorContext context) {
    return new OARefCreateHandler(context);
  }

  public static DBHandler buildOARefDeleteHandler(ProcessorContext context) {
    return new OARefDeleteHandler(context);
  }

  public static DBHandler buildOARubricStudentAssociateHandler(ProcessorContext context) {
    return new OARubricStudentAssociateHandler(context);
  }

  public static DBHandler buildOARubricTeacherAssociateHandler(ProcessorContext context) {
    return new OARubricTeacherAssociateHandler(context);
  }

  public static DBHandler buildOATaskCreateHandler(ProcessorContext context) {
    return new OATaskCreateHandler(context);
  }

  public static DBHandler buildOATaskDeleteHandler(ProcessorContext context) {
    return new OATaskDeleteHandler(context);
  }

  public static DBHandler buildOATaskSubmissionCreateHandler(ProcessorContext context) {
    return new OATaskSubmissionCreateHandler(context);
  }

  public static DBHandler buildOATaskSubmissionDeleteHandler(ProcessorContext context) {
    return new OATaskSubmissionDeleteHandler(context);
  }

  public static DBHandler buildOATaskUpdateHandler(ProcessorContext context) {
    return new OATaskUpdateHandler(context);
  }

  public static DBHandler buildOAUpdateCollaboratorHandler(ProcessorContext context) {
    return new OAUpdateCollaboratorHandler(context);
  }

  public static DBHandler buildOAUpdateHandler(ProcessorContext context) {
    return new OAUpdateHandler(context);
  }

  public static DBHandler buildOASubtypesListHandler(ProcessorContext context) {
    return new OASubtypesListHandler(context);
  }
}
