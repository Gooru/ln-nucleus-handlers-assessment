package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
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
  private static final String DELETE_TASK = "delete from oa_tasks where id = ?";

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

  public static void deleteTaskById(Long oaTaskId) {
    if (oaTaskId != null) {
      Base.exec(DELETE_TASK, oaTaskId);
    }
  }
}
