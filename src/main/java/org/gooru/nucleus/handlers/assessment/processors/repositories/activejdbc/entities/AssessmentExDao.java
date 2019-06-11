package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.LOGIN_REQUIRED;
import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.TITLE;
import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldSelector;

/**
 * @author ashish.
 */

public final class AssessmentExDao {

  public static final String FETCH_EXTERNAL_ASSSESSMENT_QUERY =
      "select id, title, owner_id, creator_id, original_creator_id, original_collection_id, thumbnail, subformat, "
          + "publish_status, learning_objective, metadata, taxonomy, visible_on_profile, url, login_required, "
          + "course_id, unit_id, lesson_id, tenant, tenant_root, primary_language from collection where id = ?::uuid and format = "
          + "'assessment-external'::content_container_type and is_deleted = false";
  public static final List<String> FETCH_EA_QUERY_FIELD_LIST = Arrays
      .asList("id", "title", "owner_id", "creator_id", "original_creator_id",
          "original_collection_id", "thumbnail",
          "learning_objective", "metadata", "taxonomy", "visible_on_profile", "url",
          "login_required", "course_id", "primary_language",
          "unit_id", "lesson_id", "subformat");
  public static final String FETCH_ASMT_ASMTEX_OA_QUERY =
      "select id, gut_codes from collection where id  = ANY(?::uuid[]) and "
          + " (format = 'assessment'::content_container_type OR format = 'assessment-external'::content_container_type"
          + " OR format = 'offline-activity'::content_container_type ) "
          + " and gut_codes[1] is not null and is_deleted = false";
  private static final Set<String> CREATABLE_EX_FIELDS = AssessmentDao.EDITABLE_FIELDS;
  private static final Set<String> MANDATORY_EX_FIELDS = new HashSet<>(
      Arrays.asList(TITLE, URL, LOGIN_REQUIRED));


  public static FieldSelector editExFieldSelector() {
    return () -> Collections.unmodifiableSet(AssessmentDao.EDITABLE_FIELDS);
  }

  public static FieldSelector createExFieldSelector() {
    return new FieldSelector() {
      @Override
      public Set<String> allowedFields() {
        return Collections.unmodifiableSet(CREATABLE_EX_FIELDS);
      }

      @Override
      public Set<String> mandatoryFields() {
        return Collections.unmodifiableSet(MANDATORY_EX_FIELDS);
      }
    };
  }


  private AssessmentExDao() {
    throw new AssertionError();
  }


}
