package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.ConverterRegistry;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldSelector;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldValidator;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.ValidatorRegistry;

/**
 * @author ashish.
 */

public final class AssessmentDao {

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
  private static final Set<String> MANDATORY_FIELDS = new HashSet<>(Arrays.asList(
      AJEntityAssessment.TITLE));
  private static final Set<String> ADD_QUESTION_FIELDS = new HashSet<>(Arrays.asList(
      AJEntityAssessment.ID));
  private static final Set<String> COLLABORATOR_FIELDS = new HashSet<>(Arrays.asList(
      AJEntityAssessment.COLLABORATOR));
  private static final Set<String> REORDER_FIELDS = new HashSet<>(
      Arrays.asList(AJEntityAssessment.REORDER_PAYLOAD_KEY));
  private static final Set<String> AGGREGATE_TAGS_FIELDS = new HashSet<>(Arrays.asList(
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
        return Collections.unmodifiableSet(COLLABORATOR_FIELDS);
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
}
