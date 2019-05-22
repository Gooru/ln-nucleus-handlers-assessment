package org.gooru.nucleus.handlers.assessment.processors;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 7/1/16.
 */
public class OAProcessorContext {

  private static final String OA_ID = "oaId";
  private static final String OA_TASK_ID = "oaTaskId";
  private static final String OA_TASK_SUBMISSION_ID = "oaTaskSubmissionId";
  private static final String OA_REF_ID = "oaRefId";
  private static final String OA_RUBRIC_ID = "oaRubricId";
  private final ProcessorContext context;

  public OAProcessorContext(ProcessorContext context) {
    this.context = context;
    if (context == null) {
      throw new IllegalStateException(
          "OA Processor Context creation failed because of invalid values");
    }
  }

  public String userId() {
    return context.userId();
  }

  public JsonObject session() {
    return context.session();
  }

  public JsonObject request() {
    return context.request();
  }

  public String assessmentId() {
    return context.assessmentId();
  }

  public String questionId() {
    return context.questionId();
  }

  public MultiMap requestHeaders() {
    return context.requestHeaders();
  }

  public String tenant() {
    return context.tenant();
  }

  public String tenantRoot() {
    return context.tenantRoot();
  }

  public String oaId() {
    return context.requestHeaders().get(OA_ID);
  }

  public String oaTaskId() {
    return context.requestHeaders().get(OA_TASK_ID);
  }

  public String oaTaskSubmissionId() {
    return context.requestHeaders().get(OA_TASK_SUBMISSION_ID);
  }

  public String oaRefId() {
    return context.requestHeaders().get(OA_REF_ID);
  }

  public String oaRubricId() {
    return context.requestHeaders().get(OA_RUBRIC_ID);
  }

  public ProcessorContext processorContext() {
    return this.context;
  }
}
