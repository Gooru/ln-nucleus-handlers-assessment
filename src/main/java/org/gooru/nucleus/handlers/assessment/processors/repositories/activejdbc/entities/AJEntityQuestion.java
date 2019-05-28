package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Created by ashish on 7/1/16.
 */
@Table("content")
public class AJEntityQuestion extends Model {

  public static final String QUESTION = "question";
  public static final String TABLE_QUESTION = "content";
  public static final String QUESTION_FOR_ADD_FILTER =
      "id = ?::uuid and is_deleted = false and content_format = 'question'::content_format_type and course_id is null and collection_id is null and "
          + "creator_id = ?::uuid";
  public static final String ADD_QUESTION_QUERY =
      "update content set collection_id = ?::uuid, course_id = ?::uuid, unit_id = ?::uuid, lesson_id = ?::uuid, modifier_id = ?::uuid, updated_at = "
          + "now(), sequence_id = ? where id = ?::uuid and is_deleted = false and content_format = 'question'::content_format_type and course_id is null "
          + "and collection_id is null and creator_id = ?::uuid";
  public static final String ADD_RUBRIC_QUERY =
      "update rubric set collection_id = ?::uuid, course_id = ?::uuid, unit_id = ?::uuid, lesson_id = ?::uuid,"
          + " modifier_id = ?::uuid, updated_at = now() where content_id = ?::uuid and is_deleted = false and course_id is null "
          + "and collection_id is null and creator_id = ?::uuid";
  public static final String MAX_QUESTION_SEQUENCE_QUERY =
      "select max(sequence_id) from content where collection_id = ?::uuid";
  public static final String DELETE_CONTENTS_QUERY =
      "update content set is_deleted = true, modifier_id = ?::uuid where content_format = 'question'::content_format_type and collection_id = ?::uuid"
          + " and is_deleted = false";
  public static final String OPEN_ENDED_QUESTION_FILTER =
      "collection_id = ?::uuid and content_subformat = 'open_ended_question'::content_subformat_type and is_deleted = false";
  public static final String FETCH_QUESTION_SUMMARY_QUERY =
      "select id, title, creator_id, original_creator_id, publish_date, content_subformat, answer, metadata, taxonomy, narration, "
          + "hint_explanation_detail, thumbnail, sequence_id, visible_on_profile, description, max_score from content where collection_id = ?::uuid and "
          + " is_deleted = false order by sequence_id asc";

  public static final String QUESTIONS_FOR_ASSESSMENT_QUERY =
      "select id from content where collection_id = ?::uuid and is_deleted = false";
  public static final String REORDER_QUERY =
      "update content set sequence_id = ?, modifier_id = ?::uuid, updated_at = now() where id = ?::uuid and collection_id = ?::uuid and is_deleted = "
          + "false";

  public static final List<String> FETCH_QUESTION_SUMMARY_FIELDS = Arrays
      .asList("id", "title", "creator_id",
          "original_creator_id", "publish_date", "content_subformat", "answer", "metadata",
          "taxonomy", "narration",
          "hint_explanation_detail", "thumbnail", "sequence_id", "visible_on_profile",
          "description", "max_score");
  public static final String SEQUENCE_ID = "sequence_id";
  public static final String MAX_SCORE = "max_score";
  public static final String ID = "id";
  public static final List<String> RUBRIC_ASSOCIATION_ALLOWED_TYPES = Collections
      .singletonList("open_ended_question");
  public static final String CONTENT_SUBFORMAT = "content_subformat";
}
