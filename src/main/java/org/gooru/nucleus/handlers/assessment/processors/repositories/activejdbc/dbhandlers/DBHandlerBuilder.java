package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;

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

}
