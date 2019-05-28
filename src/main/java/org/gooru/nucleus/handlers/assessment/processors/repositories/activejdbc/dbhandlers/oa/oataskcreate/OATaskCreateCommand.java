package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskcreate;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

public class OATaskCreateCommand {

  private final String oaId;
  private final String title;
  private final String description;
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  OATaskCreateCommand(String oaId, String title, String description) {
    this.oaId = oaId;
    this.title = title;
    this.description = description;
  }

  static OATaskCreateCommand build(OAProcessorContext context) {
    String oaId = context.oaId();
    String title = context.request().getString("title");
    String description = context.request().getString("description");
    OATaskCreateCommand command = new OATaskCreateCommand(oaId, title, description);
    command.validate();
    return command;
  }

  private void validate() {
    if (title == null || title.isEmpty()) {
      throw new MessageResponseWrapperException(
          MessageResponseFactory
              .createInvalidRequestResponse("title:" + RESOURCE_BUNDLE.getString("invalid.value")));
    }
  }

  public String getOaId() {
    return oaId;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }
}
