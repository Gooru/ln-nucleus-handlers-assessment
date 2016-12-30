package org.gooru.nucleus.handlers.assessment.processors.commands;

import static org.gooru.nucleus.handlers.assessment.processors.utils.ValidationUtils.validateContext;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish on 30/12/16.
 */
class AssessmentUpdateProcessor extends AbstractCommandProcessor {
    public AssessmentUpdateProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    protected void setDeprecatedVersions() {

    }

    @Override
    protected MessageResponse processCommand() {
        if (!validateContext(context)) {
            return MessageResponseFactory
                .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.assessment.id"));
        }
        return RepoBuilder.buildAssessmentRepo(context).updateAssessment();
    }
}
