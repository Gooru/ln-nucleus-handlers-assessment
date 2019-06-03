package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On: 09-May-2017
 */
@Table("rubric")
public class AJEntityRubric extends Model {

  private static final String ID = "id";
  public static final String CONTENT_ID = "content_id";
  public static final String IS_RUBRIC = "is_rubric";
  private static final String SCORING = "scoring";
  public static final String MAX_SCORE = "max_score";
  private static final String COURSE_ID = "course_id";
  private static final String UNIT_ID = "unit_id";
  private static final String LESSON_ID = "lesson_id";
  private static final String COLLECTION_ID = "collection_id";
  private static final String GRADER = "grader";
  private static final String GRADER_STUDENT = "Self";
  private static final String GRADER_TEACHER = "Teacher";
  private static final String MODIFIER_ID = "modifier_id";
  private static final String IS_DELETED = "is_deleted";


  public static final String DELETE_RUBRICS_QUERY =
      "UPDATE rubric SET is_deleted = true, modifier_id = ?::uuid WHERE collection_id = ?::uuid AND is_deleted = false";

  public static final String FETCH_RUBRIC_SUMMARY =
      "SELECT id, content_id, is_rubric, scoring, max_score FROM rubric WHERE content_id = ANY(?::uuid[]) AND is_deleted = false";

  public static final List<String> RUBRIC_SUMMARY =
      Arrays.asList(ID, CONTENT_ID, IS_RUBRIC, SCORING, MAX_SCORE);


  public String getCourseId() {
    return this.getString(COURSE_ID);
  }

  public String getUnitId() {
    return this.getString(UNIT_ID);
  }

  public String getLessonId() {
    return this.getString(LESSON_ID);
  }

  public String getCollectionId() {
    return this.getString(COLLECTION_ID);
  }

  public boolean isGraderSet() {
    return this.getString(GRADER) != null;
  }

  public boolean isTeacherGraded() {
    String grader = this.getString(GRADER);
    return GRADER_TEACHER.equals(grader);
  }

  public boolean isStudentGraded() {
    String grader = this.getString(GRADER);
    return GRADER_STUDENT.equals(grader);
  }

  public void setModifierId(String modifierId) {
    this.set(MODIFIER_ID, FieldConverter.convertFieldToUuid(modifierId));
  }

  public void markDeleted() {
    this.setBoolean(IS_DELETED, true);
  }

  public static String getTeacherGraderValue() {
    return GRADER_TEACHER;
  }

  public static String getStudentGraderValue() {
    return GRADER_STUDENT;
  }

  public Integer getMaxScore() {
    return this.getInteger(MAX_SCORE);
  }
}
