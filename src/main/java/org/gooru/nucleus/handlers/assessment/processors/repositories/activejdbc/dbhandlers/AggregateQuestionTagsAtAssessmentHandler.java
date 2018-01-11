package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import java.util.Map;
import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbutils.GUTCodeLookupHelper;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 10-Jan-2018
 */
public class AggregateQuestionTagsAtAssessmentHandler implements DBHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateQuestionTagsAtAssessmentHandler.class);
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
    private final ProcessorContext context;
    private AJEntityAssessment assessment;

    private JsonObject tagsAdded;
    private JsonObject tagsRemoved;
    private JsonObject aggregatedGutCodes;
    private JsonObject aggregatedTaxonomy;

    private static final String TAGS_ADDED = "tags_added";
    private static final String TAGS_REMOVED = "tags_removed";

    public AggregateQuestionTagsAtAssessmentHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // There should be an assessment id present
        if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
            LOGGER.warn("Missing assessment id");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        // The user should not be anonymous
        if (context.userId() == null || context.userId().isEmpty()
            || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
            LOGGER.warn("Anonymous user attempting to edit assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        // Payload should not be empty
        if (context.request() == null || context.request().isEmpty()) {
            LOGGER.warn("Empty payload supplied to edit assessment");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        // Our validators should certify this
        JsonObject errors = new DefaultPayloadValidator().validatePayload(context.request(),
            AJEntityAssessment.aggregateTagsFieldSelector(), AJEntityAssessment.getValidatorRegistry());
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
        this.assessment = assessments.get(0);
        return AuthorizerBuilder.buildUpdateAuthorizer(this.context).authorize(this.assessment);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        String existingAggregatedGutCodes = this.assessment.getString(AJEntityAssessment.AGGREGATED_GUT_CODES);
        String existingAggregatedTaxonomy = this.assessment.getString(AJEntityAssessment.AGGREGATED_TAXONOMY);

        this.aggregatedGutCodes = (existingAggregatedGutCodes != null && !existingAggregatedGutCodes.isEmpty())
            ? new JsonObject(existingAggregatedGutCodes) : new JsonObject();
        this.aggregatedTaxonomy = (existingAggregatedTaxonomy != null && !existingAggregatedTaxonomy.isEmpty())
            ? new JsonObject(existingAggregatedTaxonomy) : new JsonObject();

        JsonObject tagDiff = calculateTagDifference();
        // If no tag difference is found in existing tags and in request,
        // silently ignore and return success without event
        if (tagDiff == null || tagDiff.isEmpty()) {
            LOGGER.debug("no tag difference found, skipping.");
            return new ExecutionResult<>(
                MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("updated")),
                ExecutionResult.ExecutionStatus.SUCCESSFUL);
        }
        
        this.tagsAdded = tagDiff.getJsonObject(TAGS_ADDED, null);
        this.tagsRemoved = tagDiff.getJsonObject(TAGS_REMOVED, null);

        if (this.tagsRemoved != null && !this.tagsRemoved.isEmpty()) {
            processTagRemoval();
        }

        if (this.tagsAdded != null && !this.tagsAdded.isEmpty()) {
            processTagAddition();
        }

        this.assessment.setAggregatedGutCodes(this.aggregatedGutCodes.toString());
        this.assessment.setAggregatedTaxonomy(this.aggregatedTaxonomy.toString());
        boolean result = this.assessment.save();
        if (!result) {
            LOGGER.error("Assessment with id '{}' failed to save after tag aggregation", context.assessmentId());
            if (this.assessment.hasErrors()) {
                Map<String, String> map = this.assessment.errors();
                JsonObject errors = new JsonObject();
                map.forEach(errors::put);
                return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
                    ExecutionResult.ExecutionStatus.FAILED);
            }
        }
        
        LOGGER.debug("assessment saved sucessfully");
        return new ExecutionResult<>(
            MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("updated"),
                EventBuilderFactory.getAggregateQuestionTagAtAssessmentEventBuilder(context.assessmentId(), tagDiff)),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

    private static class DefaultPayloadValidator implements PayloadValidator {
    }

    private void processTagAddition() {
        Map<String, String> frameworkToGutCodeMapping =
            GUTCodeLookupHelper.populateGutCodesToTaxonomyMapping(this.tagsAdded.fieldNames());

        frameworkToGutCodeMapping.keySet().forEach(gutCode -> {
            // If the gut code to be added is already exists in aggregated gut
            // codes, then increase competency count by 1
            // If it does not exists, then add new
            if (this.aggregatedGutCodes.containsKey(gutCode)) {
                int competencyCount = this.aggregatedGutCodes.getInteger(gutCode);
                this.aggregatedGutCodes.put(gutCode, (competencyCount + 1));
            } else {
                this.aggregatedGutCodes.put(gutCode, 1);
                this.aggregatedTaxonomy.put(frameworkToGutCodeMapping.get(gutCode),
                    this.tagsAdded.getJsonObject(frameworkToGutCodeMapping.get(gutCode)));
            }
        });
    }

    private void processTagRemoval() {

        Map<String, String> frameworkToGutCodeMapping =
            GUTCodeLookupHelper.populateGutCodesToTaxonomyMapping(this.tagsRemoved.fieldNames());

        frameworkToGutCodeMapping.keySet().forEach(gutCode -> {
            if (this.aggregatedGutCodes.containsKey(gutCode)) {
                int competencyCount = this.aggregatedGutCodes.getInteger(gutCode);
                // Competency count 1 means this competency is tagged only once
                // and across lessons. Hence can be removed
                // Competency count greater than 1 means this competency is
                // tagged multiple times across lesson, so we will just reduce
                // the competency count
                if (competencyCount == 1) {
                    this.aggregatedGutCodes.remove(gutCode);
                    aggregatedTaxonomy.remove(frameworkToGutCodeMapping.get(gutCode));
                } else if (competencyCount > 1) {
                    this.aggregatedGutCodes.put(gutCode, (competencyCount - 1));
                }
            }

            // Do nothing of the gut code which is not present in existing
            // aggregated gut codes
        });
    }

    private JsonObject calculateTagDifference() {
        JsonObject result = new JsonObject();
        String existingTagsAsString = this.assessment.getString(AJEntityAssessment.AGGREGATED_TAXONOMY);
        JsonObject existingTags = existingTagsAsString != null && !existingTagsAsString.isEmpty()
            ? new JsonObject(existingTagsAsString) : new JsonObject();
        JsonObject newTags = this.context.request().getJsonObject(AJEntityAssessment.AGGREGATED_TAXONOMY);

        if (existingTags.isEmpty() && newTags != null && !newTags.isEmpty()) {
            result.put(TAGS_ADDED, newTags.copy());
            result.put(TAGS_REMOVED, new JsonObject());
        } else if (!existingTags.isEmpty() && (newTags == null || newTags.isEmpty())) {
            result.put(TAGS_ADDED, new JsonObject());
            result.put(TAGS_REMOVED, existingTags.copy());
        } else if (!existingTags.isEmpty() && newTags != null && !newTags.isEmpty()) {
            JsonObject toBeAdded = new JsonObject();
            JsonObject toBeRemoved = existingTags.copy();
            newTags.forEach(entry -> {
                String key = entry.getKey();
                if (toBeRemoved.containsKey(key)) {
                    toBeRemoved.remove(key);
                } else {
                    toBeAdded.put(key, entry.getValue());
                }
            });

            if (toBeAdded.isEmpty() && toBeRemoved.isEmpty()) {
                return null;
            }

            result.put(TAGS_ADDED, toBeAdded);
            result.put(TAGS_REMOVED, toBeRemoved);
        }
        LOGGER.debug("tag difference :{}", result.toString());
        return result;
    }
}
