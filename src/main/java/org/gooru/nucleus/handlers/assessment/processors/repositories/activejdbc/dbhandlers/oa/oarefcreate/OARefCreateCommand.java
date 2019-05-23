package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oarefcreate;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.OARefTypeSubTypeValidator;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

public class OARefCreateCommand {

  private static final String OA_REFERENCE_TYPE = "oa_reference_type";
  private static final String OA_REFERENCE_SUBTYPE = "oa_reference_subtype";
  private static final String LOCATION = "location";
  private final String oaId;
  private final String oaRefType;
  private final String oaRefSubtype;
  private final String location;
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  private OARefCreateCommand(String oaId, String oaRefType, String oaRefSubtype, String location) {
    this.oaId = oaId;
    this.oaRefType = oaRefType;
    this.oaRefSubtype = oaRefSubtype;
    this.location = location;
  }

  static OARefCreateCommand build(OAProcessorContext context) {
    String oaId = context.oaId();
    String oaRefType = context.request().getString(OA_REFERENCE_TYPE);
    String oaRefSubtype = context.request().getString(OA_REFERENCE_SUBTYPE);
    String location = context.request().getString(LOCATION);
    OARefCreateCommand command = new OARefCreateCommand(oaId, oaRefType, oaRefSubtype, location);
    command.validate();
    return command;
  }

  private void validate() {
    if (oaRefType == null || oaRefType.isEmpty() || oaRefSubtype == null || oaRefSubtype
        .isEmpty() || location == null || location.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.value")));
    }
    OARefTypeSubTypeValidator.validateTypeSubtypeValues(oaRefType, oaRefSubtype);
  }

  public String getOaId() {
    return oaId;
  }

  public String getOaRefType() {
    return oaRefType;
  }

  public String getOaRefSubtype() {
    return oaRefSubtype;
  }

  public String getLocation() {
    return location;
  }
}
