package org.gooru.nucleus.handlers.assessment.processors.commands;

import static org.gooru.nucleus.handlers.assessment.processors.utils.ValidationUtils.validateContext;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author szgooru Created On: 10-Jan-2018
 */
public class AssessmentQuestionTagAggregateProcessor extends AbstractCommandProcessor {

    protected AssessmentQuestionTagAggregateProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    protected void setDeprecatedVersions() {
        // NOOP
    }

    @Override
    protected MessageResponse processCommand() {
        if (!validateContext(context)) {
            return MessageResponseFactory
                .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
        }
        return RepoBuilder.buildAssessmentRepo(context).aggregateQuestionTagsAtAssessment();
    }

}
