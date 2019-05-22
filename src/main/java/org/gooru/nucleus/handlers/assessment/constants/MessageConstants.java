package org.gooru.nucleus.handlers.assessment.constants;

public final class MessageConstants {

  public static final String MSG_HEADER_OP = "mb.operation";
  public static final String MSG_HEADER_TOKEN = "session.token";
  public static final String MSG_OP_STATUS = "mb.operation.status";
  public static final String MSG_KEY_SESSION = "session";
  public static final String MSG_OP_STATUS_SUCCESS = "success";
  public static final String MSG_OP_STATUS_ERROR = "error";
  public static final String MSG_OP_STATUS_VALIDATION_ERROR = "error.validation";
  public static final String MSG_USER_ANONYMOUS = "anonymous";
  public static final String MSG_USER_ID = "user_id";
  public static final String MSG_HTTP_STATUS = "http.status";
  public static final String MSG_HTTP_BODY = "http.body";
  public static final String MSG_HTTP_RESPONSE = "http.response";
  public static final String MSG_HTTP_ERROR = "http.error";
  public static final String MSG_HTTP_VALIDATION_ERROR = "http.validation.error";
  public static final String MSG_HTTP_HEADERS = "http.headers";
  public static final String MSG_MESSAGE = "message";

  // Assessment Operation names: Also need to be updated in corresponding gateway
  public static final String MSG_OP_ASSESSMENT_GET = "assessment.get";
  public static final String MSG_OP_ASSESSMENT_CREATE = "assessment.create";
  public static final String MSG_OP_ASSESSMENT_UPDATE = "assessment.update";
  public static final String MSG_OP_ASSESSMENT_DELETE = "assessment.delete";
  public static final String MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE = "assessment.collaborator.update";
  public static final String MSG_OP_ASSESSMENT_QUESTION_ADD = "assessment.question.add";
  public static final String MSG_OP_ASSESSMENT_QUESTION_REORDER = "assessment.content.reorder";

  // External Assessments Operation names: Need to be updated in gateway
  public static final String MSG_OP_EXT_ASSESSMENT_GET = "ext.assessment.get";
  public static final String MSG_OP_EXT_ASSESSMENT_CREATE = "ext.assessment.create";
  public static final String MSG_OP_EXT_ASSESSMENT_UPDATE = "ext.assessment.update";
  public static final String MSG_OP_EXT_ASSESSMENT_DELETE = "ext.assessment.delete";
  public static final String MSG_OP_ASSESSMENT_MASTERY_ACCRUAL_GET = "assessment.mastery-accrual.get";

  // Offline Activity Operations names
  public static final String MSG_OP_OA_GET_SUMMARY = "oa.get.summary";
  public static final String MSG_OP_OA_GET_DETAIL = "oa.get.detail";
  public static final String MSG_OP_OA_CREATE = "oa.create";
  public static final String MSG_OP_OA_UPDATE = "oa.update";
  public static final String MSG_OP_OA_DELETE = "oa.delete";
  public static final String MSG_OP_OA_COLLABORATOR_UPDATE = "oa.collaborator.update";
  public static final String MSG_OP_OA_TASK_CREATE = "oa.task.create";
  public static final String MSG_OP_OA_TASK_DELETE = "oa.task.delete";
  public static final String MSG_OP_OA_TASK_UPDATE = "oa.task.update";
  public static final String MSG_OP_OA_TASK_SUBMISSION_CREATE = "oa.task.submission.create";
  public static final String MSG_OP_OA_TASK_SUBMISSION_DELETE = "oa.task.submission.delete";
  public static final String MSG_OP_OA_REF_DELETE = "oa.ref.delete";
  public static final String MSG_OP_OA_REF_UPDATE = "oa.ref.update";
  public static final String MSG_OP_OA_REF_CREATE = "oa.ref.create";
  public static final String MSG_OP_OA_TEACHER_RUBRIC_ASSOCIATE = "oa.rubric.teacher.add";
  public static final String MSG_OP_OA_STUDENT_RUBRIC_ASSOCIATE = "oa.rubric.student.add";

  // Containers for different responses
  public static final String RESP_CONTAINER_MBUS = "mb.container";
  public static final String RESP_CONTAINER_EVENT = "mb.event";

  public static final String ASSESSMENT_ID = "assessmentId";
  public static final String ASSESSMENT_IDS = "assessmentIds";
  public static final String ID = "id";

  private MessageConstants() {
    throw new AssertionError();
  }
}
