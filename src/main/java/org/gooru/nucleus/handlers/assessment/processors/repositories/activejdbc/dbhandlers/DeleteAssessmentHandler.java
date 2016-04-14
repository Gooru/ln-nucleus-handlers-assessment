package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import java.util.Map;
import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 11/1/16.
 */
class DeleteAssessmentHandler implements DBHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAssessmentHandler.class);
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
    private final ProcessorContext context;

    public DeleteAssessmentHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // There should be an assessment id present
        if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
            LOGGER.warn("Missing assessment id");
            return new ExecutionResult<>(
                MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // The user should not be anonymous
        if (context.userId() == null || context.userId().isEmpty()
            || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
            LOGGER.warn("Anonymous user attempting to delete assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);

    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        // Fetch the assessment where type is assessment and it is not deleted
        // already and id is specified id

        LazyList<AJEntityAssessment> assessments = AJEntityAssessment.findBySQL(AJEntityAssessment.AUTHORIZER_QUERY,
            AJEntityAssessment.ASSESSMENT, context.assessmentId(), false);
        // Assessment should be present in DB
        if (assessments.size() < 1) {
            LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
            return new ExecutionResult<>(
                MessageResponseFactory
                    .createNotFoundResponse(RESOURCE_BUNDLE.getString("assessment.id") + context.assessmentId()),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        AJEntityAssessment assessment = assessments.get(0);
        // Log a warning is assessment to be deleted is published
        if (assessment.getDate(AJEntityAssessment.PUBLISH_DATE) != null) {
            LOGGER.warn("Assessment with id '{}' is published assessment and is being deleted", context.assessmentId());
        }
        return AuthorizerBuilder.buildDeleteAuthorizer(this.context).authorize(assessment);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        // Update assessment, we need to set the deleted flag and user who is
        // deleting it but We do not reset the sequence id right now
        AJEntityAssessment assessmentToDelete = new AJEntityAssessment();
        assessmentToDelete.setIdWithConverter(context.assessmentId());
        assessmentToDelete.setBoolean(AJEntityAssessment.IS_DELETED, true);
        assessmentToDelete.setModifierId(context.userId());

        boolean result = assessmentToDelete.save();
        if (!result) {
            LOGGER.error("Assessment with id '{}' failed to delete", context.assessmentId());
            if (assessmentToDelete.hasErrors()) {
                Map<String, String> map = assessmentToDelete.errors();
                JsonObject errors = new JsonObject();
                map.forEach(errors::put);
                return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
                    ExecutionResult.ExecutionStatus.FAILED);
            }
        }
        if (!deleteContents()) {
            return new ExecutionResult<>(
                MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        return new ExecutionResult<>(
            MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("deleted"),
                EventBuilderFactory.getDeleteAssessmentEventBuilder(context.assessmentId())),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        // This operation is not read only and so is transaction
        return false;
    }

    private boolean deleteContents() {
        try {
            long deletedContentCount =
                Base.exec(AJEntityQuestion.DELETE_CONTENTS_QUERY, this.context.userId(), this.context.assessmentId());
            LOGGER.info("Assessment '{}' deleted along with '{}' questions", context.assessmentId(),
                deletedContentCount);
            return true;
        } catch (DBException e) {
            LOGGER.error("Error deleting questions for Assessment '{}'", context.assessmentId(), e);
            return false;
        }
    }

}
