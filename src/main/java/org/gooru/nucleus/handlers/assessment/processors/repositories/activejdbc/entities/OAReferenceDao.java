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

public final class OAReferenceDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAReferenceDao.class);
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");
  private static final String FETCH_QUERY_FILTER = "oa_id = ?::uuid and id = ?";
  private static final String DELETE_REF = "delete from oa_references where id = ?";

  private OAReferenceDao() {
    throw new AssertionError();
  }


  public static AJEntityOAReference fetchOARefByOAIdAndRefId(String oaId, Long oaRefId) {
    List<AJEntityOAReference> references = AJEntityOAReference
        .where(FETCH_QUERY_FILTER, oaId, oaRefId);

    if (references == null || references.isEmpty()) {
      LOGGER.warn("Offline activity id: '{}' ref id '{}' not present in DB", oaId, oaRefId);
      throw new MessageResponseWrapperException(MessageResponseFactory
          .createNotFoundResponse(
              RESOURCE_BUNDLE.getString("not.found") + " : " + oaId + ":" + oaRefId));
    }
    return references.get(0);
  }

  public static void deleteRefById(Long oaRefId) {
    if (oaRefId != null) {
      Base.exec(DELETE_REF, oaRefId);
    }
  }
}
