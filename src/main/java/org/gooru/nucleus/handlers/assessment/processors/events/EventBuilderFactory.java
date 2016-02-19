package org.gooru.nucleus.handlers.assessment.processors.events;

import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 19/1/16.
 */
public final class EventBuilderFactory {

  private static final String EVT_ASSESSMENT_CREATE = "event.assessment.create";
  private static final String EVT_ASSESSMENT_UPDATE = "event.assessment.update";
  private static final String EVT_ASSESSMENT_DELETE = "event.assessment.delete";
  private static final String EVT_ASSESSMENT_QUESTION_ADD = "event.assessment.question.add";
  private static final String EVT_ASSESSMENT_REORDER = "event.assessment.reorder";
  private static final String EVT_ASSESSMENT_COLLABORATOR_UPDATE = "event.assessment.collaborator.update";
  private static final String EVENT_NAME = "event.name";
  private static final String EVENT_BODY = "event.body";
  private static final String ASSESSMENT_ID = "id";

  private EventBuilderFactory() {
    throw new AssertionError();
  }

  public static EventBuilder getDeleteAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_DELETE).put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }

  public static EventBuilder getCreateAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_CREATE).put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }

  public static EventBuilder getUpdateAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_UPDATE).put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }

  public static EventBuilder getAddQuestionToAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_QUESTION_ADD).put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }

  public static EventBuilder getReorderAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_REORDER).put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }

  public static EventBuilder getUpdateCollaboratorForAssessmentEventBuilder(String assessmentId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_ASSESSMENT_COLLABORATOR_UPDATE)
                                 .put(EVENT_BODY, new JsonObject().put(ASSESSMENT_ID, assessmentId));
  }


}
