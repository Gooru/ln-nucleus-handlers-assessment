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

    // Operation names: Also need to be updated in corresponding gateway
    public static final String MSG_OP_ASSESSMENT_GET = "assessment.get";
    public static final String MSG_OP_ASSESSMENT_CREATE = "assessment.create";
    public static final String MSG_OP_ASSESSMENT_UPDATE = "assessment.update";
    public static final String MSG_OP_ASSESSMENT_DELETE = "assessment.delete";
    public static final String MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE = "assessment.collaborator.update";
    public static final String MSG_OP_ASSESSMENT_QUESTION_ADD = "assessment.question.add";
    public static final String MSG_OP_ASSESSMENT_QUESTION_REORDER = "assessment.content.reorder";

    // Operation names for external Assessment: Need to be updated in gateway
    public static final String MSG_OP_EXT_ASSESSMENT_GET = "ext.assessment.get";
    public static final String MSG_OP_EXT_ASSESSMENT_CREATE = "ext.assessment.create";
    public static final String MSG_OP_EXT_ASSESSMENT_UPDATE = "ext.assessment.update";
    public static final String MSG_OP_EXT_ASSESSMENT_DELETE = "ext.assessment.delete";

    // Containers for different responses
    public static final String RESP_CONTAINER_MBUS = "mb.container";
    public static final String RESP_CONTAINER_EVENT = "mb.event";

    public static final String ASSESSMENT_ID = "assessmentId";
    public static final String ID = "id";

    private MessageConstants() {
        throw new AssertionError();
    }
}
