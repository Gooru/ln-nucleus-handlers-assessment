package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class OfflineActivityDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(OfflineActivityDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private OfflineActivityDao() {
    throw new AssertionError();
  }

  private static final String FETCH_QUERY_FILTER =
      " format = 'offline-activity'::content_container_type and id = ?::uuid and is_deleted = false";


  public static AJEntityAssessment fetchOfflineActivityById(String id) {
    List<AJEntityAssessment> offlineActivities = AJEntityAssessment.where(FETCH_QUERY_FILTER, id);
    if (offlineActivities == null || offlineActivities.isEmpty()) {
      LOGGER.warn("Offline activity id: {} not present in DB", id);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(RESOURCE_BUNDLE.getString("not.found") + " : " + id));
    }
    return offlineActivities.get(0);

  }
}
