package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;

/**
 * Created by ashish on 29/1/16.
 */
public final class AuthorizerBuilder {

    private AuthorizerBuilder() {
    }

    public static Authorizer<AJEntityAssessment> buildUpdateAuthorizer(ProcessorContext context) {
        return new UpdateAuthorizer(context);
    }

    public static Authorizer<AJEntityAssessment> buildDeleteAuthorizer(ProcessorContext context) {
        return new DeleteAuthorizer(context);
    }

    public static Authorizer<AJEntityAssessment> buildUpdateCollaboratorAuthorizer(ProcessorContext context) {
        return new UpdateCollaboratorAuthorizer(context);
    }

    // Creation is only allowed outside of any context and hence it has got no
    // bearing on course container, which does not exist as our API call for
    // association may be called after create call to set that up
    // As long as session token is valid and user is not anonymous, which is the
    // case as we are, we should be fine
    public static Authorizer<AJEntityAssessment> buildCreateAuthorizer(ProcessorContext context) {
        return model -> new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }

    public static Authorizer<AJEntityAssessment> buildAddQuestionToAssessmentAuthorizer(ProcessorContext context) {
        return new AddQuestionToAssessmentAuthorizer(context);
    }

    public static Authorizer<AJEntityAssessment> buildTenantAuthorizer(ProcessorContext context) {
        return new TenantAuthorizer(context);
    }
}
