package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import java.util.Map;
import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entitybuilders.EntityBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 11/1/16.
 */
class UpdateCollaboratorHandler implements DBHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCollaboratorHandler.class);
    private static final String COLLABORATORS_REMOVED = "collaborators.removed";
    private static final String COLLABORATORS_ADDED = "collaborators.added";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
    private final ProcessorContext context;
    private AJEntityAssessment assessment;
    private JsonObject diffCollaborators;

    public UpdateCollaboratorHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // Assessment id is present
        if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
            LOGGER.warn("Missing assessment id");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // The user should not be anonymous
        if (context.userId() == null || context.userId().isEmpty() || context.userId()
            .equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
            LOGGER.warn("Anonymous user attempting to edit assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // Payload should not be empty
        if (context.request() == null || context.request().isEmpty()) {
            LOGGER.warn("Empty payload supplied to upload assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // Our validators should certify this
        JsonObject errors = new DefaultPayloadValidator()
            .validatePayload(context.request(), AJEntityAssessment.editCollaboratorFieldSelector(),
                AJEntityAssessment.getValidatorRegistry());
        if (errors != null && !errors.isEmpty()) {
            LOGGER.warn("Validation errors for request");
            return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);

    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        // Fetch the assessment where type is assessment and it is not deleted
        // already and id is specified id
        LazyList<AJEntityAssessment> assessments = AJEntityAssessment
            .findBySQL(AJEntityAssessment.AUTHORIZER_QUERY, AJEntityAssessment.ASSESSMENT, context.assessmentId(),
                false);
        // Assessment should be present in DB
        if (assessments.size() < 1) {
            LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
            return new ExecutionResult<>(MessageResponseFactory
                .createNotFoundResponse(RESOURCE_BUNDLE.getString("assessment.id") + context.assessmentId()),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        this.assessment = assessments.get(0);
        final String course = this.assessment.getCourseId();
        if (course != null) {
            LOGGER.error("Cannot update collaborator for assessment '{}' as it is part of course '{}'",
                context.assessmentId(), course);
            return new ExecutionResult<>(MessageResponseFactory
                .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("assessment.associated.with.course")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        diffCollaborators = calculateDiffOfCollaborators();
        return doAuthorization();
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        AJEntityAssessment assessment = new AJEntityAssessment();
        assessment.setIdWithConverter(context.assessmentId());
        assessment.setModifierId(context.userId());
        // Now auto populate is done, we need to setup the converter machinery
        new DefaultAJEntityAssessmentEntityBuilder()
            .build(assessment, context.request(), AJEntityAssessment.getConverterRegistry());

        boolean result = assessment.save();
        if (!result) {
            LOGGER.error("Assessment with id '{}' failed to save", context.assessmentId());
            if (assessment.hasErrors()) {
                Map<String, String> map = assessment.errors();
                JsonObject errors = new JsonObject();
                map.forEach(errors::put);
                return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
                    ExecutionResult.ExecutionStatus.FAILED);
            }
        }
        return new ExecutionResult<>(MessageResponseFactory
            .createNoContentResponse(RESOURCE_BUNDLE.getString("updated"), EventBuilderFactory
                .getUpdateCollaboratorForAssessmentEventBuilder(context.assessmentId(), diffCollaborators)),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

    private ExecutionResult<MessageResponse> doAuthorization() {
        ExecutionResult<MessageResponse> result =
            AuthorizerBuilder.buildUpdateCollaboratorAuthorizer(this.context).authorize(this.assessment);
        if (result.hasFailed()) {
            return result;
        }
        return AuthorizerBuilder
            .buildTenantCollaboratorAuthorizer(this.context, diffCollaborators.getJsonArray(COLLABORATORS_ADDED))
            .authorize(assessment);
    }

    private JsonObject calculateDiffOfCollaborators() {
        JsonObject result = new JsonObject();
        // Find current collaborators
        String currentCollaboratorsAsString = this.assessment.getString(AJEntityAssessment.COLLABORATOR);
        JsonArray currentCollaborators;
        currentCollaborators = currentCollaboratorsAsString != null && !currentCollaboratorsAsString.isEmpty() ?
            new JsonArray(currentCollaboratorsAsString) : new JsonArray();
        JsonArray newCollaborators = this.context.request().getJsonArray(AJEntityAssessment.COLLABORATOR);
        if (currentCollaborators.isEmpty() && !newCollaborators.isEmpty()) {
            // Adding all
            result.put(COLLABORATORS_ADDED, newCollaborators.copy());
            result.put(COLLABORATORS_REMOVED, new JsonArray());
        } else if (!currentCollaborators.isEmpty() && newCollaborators.isEmpty()) {
            // Removing all
            result.put(COLLABORATORS_ADDED, new JsonArray());
            result.put(COLLABORATORS_REMOVED, currentCollaborators.copy());
        } else if (!currentCollaborators.isEmpty() && !newCollaborators.isEmpty()) {
            // Do the diffing
            JsonArray toBeAdded = new JsonArray();
            JsonArray toBeDeleted = currentCollaborators.copy();
            for (Object o : newCollaborators) {
                if (toBeDeleted.contains(o)) {
                    toBeDeleted.remove(o);
                } else {
                    toBeAdded.add(o);
                }
            }
            result.put(COLLABORATORS_ADDED, toBeAdded);
            result.put(COLLABORATORS_REMOVED, toBeDeleted);
        } else {
            // WHAT ????
            LOGGER
                .warn("Updating collaborator with empty payload when current collaborator is empty for assessment '{}'",
                    this.context.assessmentId());
            result.put(COLLABORATORS_ADDED, new JsonArray());
            result.put(COLLABORATORS_REMOVED, new JsonArray());
        }
        return result;
    }

    private static class DefaultPayloadValidator implements PayloadValidator {
    }

    private static class DefaultAJEntityAssessmentEntityBuilder implements EntityBuilder<AJEntityAssessment> {
    }
}
