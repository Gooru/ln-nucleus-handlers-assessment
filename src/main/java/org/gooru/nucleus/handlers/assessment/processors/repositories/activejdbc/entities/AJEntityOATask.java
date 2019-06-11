package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.sql.Date;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author ashish.
 */

@Table("oa_tasks")
public class AJEntityOATask extends Model {

  private static final String OA_ID = "oa_id";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String CREATED_AT = "created_at";
  private static final String UPDATED_AT = "updated_at";

  public String getOaId() {
    return this.getString(OA_ID);
  }

  public String getTitle() {
    return this.getString(TITLE);
  }

  public String getDescription() {
    return this.getString(DESCRIPTION);
  }

  public Date getCreatedAt() {
    return this.getDate(CREATED_AT);
  }

  public Date getUpdatedAt() {
    return this.getDate(UPDATED_AT);
  }

  public void setOaId(String oaId) {
    this.set(OA_ID, FieldConverter.convertFieldToUuid(oaId));
  }

  public void setTitle(String title) {
    this.setString(TITLE, title);
  }

  public void setDescription(String description) {
    this.setString(DESCRIPTION, description);
  }
}
