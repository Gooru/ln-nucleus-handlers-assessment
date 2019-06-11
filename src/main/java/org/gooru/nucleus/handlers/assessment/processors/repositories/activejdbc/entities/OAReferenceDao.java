package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefcreate.OARefCreateCommand;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.formatter.ModelErrorFormatter;
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
  private static final String FETCH_BY_OAID_FILTER = "oa_id = ?::uuid";
  private static final String DELETE_REF = "delete from oa_references where id = ?";
  private static final List<String> FETCH_LIST = Arrays
      .asList("id", "oa_id", "oa_reference_type", "oa_reference_subtype", "location", "created_at",
          "updated_at");
  public static final String OA_REFERENCES = "oa_references";


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

  public static List<AJEntityOAReference> fetchRefsForActivity(String oaId) {
    return AJEntityOAReference.where(FETCH_BY_OAID_FILTER, oaId);
  }

  public static JsonArray fetchRefsForActivityAsJson(String oaId) {
    List<AJEntityOAReference> references = fetchRefsForActivity(oaId);
    if (references != null && !references.isEmpty()) {
      return new JsonArray(JsonFormatterBuilder
          .buildSimpleJsonFormatter(false, FETCH_LIST).toJsonFromList(references));
    }
    return new JsonArray();
  }

  public static void deleteRefById(Long oaRefId) {
    if (oaRefId != null) {
      Base.exec(DELETE_REF, oaRefId);
    }
  }

  public static Long createReference(OARefCreateCommand command) {
    AJEntityOAReference ref = new AJEntityOAReference();
    ref.setOaId(command.getOaId());
    ref.setOaReferenceType(command.getOaRefType());
    ref.setOaReferenceSubtype(command.getOaRefSubtype());
    ref.setLocation(command.getLocation());

    boolean result = ref.save();
    if (!result) {
      JsonObject errors = ModelErrorFormatter.formattedError(ref);
      throw new MessageResponseWrapperException(
          MessageResponseFactory.createValidationErrorResponse(errors));
    }
    return (Long) ref.getId();
  }
}
