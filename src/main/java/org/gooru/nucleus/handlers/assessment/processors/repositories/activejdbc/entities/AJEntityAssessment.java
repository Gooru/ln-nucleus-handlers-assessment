package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import io.vertx.core.json.JsonArray;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.converters.FieldConverter;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbutils.OASubformatValidationUtil;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldValidator;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.ReorderFieldValidator;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 7/1/16.
 */
@Table("collection")
public class AJEntityAssessment extends Model {

  private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityAssessment.class);
  // Variables used
  public static final String ID = "id";
  public static final String ASSESSMENT_EXTERNAL = "assessment-external";
  public static final String ASSESSMENT = "assessment";
  private static final String CREATOR_ID = "creator_id";
  private static final String PUBLISH_DATE = "publish_date";
  private static final String IS_DELETED = "is_deleted";
  private static final String MODIFIER_ID = "modifier_id";
  private static final String OWNER_ID = "owner_id";
  static final String TITLE = "title";
  static final String THUMBNAIL = "thumbnail";
  static final String LEARNING_OBJECTIVE = "learning_objective";
  private static final String FORMAT = "format";
  static final String METADATA = "metadata";
  public static final String TAXONOMY = "taxonomy";
  static final String URL = "url";
  static final String LOGIN_REQUIRED = "login_required";
  static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String COLLABORATOR = "collaborator";
  static final String SETTING = "setting";
  private static final String COURSE_ID = "course_id";
  private static final String UNIT_ID = "unit_id";
  private static final String LESSON_ID = "lesson_id";
  public static final String GRADING = "grading";
  public static final String TABLE_COURSE = "course";
  public static final String UPDATED_AT = "updated_at";
  private static final String ASSESSMENT_TYPE_NAME = "content_container_type";
  private static final String ASSESSMENT_TYPE_VALUE = "assessment";
  private static final String ASSESSMENT_EX_TYPE_VALUE = "assessment-external";
  private static final String ASSESSMENT_TYPE_OFFLINE = "offline-activity";
  private static final String GRADING_TYPE_NAME = "grading_type";
  public static final String GRADING_TYPE_TEACHER = "teacher";
  public static final String GRADING_TYPE_SYSTEM = "system";
  public static final String REORDER_PAYLOAD_KEY = "order";
  private static final String LICENSE = "license";
  private static final String TENANT = "tenant";
  private static final String TENANT_ROOT = "tenant_root";
  private static final String PUBLISH_STATUS = "publish_status";
  private static final String PUBLISH_STATUS_PUBLISHED = "published";
  private static final String GUT_CODES = "gut_codes";
  static final String PRIMARY_LANGUAGE = "primary_language";
  static final String TAXONOMY_TO_BUILD = "taxonomy_to_build";
  static final String REFERENCE = "reference";
  static final String DURATION_HOURS = "duration_hours";
  static final String MAX_SCORE = "max_score";
  static final String EXEMPLAR = "exemplar";
  static final String SUBFORMAT = "subformat";

  private static final String TEXT_ARRAY_TYPE = "text[]";

  static final Map<String, FieldValidator> validatorRegistry;
  static final Map<String, FieldConverter> converterRegistry;

  static {
    validatorRegistry = initializeValidators();
    converterRegistry = initializeConverters();
  }

  private static Map<String, FieldConverter> initializeConverters() {
    Map<String, FieldConverter> converterMap = new HashMap<>();
    converterMap.put(ID, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    converterMap.put(METADATA, (FieldConverter::convertFieldToJson));
    converterMap.put(TAXONOMY, (FieldConverter::convertFieldToJson));
    converterMap.put(TAXONOMY_TO_BUILD, (FieldConverter::convertFieldToJson));
    converterMap
        .put(CREATOR_ID, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    converterMap
        .put(MODIFIER_ID, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    converterMap
        .put(OWNER_ID, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    converterMap
        .put(FORMAT, (fieldValue -> FieldConverter
            .convertFieldToNamedType(fieldValue, ASSESSMENT_TYPE_NAME)));
    converterMap.put(COLLABORATOR, (FieldConverter::convertFieldToJson));
    converterMap.put(SETTING, FieldConverter::convertFieldToJson);
    converterMap
        .put(GRADING,
            (fieldValue -> FieldConverter.convertFieldToNamedType(fieldValue, GRADING_TYPE_NAME)));
    converterMap
        .put(TENANT, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    converterMap
        .put(TENANT_ROOT, (fieldValue -> FieldConverter.convertFieldToUuid((String) fieldValue)));
    return Collections.unmodifiableMap(converterMap);
  }

  private static Map<String, FieldValidator> initializeValidators() {
    Map<String, FieldValidator> validatorMap = new HashMap<>();
    validatorMap.put(ID, (FieldValidator::validateUuid));
    validatorMap.put(TITLE, (value) -> FieldValidator.validateString(value, 1000));
    validatorMap.put(THUMBNAIL, (value) -> FieldValidator.validateStringIfPresent(value, 2000));
    validatorMap
        .put(LEARNING_OBJECTIVE, (value) -> FieldValidator.validateStringIfPresent(value, 20000));
    validatorMap.put(METADATA, FieldValidator::validateJsonIfPresent);
    validatorMap.put(TAXONOMY, FieldValidator::validateJsonIfPresent);
    validatorMap.put(TAXONOMY_TO_BUILD, FieldValidator::validateJsonIfPresent);
    validatorMap.put(SETTING, FieldValidator::validateJsonIfPresent);
    validatorMap.put(URL, (value) -> FieldValidator.validateStringIfPresent(value, 2000));
    validatorMap.put(LOGIN_REQUIRED, FieldValidator::validateBooleanIfPresent);
    validatorMap.put(VISIBLE_ON_PROFILE, FieldValidator::validateBooleanIfPresent);
    validatorMap.put(MAX_SCORE, FieldValidator::validateOptionalInteger);
    validatorMap.put(DURATION_HOURS, FieldValidator::validateOptionalInteger);
    validatorMap.put(COLLABORATOR,
        (value) -> FieldValidator
            .validateDeepJsonArrayIfPresent(value, FieldValidator::validateUuid));
    validatorMap.put(REORDER_PAYLOAD_KEY, new ReorderFieldValidator());
    validatorMap.put(TENANT, (FieldValidator::validateUuid));
    validatorMap.put(TENANT_ROOT, (FieldValidator::validateUuid));
    validatorMap.put(PRIMARY_LANGUAGE, FieldValidator::validateLanguageIfPresent);
    validatorMap.put(SUBFORMAT, OASubformatValidationUtil::validateOASubformat);
    return Collections.unmodifiableMap(validatorMap);
  }

  public void setModifierId(String modifier) {
    setFieldUsingConverter(MODIFIER_ID, modifier);
  }

  public void setCreatorId(String creator) {
    setFieldUsingConverter(CREATOR_ID, creator);
  }

  public void setOwnerId(String owner) {
    setFieldUsingConverter(OWNER_ID, owner);
  }

  public void setIdWithConverter(String id) {
    setFieldUsingConverter(ID, id);
  }

  public void setTenant(String tenant) {
    setFieldUsingConverter(TENANT, tenant);
  }

  public void setTenantRoot(String tenantRoot) {
    setFieldUsingConverter(TENANT_ROOT, tenantRoot);
  }

  public void setGrading(String grading) {
    setFieldUsingConverter(GRADING, grading);
  }

  public void setLicense(Integer code) {
    this.set(LICENSE, code);
  }

  public void setTypeAssessment() {
    setFieldUsingConverter(FORMAT, ASSESSMENT_TYPE_VALUE);
  }

  public void setTypeOfflineActivity() {
    setFieldUsingConverter(FORMAT, ASSESSMENT_TYPE_OFFLINE);
  }

  public void setTypeExAssessment() {
    setFieldUsingConverter(FORMAT, ASSESSMENT_EX_TYPE_VALUE);
  }

  public void setTaxonomy(String taxonomy) {
    setFieldUsingConverter(TAXONOMY, taxonomy);
  }

  public void setGutCodes(String gutCodes) {
    setPGObject(GUT_CODES, TEXT_ARRAY_TYPE, gutCodes);
  }

  public List<String> getGutCodes() throws SQLException {
    java.sql.Array sqlArray = (java.sql.Array) this.get(GUT_CODES);
    if (sqlArray == null) {
      return Collections.emptyList();
    }
    String[] resultArray = (String[]) (sqlArray).getArray();
    if (resultArray == null) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(resultArray);
    }
  }

  private void setPGObject(String field, String type, String value) {
    PGobject pgObject = new PGobject();
    pgObject.setType(type);
    try {
      pgObject.setValue(value);
      this.set(field, pgObject);
    } catch (SQLException e) {
      LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
      this.errors().put(field, value);
    }
  }

  private void setFieldUsingConverter(String fieldName, Object fieldValue) {
    FieldConverter fc = converterRegistry.get(fieldName);
    if (fc != null) {
      this.set(fieldName, fc.convertField(fieldValue));
    } else {
      this.set(fieldName, fieldValue);
    }
  }

  public boolean isAssessmentPublished() {
    return Objects.equals(this.getString(PUBLISH_STATUS), PUBLISH_STATUS_PUBLISHED);
  }

  public void setIsDeleted(boolean deleted) {
    this.setBoolean(AJEntityAssessment.IS_DELETED, deleted);
  }

  public Date getPublishDate() {
    return this.getDate(AJEntityAssessment.PUBLISH_DATE);
  }

  public String getCourseId() {
    return this.getString(COURSE_ID);
  }

  public String getUnitId() {
    return this.getString(UNIT_ID);
  }

  public String getLessonId() {
    return this.getString(LESSON_ID);
  }

  public String getTenant() {
    return this.getString(TENANT);
  }

  public String getTenantRoot() {
    return this.getString(TENANT_ROOT);
  }

  public String getOwnerId() {
    return this.getString(AJEntityAssessment.OWNER_ID);
  }

  public String getReference() {
    return this.getString(REFERENCE);
  }

  public Integer getDurationHours() {
    return this.getInteger(DURATION_HOURS);
  }

  public Integer getMaxScore() {
    return this.getInteger(MAX_SCORE);
  }

  public void setMaxScore(Integer maxScore) {
    this.setInteger(MAX_SCORE, maxScore);
  }

  public void setDefaultMaxScore() {
    this.setMaxScore(1);
  }

  public String getExemplar() {
    return this.getString(EXEMPLAR);
  }

  public JsonArray getCollaborators() {
    String currentCollaboratorsAsString = this.getString(COLLABORATOR);
    return (currentCollaboratorsAsString != null && !currentCollaboratorsAsString.isEmpty()
        ? new JsonArray(currentCollaboratorsAsString) : new JsonArray());

  }
}
