package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.sql.Date;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author ashish.
 */

@Table("oa_references")
public class AJEntityOAReference extends Model {

  private static final String OA_ID = "oa_id";
  private static final String OA_REFERENCE_TYPE = "oa_reference_type";
  private static final String OA_REFERENCE_SUBTYPE = "oa_reference_subtype";
  private static final String LOCATION = "location";
  private static final String CREATED_AT = "created_at";
  private static final String UPDATED_AT = "updated_at";

  public String getOaId() {
    return this.getString(OA_ID);
  }

  public String getOaReferenceType() {
    return this.getString(OA_REFERENCE_TYPE);
  }

  public String getOaReferenceSubtype() {
    return this.getString(OA_REFERENCE_SUBTYPE);
  }

  public String getLocation() {
    return this.getString(LOCATION);
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

  public void setOaReferenceType(String type) {
    this.setString(OA_REFERENCE_TYPE, type);
  }

  public void setOaReferenceSubtype(String subtype) {
    this.setString(OA_REFERENCE_SUBTYPE, subtype);
  }

  public void setLocation(String location) {
    this.setString(LOCATION, location);
  }
}
