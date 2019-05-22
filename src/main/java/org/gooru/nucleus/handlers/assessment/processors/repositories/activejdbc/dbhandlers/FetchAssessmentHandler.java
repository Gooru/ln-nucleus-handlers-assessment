package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers.DbHelperUtil;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityRubric;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 11/1/16.
 */
class FetchAssessmentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;
  private AJEntityAssessment assessment;

  public FetchAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
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
        AJEntityAssessment
            .findBySQL(AJEntityAssessment.FETCH_ASSESSMENT_QUERY, context.assessmentId());
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
        JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, AJEntityAssessment.FETCH_QUERY_FIELD_LIST)
            .toJson(this.assessment));
    // Now query questions and populate them
    LazyList<AJEntityQuestion> questions =
        AJEntityQuestion
            .findBySQL(AJEntityQuestion.FETCH_QUESTION_SUMMARY_QUERY, context.assessmentId());
    if (!questions.isEmpty()) {
      List<String> oeQuestionIds = new ArrayList<>();
      for (AJEntityQuestion question : questions) {
        if (question.get(AJEntityQuestion.CONTENT_SUBFORMAT) != null
            && AJEntityQuestion.RUBRIC_ASSOCIATION_ALLOWED_TYPES
            .contains(question.getString(AJEntityQuestion.CONTENT_SUBFORMAT))) {
          oeQuestionIds.add(question.get(AJEntityQuestion.ID).toString());
        }
      }

      JsonArray questionsArray = new JsonArray(
          JsonFormatterBuilder
              .buildSimpleJsonFormatter(false, AJEntityQuestion.FETCH_QUESTION_SUMMARY_FIELDS)
              .toJson(questions));
      if (!oeQuestionIds.isEmpty()) {
        LazyList<AJEntityRubric> rubrics =
            AJEntityRubric.findBySQL(AJEntityRubric.FETCH_RUBRIC_SUMMARY,
                DbHelperUtil.toPostgresArrayString(oeQuestionIds));
        if (rubrics != null && !rubrics.isEmpty()) {
          rubrics.forEach(rubric -> {
            for (Object questionObject : questionsArray) {
              JsonObject question = (JsonObject) questionObject;
              if (question.getString(AJEntityQuestion.ID) != null && rubric
                  .get(AJEntityRubric.CONTENT_ID).toString()
                  .contains(question.getString(AJEntityQuestion.ID))) {
                if (!rubric.getBoolean(AJEntityRubric.IS_RUBRIC)) {
                  question.put(AJEntityQuestion.MAX_SCORE, rubric.get(AJEntityRubric.MAX_SCORE));
                }
              }
            }
          });
        }
      }
      response.put(AJEntityQuestion.QUESTION, questionsArray);
    } else {
      response.put(AJEntityQuestion.QUESTION, new JsonArray());
    }
    // Now collaborator, we need to know if we want to get it from course or
    // whatever is in the collection would suffice
    String courseId = this.assessment.getCourseId();
    if (courseId == null || courseId.isEmpty()) {
      String collaborators = this.assessment.getString(AJEntityAssessment.COLLABORATOR);
      if (collaborators == null || collaborators.isEmpty()) {
        response.put(AJEntityAssessment.COLLABORATOR, new JsonArray());
      } else {
        response.put(AJEntityAssessment.COLLABORATOR, new JsonArray(collaborators));
      }
    } else {
      try {
        // Need to fetch collaborators
        Object courseCollaboratorObject =
            Base.firstCell(AJEntityAssessment.COURSE_COLLABORATOR_QUERY, courseId);
        if (courseCollaboratorObject != null) {
          response.put(AJEntityAssessment.COLLABORATOR,
              new JsonArray(courseCollaboratorObject.toString()));
        } else {
          response.put(AJEntityAssessment.COLLABORATOR, new JsonArray());
        }
      } catch (DBException e) {
        LOGGER
            .error(
                "Error trying to get course collaborator for course '{}' to fetch assessment '{}'",
                courseId,
                this.context.assessmentId(), e);
        return new ExecutionResult<>(
            MessageResponseFactory
                .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
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
