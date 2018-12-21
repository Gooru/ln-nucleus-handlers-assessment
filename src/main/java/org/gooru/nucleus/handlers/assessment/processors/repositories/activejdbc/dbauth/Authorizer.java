package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.javalite.activejdbc.Model;

/**
 * Created by ashish on 29/1/16.
 */
public interface Authorizer<T extends Model> {

  ExecutionResult<MessageResponse> authorize(T model);

}
