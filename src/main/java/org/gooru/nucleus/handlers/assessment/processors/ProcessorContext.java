package org.gooru.nucleus.handlers.assessment.processors;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;

/**
 * Created by ashish on 7/1/16.
 */
public class ProcessorContext {

  private final String userId;
  private final JsonObject session;
  private final JsonObject request;
  private final String assessmentId;
  private final String questionId;
  private final MultiMap requestHeaders;
  private final TenantContext tenantContext;

  public ProcessorContext(String userId, JsonObject session, JsonObject request,
      MultiMap headers) {
    if (session == null || userId == null || session.isEmpty() || headers == null || headers
        .isEmpty()) {
      throw new IllegalStateException(
          "Processor Context creation failed because of invalid values");
    }
    this.userId = userId;
    this.session = session.copy();
    this.request = request != null ? request.copy() : null;
    // Assessment id and question id can be null in case of create and hence can't validate them unless we know
    // the op type also Do not want to build dependency on op for this context to work and hence is open ended.
    // Worst case would be RTE, so beware
    this.assessmentId = headers.get(MessageConstants.ASSESSMENT_ID);
    this.questionId = request != null ? request.getString(MessageConstants.ID) : null;
    this.requestHeaders = headers;
    this.tenantContext = new TenantContext(session);
  }

  public String userId() {
    return this.userId;
  }

  public JsonObject session() {
    return this.session.copy();
  }

  public JsonObject request() {
    return this.request;
  }

  public String assessmentId() {
    return this.assessmentId;
  }

  public String questionId() {
    return this.questionId;
  }

  public MultiMap requestHeaders() {
    return requestHeaders;
  }

  public String tenant() {
    return this.tenantContext.tenant();
  }

  public String tenantRoot() {
    return this.tenantContext.tenantRoot();
  }

  private static class TenantContext {

    private static final String TENANT = "tenant";
    private static final String TENANT_ID = "tenant_id";
    private static final String TENANT_ROOT = "tenant_root";

    private final String tenantId;
    private final String tenantRoot;

    TenantContext(JsonObject session) {
      JsonObject tenantJson = session.getJsonObject(TENANT);
      if (tenantJson == null || tenantJson.isEmpty()) {
        throw new IllegalStateException("Tenant Context invalid");
      }
      this.tenantId = tenantJson.getString(TENANT_ID);
      if (tenantId == null || tenantId.isEmpty()) {
        throw new IllegalStateException("Tenant Context with invalid tenant");
      }
      this.tenantRoot = tenantJson.getString(TENANT_ROOT);
    }

    public String tenant() {
      return this.tenantId;
    }

    public String tenantRoot() {
      return this.tenantRoot;
    }
  }
}
