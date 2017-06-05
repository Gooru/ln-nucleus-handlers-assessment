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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 25/3/16.
 */
public class CreateExternalAssessmentHandler implements DBHandler {
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateExternalAssessmentHandler.class);
    private final ProcessorContext context;

    public CreateExternalAssessmentHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // The user should not be anonymous
        if (context.userId() == null || context.userId().isEmpty() || context.userId()
            .equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
            LOGGER.warn("Anonymous or invalid user attempting to create assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // Payload should not be empty
        if (context.request() == null || context.request().isEmpty()) {
            LOGGER.warn("Empty payload supplied to create assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")),
                ExecutionResult.ExecutionStatus.FAILED);
        }
        // Our validators should certify this
        JsonObject errors = new DefaultPayloadValidator()
            .validatePayload(context.request(), AJEntityAssessment.createExFieldSelector(),
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
        // Only thing to do here is to authorize
        return AuthorizerBuilder.buildCreateAuthorizer(context).authorize(null);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        AJEntityAssessment assessment = new AJEntityAssessment();
        autoPopulateFields(assessment);

        new DefaultAJEntityAssessmentEntityBuilder()
            .build(assessment, context.request(), AJEntityAssessment.getConverterRegistry());

        boolean result = assessment.save();
        if (!result) {
            LOGGER.error("Assessment creation failed for user '{}'", context.userId());
            if (assessment.hasErrors()) {
                Map<String, String> map = assessment.errors();
                JsonObject errors = new JsonObject();
                map.forEach(errors::put);
                return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
                    ExecutionResult.ExecutionStatus.FAILED);
            }
        }
        return new ExecutionResult<>(MessageResponseFactory.createCreatedResponse(assessment.getId().toString(),
            EventBuilderFactory.getCreateExAssessmentEventBuilder(assessment.getString(AJEntityAssessment.ID))),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    private void autoPopulateFields(AJEntityAssessment assessment) {
        assessment.setModifierId(context.userId());
        assessment.setOwnerId(context.userId());
        assessment.setCreatorId(context.userId());
        assessment.setTypeExAssessment();
        assessment.setTenant(context.tenant());
        String tenantRoot = context.tenantRoot();
        if (tenantRoot != null && !tenantRoot.isEmpty()) {
            assessment.setTenantRoot(tenantRoot);
        }
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

    private static class DefaultPayloadValidator implements PayloadValidator {
    }

    private static class DefaultAJEntityAssessmentEntityBuilder implements EntityBuilder<AJEntityAssessment> {
    }
}
