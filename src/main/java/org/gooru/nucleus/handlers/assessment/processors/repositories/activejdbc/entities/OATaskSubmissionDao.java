package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oatasksubmissioncreate.OATaskSubmissionCreateCommand;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class OATaskSubmissionDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(OATaskSubmissionDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private static final String FETCH_QUERY_FILTER = "oa_task_id = ? and id = ?";
  private static final String DELETE_TASK_SUBMISSION = "delete from oa_tasks_submissions where id = ?";

  private OATaskSubmissionDao() {
    throw new AssertionError();
  }


  public static AJEntityOATaskSubmission fetchOATaskSubmissionByTaskIdAndId(Long oaTaskId,
      Long oaTaskSubmissionId) {
    List<AJEntityOATaskSubmission> submissions = AJEntityOATaskSubmission
        .where(FETCH_QUERY_FILTER, oaTaskId, oaTaskSubmissionId);

    if (submissions == null || submissions.isEmpty()) {
      LOGGER.warn("Offline activity task id: '{}' submission id '{}' not present in DB", oaTaskId,
          oaTaskSubmissionId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(
              RESOURCE_BUNDLE.getString("not.found") + " : " + oaTaskId + ":"
                  + oaTaskSubmissionId));
    }
    return submissions.get(0);
  }

  public static void deleteTaskSubmissionById(Long oaTaskSubmissionId) {
    if (oaTaskSubmissionId != null) {
      Base.exec(DELETE_TASK_SUBMISSION, oaTaskSubmissionId);
    }
  }

  public static Long createTaskSubmission(OATaskSubmissionCreateCommand command) {
    AJEntityOATaskSubmission submission = new AJEntityOATaskSubmission();
    submission.setOaTaskId(command.getOaTaskId());
    submission.setOaTaskSubmissionType(command.getOaTaskSubmissionType());
    submission.setOaTaskSubmissionSubtype(command.getOaTaskSubmissionSubtype());

    boolean result = submission.save();

    if (!result) {
      JsonObject errors = ModelErrorFormatter.formattedError(submission);
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(errors));
    }
    return (Long) submission.getId();
  }
}
