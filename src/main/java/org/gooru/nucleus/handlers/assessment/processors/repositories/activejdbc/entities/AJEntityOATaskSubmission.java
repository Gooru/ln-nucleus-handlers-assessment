package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author ashish.
 */
@Table("oa_tasks_submissions")
public class AJEntityOATaskSubmission extends Model {

  private static final String OA_TASK_ID = "oa_task_id";
  private static final String OA_TASK_SUBMISSION_TYPE = "oa_task_submission_type";
  private static final String OA_TASK_SUBMISSION_SUBTYPE = "oa_task_submission_subtype";
  private static final String CREATED_AT = "created_at";
  private static final String UPDATED_AT = "updated_at";
  private static final String ID = "id";

  public static final List<String> FETCH_LIST = Arrays
      .asList(ID, OA_TASK_ID, OA_TASK_SUBMISSION_TYPE, OA_TASK_SUBMISSION_SUBTYPE, CREATED_AT,
          UPDATED_AT);

  public Long getOaTaskId() {
    return this.getLong(OA_TASK_ID);
  }

  public String getOaTaskSubmissionType() {
    return this.getString(OA_TASK_SUBMISSION_TYPE);
  }

  public String getOaTaskSubmissionSubtype() {
    return this.getString(OA_TASK_SUBMISSION_SUBTYPE);
  }

  public Date getCreatedAt() {
    return this.getDate(CREATED_AT);
  }

  public Date getUpdatedAt() {
    return this.getDate(UPDATED_AT);
  }

  public void setOaTaskId(Long taskId) {
    this.setLong(OA_TASK_ID, taskId);
  }

  public void setOaTaskSubmissionType(String type) {
    this.setString(OA_TASK_SUBMISSION_TYPE, type);
  }

  public void setOaTaskSubmissionSubtype(String subtype) {
    this.setString(OA_TASK_SUBMISSION_SUBTYPE, subtype);
  }
}
