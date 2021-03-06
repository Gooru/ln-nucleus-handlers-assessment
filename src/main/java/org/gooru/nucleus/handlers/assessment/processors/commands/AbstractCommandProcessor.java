package org.gooru.nucleus.handlers.assessment.processors.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.processors.Processor;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.assessment.processors.utils.VersionValidationUtils;

/**
 * @author ashish on 29/12/16.
 */
abstract class AbstractCommandProcessor implements Processor {

  protected final List<String> deprecatedVersions = new ArrayList<>();
  protected final ProcessorContext context;
  protected String version;

  protected static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

  protected AbstractCommandProcessor(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse process() {
    setDeprecatedVersions();
    version = VersionValidationUtils.validateVersion(deprecatedVersions, context.requestHeaders());
    return processCommand();
  }

  protected abstract void setDeprecatedVersions();

  protected abstract MessageResponse processCommand();
}
