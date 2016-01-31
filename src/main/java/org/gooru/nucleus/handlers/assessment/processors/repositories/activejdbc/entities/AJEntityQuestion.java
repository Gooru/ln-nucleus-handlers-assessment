package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Created by ashish on 7/1/16.
 */
@Table("content")
public class AJEntityQuestion extends Model {
  public static final String TABLE_QUESTION = "content";
  public static final String QUESTION_FOR_ADD_FILTER =
    "id = ?::uuid and is_deleted = false and content_format = 'question'::content_format_type and course_id is null and collection_id is null and " +
      "creator_id = ?::uuid";
  public static final String ADD_QUESTION_QUERY =
    "update content set collection_id = ?::uuid, modifier_id = ?::uuid, updated_at = now(), sequence_id = ? where id = ?::uuid and is_deleted = " +
      "false and content_format = 'question'::content_format_type and course_id is null and collection_id is null and creator_id = ?::uuid";
  public static final String MAX_QUESTION_SEQUENCE_QUERY = "select max(sequence_id) from content where collection_id = ?::uuid";
  public static final String DELETE_CONTENTS_QUERY =
    "update content set is_deleted = true, modifier_id = ?::uuid where content_format = 'question'::content_format_type and collection_id = ?::uuid" +
      " and is_deleted = false";
  public static final String OPEN_ENDED_QUESTION_FILTER =
    "collection_id = ?::uuid and content_subformat = 'open_ended_question'::content_subformat_type and is_deleted = false";
}
