package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.ConverterRegistry;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldSelector;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldValidator;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.ValidatorRegistry;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class AssessmentDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  public static final String AUTH_FILTER = "id = ?::uuid and (owner_id = ?::uuid or collaborator ?? ?);";
  public static final String PUBLISHED_FILTER = "id = ?::uuid and publish_status = 'published'::publish_status_type;";
  public static final String FETCH_ASSESSMENT_QUERY =
      "select id, title, owner_id, creator_id, original_creator_id, original_collection_id, publish_date, subformat, "
          + "publish_status, thumbnail, learning_objective, license, metadata, taxonomy, setting, grading, gut_codes, "
          + "visible_on_profile, collaborator, course_id, unit_id, lesson_id, tenant, tenant_root, primary_language "
          + "from collection where id = ?::uuid and format = 'assessment'::content_container_type and is_deleted = false";
  public static final String COURSE_COLLABORATOR_QUERY =
      "select collaborator from course where id = ?::uuid and is_deleted = false";
  public static final List<String> FETCH_QUERY_FIELD_LIST = Arrays
      .asList("id", "title", "owner_id", "creator_id", "original_creator_id",
          "original_collection_id", "publish_date", "thumbnail", "learning_objective", "license",
          "metadata", "taxonomy", "setting", "grading", "primary_language", "visible_on_profile",
          "course_id", "unit_id", "lesson_id", "subformat", "gut_codes");
  // Queries used
  public static final String AUTHORIZER_QUERY =
      "select id, course_id, unit_id, lesson_id, owner_id, creator_id, publish_date, collaborator, grading, tenant,"
          + " tenant_root, taxonomy from collection where format = ?::content_container_type"
          + " and id = ?::uuid and is_deleted = ?";

  private AssessmentDao() {
    throw new AssertionError();
  }

  static final Set<String> EDITABLE_FIELDS = new HashSet<>(Arrays
      .asList(
          AJEntityAssessment.TITLE, AJEntityAssessment.THUMBNAIL,
          AJEntityAssessment.LEARNING_OBJECTIVE, AJEntityAssessment.METADATA,
          AJEntityAssessment.TAXONOMY, AJEntityAssessment.URL, AJEntityAssessment.LOGIN_REQUIRED,
          AJEntityAssessment.VISIBLE_ON_PROFILE,
          AJEntityAssessment.SETTING, AJEntityAssessment.PRIMARY_LANGUAGE));
  private static final Set<String> CREATABLE_FIELDS = EDITABLE_FIELDS;
  private static final Set<String> MANDATORY_FIELDS = new HashSet<>(Collections.singletonList(
      AJEntityAssessment.TITLE));
  private static final Set<String> ADD_QUESTION_FIELDS = new HashSet<>(Collections.singletonList(
      AJEntityAssessment.ID));
  private static final Set<String> COLLABORATOR_FIELDS = new HashSet<>(Collections.singletonList(
      AJEntityAssessment.COLLABORATOR));
  private static final Set<String> REORDER_FIELDS = new HashSet<>(
      Collections.singletonList(AJEntityAssessment.REORDER_PAYLOAD_KEY));
  private static final Set<String> AGGREGATE_TAGS_FIELDS = new HashSet<>(Collections.singletonList(
      AJEntityAssessment.TAXONOMY));

  public static FieldSelector editFieldSelector() {
    return () -> Collections.unmodifiableSet(EDITABLE_FIELDS);
  }

  public static FieldSelector aggregateTagsFieldSelector() {
    return () -> Collections.unmodifiableSet(AGGREGATE_TAGS_FIELDS);
  }

  public static FieldSelector reorderFieldSelector() {
    return new FieldSelector() {
      @Override
      public Set<String> allowedFields() {
        return Collections.unmodifiableSet(REORDER_FIELDS);
      }

      @Override
      public Set<String> mandatoryFields() {
        return Collections.unmodifiableSet(REORDER_FIELDS);
      }
    };
  }

  public static FieldSelector createFieldSelector() {
    return new FieldSelector() {
      @Override
      public Set<String> allowedFields() {
        return Collections.unmodifiableSet(CREATABLE_FIELDS);
      }

      @Override
      public Set<String> mandatoryFields() {
        return Collections.unmodifiableSet(MANDATORY_FIELDS);
      }
    };
  }

  public static FieldSelector editCollaboratorFieldSelector() {
    return new FieldSelector() {
      @Override
      public Set<String> mandatoryFields() {
        return Collections.emptySet();
      }

      @Override
      public Set<String> allowedFields() {
        return Collections.unmodifiableSet(COLLABORATOR_FIELDS);
      }
    };
  }

  public static FieldSelector addQuestionFieldSelector() {
    return () -> Collections.unmodifiableSet(ADD_QUESTION_FIELDS);
  }

  public static ValidatorRegistry getValidatorRegistry() {
    return new AssessmentValidationRegistry();
  }

  public static ConverterRegistry getConverterRegistry() {
    return new AssessmentConverterRegistry();
  }

  private static class AssessmentValidationRegistry implements ValidatorRegistry {

    @Override
    public FieldValidator lookupValidator(String fieldName) {
      return AJEntityAssessment.validatorRegistry.get(fieldName);
    }
  }

  private static class AssessmentConverterRegistry implements ConverterRegistry {

    @Override
    public FieldConverter lookupConverter(String fieldName) {
      return AJEntityAssessment.converterRegistry.get(fieldName);
    }
  }

  public static AJEntityAssessment fetchAssessmentWithAuthorizerQuery(String assessmentId) {
    LazyList<AJEntityAssessment> assessments = AJEntityAssessment
        .findBySQL(AssessmentDao.AUTHORIZER_QUERY, AJEntityAssessment.ASSESSMENT,
            assessmentId, false);
    if (assessments.size() < 1) {
      LOGGER.warn("Assessment id: {} not present in DB", assessmentId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(
              RESOURCE_BUNDLE.getString("assessment.id") + assessmentId));
    }
    return assessments.get(0);
  }
}
