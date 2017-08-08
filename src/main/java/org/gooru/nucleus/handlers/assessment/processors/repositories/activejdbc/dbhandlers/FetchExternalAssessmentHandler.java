package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 25/3/16.
 */
class FetchExternalAssessmentHandler implements DBHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchExternalAssessmentHandler.class);
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
    private final ProcessorContext context;
    private AJEntityAssessment assessment;

    public FetchExternalAssessmentHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // There should be an assessment id present
        if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
            LOGGER.warn("Missing assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        if (context.userId() == null || context.userId().isEmpty()) {
            LOGGER.warn("Invalid user");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        LazyList<AJEntityAssessment> assessments =
            AJEntityAssessment.findBySQL(AJEntityAssessment.FETCH_EXTERNAL_ASSSESSMENT_QUERY, context.assessmentId());
        if (assessments.isEmpty()) {
            LOGGER.warn("Not able to find assessment '{}'", this.context.assessmentId());
            return new ExecutionResult<>(
                MessageResponseFactory.createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        this.assessment = assessments.get(0);
        return AuthorizerBuilder.buildTenantAuthorizer(this.context).authorize(assessment);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        // First create response from Assessment
        JsonObject response = new JsonObject(
            JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityAssessment.FETCH_EA_QUERY_FIELD_LIST)
                .toJson(this.assessment));
        // Now collaborator, we need to know if we want to get it from course
        // else no collaboration on external assessment
        String courseId = this.assessment.getString(AJEntityAssessment.COURSE_ID);
        if (courseId == null || courseId.isEmpty()) {
            response.put(AJEntityAssessment.COLLABORATOR, new JsonArray());
        } else {
            try {
                // Need to fetch collaborators
                Object courseCollaboratorObject =
                    Base.firstCell(AJEntityAssessment.COURSE_COLLABORATOR_QUERY, courseId);
                if (courseCollaboratorObject != null) {
                    response.put(AJEntityAssessment.COLLABORATOR, new JsonArray(courseCollaboratorObject.toString()));
                } else {
                    response.put(AJEntityAssessment.COLLABORATOR, new JsonArray());
                }
            } catch (DBException e) {
                LOGGER
                    .error("Error trying to get course collaborator for course '{}' to fetch assessment '{}'", courseId,
                        this.context.assessmentId(), e);
                return new ExecutionResult<>(
                    MessageResponseFactory.createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
                    ExecutionResult.ExecutionStatus.FAILED);
            }
        }
        return new ExecutionResult<>(MessageResponseFactory.createOkayResponse(response),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }
}
