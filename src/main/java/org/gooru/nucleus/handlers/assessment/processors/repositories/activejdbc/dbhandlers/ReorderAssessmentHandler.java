package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityQuestion;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AssessmentDao;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.PayloadValidator;
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
class ReorderAssessmentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReorderAssessmentHandler.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private final ProcessorContext context;
  private JsonArray input;
  private AJEntityAssessment assessment;

  public ReorderAssessmentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // There should be an assessment id present
    if (context.assessmentId() == null || context.assessmentId().isEmpty()) {
      LOGGER.warn("Missing assessment id");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("missing.assessment.id")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty()
        || context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous user attempting to reorder assessment");
      return new ExecutionResult<>(
          MessageResponseFactory.createForbiddenResponse(RESOURCE_BUNDLE.getString("not.allowed")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    // Payload should not be empty
    if (context.request() == null || context.request().isEmpty()) {
      LOGGER.warn("Empty payload supplied to reorder assessment");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("empty.payload")),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    JsonObject errors = new DefaultPayloadValidator().validatePayload(context.request(),
        AssessmentDao.reorderFieldSelector(), AssessmentDao.getValidatorRegistry());
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
        .findBySQL(AJEntityAssessment.AUTHORIZER_QUERY,
            AJEntityAssessment.ASSESSMENT, context.assessmentId(), false);
    // Assessment should be present in DB
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", context.assessmentId());
      return new ExecutionResult<>(
          MessageResponseFactory
              .createNotFoundResponse(
                  RESOURCE_BUNDLE.getString("assessment.id") + context.assessmentId()),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    this.assessment = assessments.get(0);
    try {
      List idList =
          Base.firstColumn(AJEntityQuestion.QUESTIONS_FOR_ASSESSMENT_QUERY,
              this.context.assessmentId());
      this.input = this.context.request().getJsonArray(AJEntityAssessment.REORDER_PAYLOAD_KEY);
      if (idList.size() != input.size()) {
        return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
            RESOURCE_BUNDLE.getString("question.count.mismatch")),
            ExecutionResult.ExecutionStatus.FAILED);
      }
      for (Object entry : input) {
        String payloadId = ((JsonObject) entry).getString(AJEntityAssessment.ID);
        if (!idList.contains(UUID.fromString(payloadId))) {
          return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
              RESOURCE_BUNDLE.getString("missing.questions")),
              ExecutionResult.ExecutionStatus.FAILED);
        }
      }
    } catch (DBException | ClassCastException e) {
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
          RESOURCE_BUNDLE.getString("incorrect.payload.data.types")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    return AuthorizerBuilder.buildUpdateAuthorizer(this.context).authorize(assessment);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    try {
      PreparedStatement ps = Base.startBatch(AJEntityQuestion.REORDER_QUERY);
      for (Object entry : input) {
        String payloadId = ((JsonObject) entry).getString(AJEntityAssessment.ID);
        int sequenceId = ((JsonObject) entry).getInteger(AJEntityQuestion.SEQUENCE_ID);
        Base.addBatch(ps, sequenceId, this.context.userId(), payloadId, context.assessmentId());
      }
      Base.executeBatch(ps);
      this.assessment
          .setTimestamp(AJEntityAssessment.UPDATED_AT, new Timestamp(System.currentTimeMillis()));
      boolean result = this.assessment.save();
      if (!result) {
        LOGGER.error("Assessment with id '{}' failed to save modified time stamp",
            context.assessmentId());
        if (this.assessment.hasErrors()) {
          Map<String, String> map = this.assessment.errors();
          JsonObject errors = new JsonObject();
          map.forEach(errors::put);
          return new ExecutionResult<>(MessageResponseFactory.createValidationErrorResponse(errors),
              ExecutionResult.ExecutionStatus.FAILED);
        }
      }

    } catch (DBException | ClassCastException e) {
      // No special handling for CCE as this could have been thrown in the
      // validation itself
      LOGGER
          .error("Not able to update the sequences for assessment '{}'", context.assessmentId(), e);
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInternalErrorResponse(RESOURCE_BUNDLE.getString("error.from.store")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(
        MessageResponseFactory.createNoContentResponse(RESOURCE_BUNDLE.getString("updated"),
            EventBuilderFactory.getReorderAssessmentEventBuilder(context.assessmentId())),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);

  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

  private static class DefaultPayloadValidator implements PayloadValidator {

  }
}
