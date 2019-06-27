package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class CollaboratorHelper {

  private CollaboratorHelper() {
    throw new AssertionError();
  }

  private static final String COLLABORATORS_REMOVED = "collaborators.removed";
  private static final String COLLABORATORS_ADDED = "collaborators.added";
  private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorHelper.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  public static JsonObject calculateDiffOfCollaborators(JsonArray collaboratorsInRequest,
      JsonArray currentCollaborators) {

    JsonArray collaboratorsFromPayload =
        collaboratorsInRequest != null ? collaboratorsInRequest : new JsonArray();
    JsonObject result = new JsonObject();
    if (currentCollaborators.isEmpty() && !collaboratorsFromPayload.isEmpty()) {
      // Adding all
      result.put(COLLABORATORS_ADDED, collaboratorsFromPayload.copy());
      result.put(COLLABORATORS_REMOVED, new JsonArray());
    } else if (!currentCollaborators.isEmpty() && collaboratorsFromPayload.isEmpty()) {
      // Removing all
      result.put(COLLABORATORS_ADDED, new JsonArray());
      result.put(COLLABORATORS_REMOVED, currentCollaborators.copy());
    } else if (!currentCollaborators.isEmpty() && !collaboratorsFromPayload.isEmpty()) {
      // Do the diffing
      JsonArray toBeAdded = new JsonArray();
      JsonArray toBeDeleted = currentCollaborators.copy();
      for (Object o : collaboratorsFromPayload) {
        if (toBeDeleted.contains(o)) {
          toBeDeleted.remove(o);
        } else {
          toBeAdded.add(o);
        }
      }
      result.put(COLLABORATORS_ADDED, toBeAdded);
      result.put(COLLABORATORS_REMOVED, toBeDeleted);
    } else {
      result.put(COLLABORATORS_ADDED, new JsonArray());
      result.put(COLLABORATORS_REMOVED, new JsonArray());
    }
    return result;
  }

  public static JsonArray getAddedCollaboratorsFromDiff(JsonObject diffCollaborators) {
    if (diffCollaborators == null) {
      return new JsonArray();
    }
    return diffCollaborators.getJsonArray(COLLABORATORS_ADDED);
  }

  public static ExecutionResult<MessageResponse> doAuthorization(ProcessorContext processorContext,
      AJEntityAssessment assessment, JsonArray collaboratorsToBeAdded) {
    ExecutionResult<MessageResponse> result =
        AuthorizerBuilder.buildUpdateCollaboratorAuthorizer(processorContext)
            .authorize(assessment);
    if (result.hasFailed()) {
      return result;
    }
    return AuthorizerBuilder
        .buildTenantCollaboratorAuthorizer(processorContext, collaboratorsToBeAdded)
        .authorize(assessment);
  }

  public static void validateOAIsStandalone(String courseId, String assessmentId) {
    if (courseId != null) {
      LOGGER.error(
          "Cannot update collaborator for offline activity '{}' as it is part of course '{}'",
          assessmentId, courseId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createInvalidRequestResponse(
              RESOURCE_BUNDLE.getString("assessment.associated.with.course")));
    }
  }

  public static JsonArray fetchCollaboratorsForAssessment(AJEntityAssessment assessment) {
    String courseId = assessment.getCourseId();
    if (courseId == null || courseId.isEmpty()) {
      String collaborators = assessment.getString(AJEntityAssessment.COLLABORATOR);
      if (collaborators == null || collaborators.isEmpty()) {
        return new JsonArray();
      } else {
        return new JsonArray(collaborators);
      }
    } else {
      try {
        Object courseCollaboratorObject =
            Base.firstCell(AssessmentDao.COURSE_COLLABORATOR_QUERY, courseId);
        if (courseCollaboratorObject != null) {
          return new JsonArray(courseCollaboratorObject.toString());
        } else {
          return new JsonArray();
        }
      } catch (DBException e) {
        LOGGER.error(
            "Error trying to get course collaborator for course '{}' to fetch assessment '{}'",
            courseId, assessment.getId(), e);
        return new JsonArray();
      }

    }
  }
}
