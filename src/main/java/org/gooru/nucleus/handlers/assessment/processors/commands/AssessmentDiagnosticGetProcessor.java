package org.gooru.nucleus.handlers.assessment.processors.commands;

import static org.gooru.nucleus.handlers.assessment.processors.utils.ValidationUtils.validateContext;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish on 30/12/16.
 */
class AssessmentDiagnosticGetProcessor extends AbstractCommandProcessor {
    public AssessmentDiagnosticGetProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    protected void setDeprecatedVersions() {

    }

    @Override
    protected MessageResponse processCommand() {
        return RepoBuilder.buildAssessmentRepo(context).fetchDiagnosticAssessment();
    }
}
