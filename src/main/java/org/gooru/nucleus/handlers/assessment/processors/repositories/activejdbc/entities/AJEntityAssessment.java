package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Created by ashish on 7/1/16.
 */
@Table("collection")
public class AJEntityAssessment extends Model  {

  public static final String SELECT_FOR_VALIDATE =
    "select id, creator_id, publish_date, is_deleted from collection where format = ?::content_container_type and id = ? and is_deleted = ?";
  public static final String ASSESSMENT = "assessment";
  public static final String CREATOR_ID = "creator_id";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String IS_DELETED = "is_deleted";
  public static final String MODIFIER_ID = "modifier_id";
}
