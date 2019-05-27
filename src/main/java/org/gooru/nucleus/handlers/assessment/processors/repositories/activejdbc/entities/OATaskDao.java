package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskcreate.OATaskCreateCommand;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskupdate.OATaskUpdateCommand;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class OATaskDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(OATaskDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private static final String FETCH_QUERY_FILTER = "oa_id = ?::uuid and id = ?";
  private static final String FETCH_BY_ID_FILTER = "oa_id = ?::uuid";
  private static final String DELETE_TASK = "delete from oa_tasks where id = ?";
  private static final List<String> FETCH_LIST = Arrays
      .asList("id", "oa_id", "title", "description", "created_at", "updated_at");
  public static final String OA_TASKS = "oa_tasks";


  private OATaskDao() {
    throw new AssertionError();
  }


  public static AJEntityOATask fetchOATaskByOAIdAndTaskId(String oaId, Long oaTaskId) {
    List<AJEntityOATask> tasks = AJEntityOATask
        .where(FETCH_QUERY_FILTER, oaId, oaTaskId);

    if (tasks == null || tasks.isEmpty()) {
      LOGGER.warn("Offline activity id: '{}' task id '{}' not present in DB", oaId, oaTaskId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(
              RESOURCE_BUNDLE.getString("not.found") + " : " + oaId + ":" + oaTaskId));
    }
    return tasks.get(0);
  }

  public static List<AJEntityOATask> fetchTasksForActivity(String oaId) {
    return AJEntityOATask.where(FETCH_BY_ID_FILTER, oaId);
  }

  public static JsonArray fetchTasksForActivityAsJson(String oaId) {
    List<AJEntityOATask> tasks = fetchTasksForActivity(oaId);
    if (tasks != null && !tasks.isEmpty()) {
      return new JsonArray(JsonFormatterBuilder
          .buildSimpleJsonFormatter(false, FETCH_LIST).toJsonFromList(tasks));
    }
    return new JsonArray();
  }

  public static JsonArray fetchTasksWithDetailsForActivityAsJson(String oaId) {
    List<AJEntityOATask> tasks = fetchTasksForActivity(oaId);
    if (tasks != null && !tasks.isEmpty()) {
      List<AJEntityOATaskSubmission> submissions = OATaskSubmissionDao
          .fetchOATaskSubmissionsForSpecifiedTasks(tasks);

      JsonArray tasksInfo = new JsonArray(JsonFormatterBuilder
          .buildSimpleJsonFormatter(false, FETCH_LIST).toJsonFromList(tasks));
      if (submissions != null && !submissions.isEmpty()) {
        return mergeTaskDetailsInTasksInfo(tasksInfo, submissions);
      } else {
        return tasksInfo;
      }
    } else {
      return new JsonArray();
    }
  }

  private static JsonArray mergeTaskDetailsInTasksInfo(JsonArray tasksInfo,
      List<AJEntityOATaskSubmission> submissions) {
    Map<Long, List<AJEntityOATaskSubmission>> mapTaskIdToSubmission = new HashMap<>();
    for (AJEntityOATaskSubmission submission : submissions) {
      List<AJEntityOATaskSubmission> existingSubmissions = mapTaskIdToSubmission
          .get(submission.getOaTaskId());
      if (existingSubmissions == null) {
        existingSubmissions = new ArrayList<>();
      }
      existingSubmissions.add(submission);
      mapTaskIdToSubmission.put(submission.getOaTaskId(), existingSubmissions);
    }

    for (Object o : tasksInfo) {
      JsonObject task = (JsonObject) o;
      Long id = task.getLong("id");
      List<AJEntityOATaskSubmission> submissionsForTask = mapTaskIdToSubmission.get(id);
      if (submissionsForTask != null && !submissionsForTask.isEmpty()) {
        JsonArray taskSubmissions = new JsonArray(JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, AJEntityOATaskSubmission.FETCH_LIST)
            .toJsonFromList(mapTaskIdToSubmission.get(id)));
        task.put(OATaskSubmissionDao.OA_TASKS_SUBMISSIONS, taskSubmissions);
      } else {
        task.put(OATaskSubmissionDao.OA_TASKS_SUBMISSIONS, new JsonArray());
      }
    }

    return tasksInfo;
  }


  public static void deleteTaskById(Long oaTaskId) {
    if (oaTaskId != null) {
      Base.exec(DELETE_TASK, oaTaskId);
    }
  }

  public static Long createTask(OATaskCreateCommand command) {
    AJEntityOATask task = new AJEntityOATask();
    task.setOaId(command.getOaId());
    task.setTitle(command.getTitle());
    task.setDescription(command.getDescription());

    boolean result = task.save();
    if (!result) {
      JsonObject errors = ModelErrorFormatter.formattedError(task);
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(errors));
    }
    return (Long) task.getId();
  }

  public static void updateTask(AJEntityOATask task, OATaskUpdateCommand command) {
    task.setTitle(command.getTitle());
    task.setDescription(command.getDescription());
    boolean result = task.save();
    if (!result) {
      JsonObject errors = ModelErrorFormatter.formattedError(task);
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(errors));
    }
  }
}
