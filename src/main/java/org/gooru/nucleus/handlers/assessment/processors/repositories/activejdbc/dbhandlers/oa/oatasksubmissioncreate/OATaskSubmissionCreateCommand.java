package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oatasksubmissioncreate;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.OATaskSubmissionTypeSubTypeValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

public class OATaskSubmissionCreateCommand {

  private static final String OA_TASK_SUBMISSION_TYPE = "oa_task_submission_type";
  private static final String OA_TASK_SUBMISSION_SUBTYPE = "oa_task_submission_subtype";
  private final Long oaTaskId;
  private final String oaTaskSubmissionType;
  private final String oaTaskSubmissionSubtype;
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private OATaskSubmissionCreateCommand(Long oaTaskId, String oaTaskSubmissionType,
      String oaTaskSubmissionSubtype) {
    this.oaTaskId = oaTaskId;
    this.oaTaskSubmissionType = oaTaskSubmissionType;
    this.oaTaskSubmissionSubtype = oaTaskSubmissionSubtype;
  }

  static OATaskSubmissionCreateCommand build(OAProcessorContext context) {
    Long oaTaskId = Long.parseLong(context.oaTaskId());
    String oaTaskSubmissionType = context.request().getString(OA_TASK_SUBMISSION_TYPE);
    String oaTaskSubmissionSubtype = context.request().getString(OA_TASK_SUBMISSION_SUBTYPE);
    OATaskSubmissionCreateCommand command = new OATaskSubmissionCreateCommand(oaTaskId,
        oaTaskSubmissionType,
        oaTaskSubmissionSubtype);
    command.validate();
    return command;
  }

  private void validate() {
    if (oaTaskSubmissionType == null || oaTaskSubmissionType.isEmpty()
        || oaTaskSubmissionSubtype == null || oaTaskSubmissionSubtype.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.value")));
    }
    OATaskSubmissionTypeSubTypeValidator.validateTypeSubtypeValues(oaTaskSubmissionType,
        oaTaskSubmissionSubtype);
  }


  public String getOaTaskSubmissionType() {
    return oaTaskSubmissionType;
  }

  public String getOaTaskSubmissionSubtype() {
    return oaTaskSubmissionSubtype;
  }

  public Long getOaTaskId() {
    return oaTaskId;
  }
}
