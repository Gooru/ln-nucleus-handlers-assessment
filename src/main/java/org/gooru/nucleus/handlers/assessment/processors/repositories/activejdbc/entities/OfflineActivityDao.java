package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.ConverterRegistry;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbutils.GUTCodeLookupHelper;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entitybuilders.EntityBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldSelector;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldValidator;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.ValidatorRegistry;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.assessment.processors.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class OfflineActivityDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(OfflineActivityDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private static final Set<String> EDITABLE_FIELDS = new HashSet<>(Arrays
      .asList(
          AJEntityAssessment.TITLE, AJEntityAssessment.THUMBNAIL,
          AJEntityAssessment.LEARNING_OBJECTIVE, AJEntityAssessment.METADATA,
          AJEntityAssessment.TAXONOMY, AJEntityAssessment.URL, AJEntityAssessment.LOGIN_REQUIRED,
          AJEntityAssessment.VISIBLE_ON_PROFILE, AJEntityAssessment.SETTING,
          AJEntityAssessment.PRIMARY_LANGUAGE, AJEntityAssessment.TAXONOMY_TO_BUILD,
          AJEntityAssessment.REFERENCE, AJEntityAssessment.DURATION_HOURS,
          AJEntityAssessment.MAX_SCORE, AJEntityAssessment.EXEMPLAR));

  private static final Set<String> CREATABLE_FIELDS = new HashSet<>(Arrays
      .asList(
          AJEntityAssessment.TITLE, AJEntityAssessment.THUMBNAIL,
          AJEntityAssessment.LEARNING_OBJECTIVE, AJEntityAssessment.METADATA,
          AJEntityAssessment.TAXONOMY, AJEntityAssessment.URL, AJEntityAssessment.LOGIN_REQUIRED,
          AJEntityAssessment.VISIBLE_ON_PROFILE, AJEntityAssessment.SETTING,
          AJEntityAssessment.PRIMARY_LANGUAGE, AJEntityAssessment.TAXONOMY_TO_BUILD,
          AJEntityAssessment.REFERENCE, AJEntityAssessment.DURATION_HOURS,
          AJEntityAssessment.MAX_SCORE, AJEntityAssessment.EXEMPLAR, AJEntityAssessment.SUBFORMAT));

  private static final Set<String> MANDATORY_FIELDS = new HashSet<>(Arrays.asList(
      AJEntityAssessment.TITLE, AJEntityAssessment.TAXONOMY, AJEntityAssessment.SUBFORMAT));

  private OfflineActivityDao() {
    throw new AssertionError();
  }

  private static final String FETCH_QUERY_FILTER =
      " format = 'offline-activity'::content_container_type and id = ?::uuid and is_deleted = false";

  public static final List<String> FETCH_FIELD_LIST = Arrays
      .asList("id", "title", "owner_id", "creator_id", "original_creator_id", "url",
          "login_required",
          "original_collection_id", "publish_date", "thumbnail", "learning_objective", "license",
          "metadata", "taxonomy", "setting", "grading", "primary_language", "visible_on_profile",
          "course_id", "unit_id", "lesson_id", "subformat", "exemplar", "reference",
          "duration_hours", "max_score");

  public static FieldSelector editFieldSelector() {
    return () -> Collections.unmodifiableSet(EDITABLE_FIELDS);
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


  public static AJEntityAssessment fetchOfflineActivityById(String id) {
    List<AJEntityAssessment> offlineActivities = AJEntityAssessment.where(FETCH_QUERY_FILTER, id);
    if (offlineActivities == null || offlineActivities.isEmpty()) {
      LOGGER.warn("Offline activity id: {} not present in DB", id);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found") + " : " + id));
    }
    return offlineActivities.get(0);

  }

  public static String createOfflineActivity(ProcessorContext context) {
    AJEntityAssessment offlineActivity = new AJEntityAssessment();
    autoPopulateFields(context, offlineActivity);

    new EntityBuilder<AJEntityAssessment>() {
    }
        .build(offlineActivity, context.request(), AssessmentDao.getConverterRegistry());

    populateGutCodes(offlineActivity, context.request());

    boolean result = offlineActivity.save();
    if (!result) {
      LOGGER.error("Offline activity creation failed for user '{}'", context.userId());
      if (offlineActivity.hasErrors()) {
        throw new MessageResponseWrapperException(
            MessageResponseFactory.createValidationErrorResponse(
                ModelErrorFormatter.formattedError(offlineActivity)));
      }
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createInternalErrorResponse());
    }
    return offlineActivity.getId().toString();
  }

  public static void updateOfflineActivity(OAProcessorContext context) {
    AJEntityAssessment offlineActivity = new AJEntityAssessment();
    offlineActivity.setIdWithConverter(context.oaId());
    offlineActivity.setModifierId(context.userId());
    new EntityBuilder<AJEntityAssessment>() {
    }.build(offlineActivity, context.request(), AssessmentDao.getConverterRegistry());

    populateGutCodes(offlineActivity, context.request());

    boolean result = offlineActivity.save();
    if (!result) {
      LOGGER.error("Offline activity with id '{}' failed to save", context.oaId());
      if (offlineActivity.hasErrors()) {
        throw new MessageResponseWrapperException(
            MessageResponseFactory.createValidationErrorResponse(
                ModelErrorFormatter.formattedError(offlineActivity)));
      }
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createInternalErrorResponse());
    }
  }

  private static void populateGutCodes(AJEntityAssessment offlineActivity, JsonObject request) {
    JsonObject newTags = request.getJsonObject(AJEntityAssessment.TAXONOMY);
    if (newTags != null && !newTags.isEmpty()) {
      Map<String, String> frameworkToGutCodeMapping =
          GUTCodeLookupHelper.populateGutCodesToTaxonomyMapping(newTags.fieldNames());
      offlineActivity
          .setGutCodes(CommonUtils.toPostgresArrayString(frameworkToGutCodeMapping.keySet()));
    }
  }

  private static void autoPopulateFields(ProcessorContext context,
      AJEntityAssessment offlineActivity) {
    offlineActivity.setModifierId(context.userId());
    offlineActivity.setOwnerId(context.userId());
    offlineActivity.setCreatorId(context.userId());
    offlineActivity.setTypeOfflineActivity();
    offlineActivity.setTenant(context.tenant());
    String tenantRoot = context.tenantRoot();
    if (tenantRoot != null && !tenantRoot.isEmpty()) {
      offlineActivity.setTenantRoot(tenantRoot);
    }
  }

}
