package org.gooru.nucleus.handlers.assessment.processors;

import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponse;

public interface Processor {
  public MessageResponse process();
}
