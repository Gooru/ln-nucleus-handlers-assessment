package org.gooru.nucleus.handlers.assessment.processors;

import io.vertx.core.json.JsonObject;

public interface Processor {
  public JsonObject process();
}
