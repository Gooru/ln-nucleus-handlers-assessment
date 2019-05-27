package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class RubricDao {


  private static final String RUBRIC_FETCH = "id = ?::uuid and is_deleted = false and is_rubric = true";
  private static final String RUBRIC_FETCH_FOR_COLLECTION = "collection_id = ?::uuid and is_deleted = false and is_rubric = true";
  private static final String RUBRIC_COPY =
      "INSERT INTO rubric(id, title, url, is_remote, description, categories, feedback_guidance, overall_feedback_required, "
          + "    creator_id, modifier_id, original_creator_id, original_rubric_id, "
          + "    parent_rubric_id, metadata, taxonomy, "
          + "    gut_codes, thumbnail, created_at, updated_at, tenant, tenant_root, visible_on_profile, creator_system, "
          + "    course_id, unit_id, lesson_id, collection_id, is_rubric, scoring, increment, grader, primary_language, max_score) "
          + "SELECT ?::uuid, title, url, is_remote, description, categories, feedback_guidance, overall_feedback_required, "
          + "    ?::uuid, ?::uuid, coalesce(original_creator_id, creator_id) as original_creator_id, coalesce(original_rubric_id, ?::uuid) as original_rubric_id, "
          + "    ?::uuid, metadata, taxonomy, "
          + "    gut_codes, thumbnail, created_at, updated_at, ?::uuid, ?::uuid, visible_on_profile, creator_system, "
          + "    ?::uuid, ?::uuid, ?::uuid, ?::uuid, is_rubric, scoring, increment, ?, primary_language, max_score "
          + "FROM rubric WHERE id = ?::uuid AND is_deleted = false AND is_rubric = true";

  private static final Logger LOGGER = LoggerFactory.getLogger(RubricDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private RubricDao() {
    throw new AssertionError();
  }

  public static void associateTeacherRubricToOA(OAProcessorContext context,
      AJEntityAssessment offlineActivity, AJEntityRubric sourceRubric) {
    deleteTeacherRubricAssociatedWithCollection(context.oaId(), context.userId());
    copySpecifiedRubricAsTeacherRubricForSpecifiedOA(context.processorContext(), offlineActivity,
        sourceRubric);
  }

  public static void associateStudentRubricToOA(OAProcessorContext context,
      AJEntityAssessment offlineActivity, AJEntityRubric sourceRubric) {
    deleteStudentRubricAssociatedWithCollection(context.oaId(), context.userId());
    copySpecifiedRubricAsStudentRubricForSpecifiedOA(context.processorContext(), offlineActivity,
        sourceRubric);
  }

  public static AJEntityRubric fetchRubricById(String rubricId) {
    List<AJEntityRubric> rubrics = AJEntityRubric.where(RUBRIC_FETCH, rubricId);
    if (rubrics == null || rubrics.isEmpty()) {
      LOGGER.warn("Rubric id : {} not present in DB as real rubric", rubricId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found") + " : " + rubricId));
    }
    return rubrics.get(0);
  }

  public static List<AJEntityRubric> fetchRubricsForCollection(String collectionId) {
    return AJEntityRubric.where(RUBRIC_FETCH_FOR_COLLECTION, collectionId);
  }

  public static void deleteTeacherRubricAssociatedWithCollection(String collectionId, String user) {
    List<AJEntityRubric> rubrics = fetchRubricsForCollection(collectionId);
    if (rubrics != null && !rubrics.isEmpty()) {
      for (AJEntityRubric rubric : rubrics) {
        if (rubric.isGraderSet() && rubric.isTeacherGraded()) {
          deleteRubric(rubric, user);
        }
      }
    }
  }

  public static void deleteStudentRubricAssociatedWithCollection(String collectionId, String user) {
    List<AJEntityRubric> rubrics = fetchRubricsForCollection(collectionId);
    if (rubrics != null && !rubrics.isEmpty()) {
      for (AJEntityRubric rubric : rubrics) {
        if (rubric.isGraderSet() && rubric.isStudentGraded()) {
          deleteRubric(rubric, user);
        }
      }
    }
  }

  public static void copySpecifiedRubricAsTeacherRubricForSpecifiedOA(ProcessorContext context,
      AJEntityAssessment collection, AJEntityRubric sourceRubric) {
    copyRubric(context.userId(), sourceRubric.getId().toString(), context.tenant(),
        context.tenantRoot(), collection.getCourseId(), collection.getUnitId(),
        collection.getLessonId(), collection.getId().toString(),
        AJEntityRubric.getTeacherGraderValue());
  }

  public static void copySpecifiedRubricAsStudentRubricForSpecifiedOA(ProcessorContext context,
      AJEntityAssessment collection, AJEntityRubric sourceRubric) {
    copyRubric(context.userId(), sourceRubric.getId().toString(), context.tenant(),
        context.tenantRoot(), collection.getCourseId(), collection.getUnitId(),
        collection.getLessonId(), collection.getId().toString(),
        AJEntityRubric.getStudentGraderValue());
  }

  private static UUID copyRubric(String user, String sourceRubricId, String userTenant,
      String userTenantRoot, String courseId, String unitId, String lessonId, String collectionId,
      String grader) {
    UUID copiedRubricId = UUID.randomUUID();
    Base.exec(RUBRIC_COPY, copiedRubricId.toString(), user, user, sourceRubricId, sourceRubricId,
        userTenant, userTenantRoot,
        courseId, unitId, lessonId, collectionId, grader, sourceRubricId);
    return copiedRubricId;
  }

  private static void saveRubric(AJEntityRubric rubric) {
    if (!rubric.save()) {
      LOGGER.warn("error while deleting rubric:{}", rubric.getId());
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(
              ModelErrorFormatter.formattedError(rubric)));
    }
  }

  private static void deleteRubric(AJEntityRubric rubric, String userId) {
    rubric.setModifierId(userId);
    rubric.markDeleted();

    saveRubric(rubric);
  }

}
