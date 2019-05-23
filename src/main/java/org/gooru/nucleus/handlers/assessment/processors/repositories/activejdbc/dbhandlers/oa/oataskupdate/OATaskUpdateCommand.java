package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhandlers.oa.oataskupdate;

import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.OAProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.exceptions.MessageResponseWrapperException;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;

/**
 * @author ashish.
 */

public class OATaskUpdateCommand {

  private final String oaId;
  private final Long oaTaskId;
  private final String title;
  private final String description;
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  OATaskUpdateCommand(String oaId, Long oaTaskId, String title, String description) {
    this.oaId = oaId;
    this.oaTaskId = oaTaskId;
    this.title = title;
    this.description = description;
  }

  static OATaskUpdateCommand build(OAProcessorContext context) {
    String oaId = context.oaId();
    Long oaTaskId = Long.parseLong(context.oaTaskId());
    String title = context.request().getString("title");
    String description = context.request().getString("description");
    OATaskUpdateCommand command = new OATaskUpdateCommand(oaId, oaTaskId, title, description);
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

  public Long getOaTaskId() {
    return oaTaskId;
  }
}
