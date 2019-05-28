package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbutils;

import java.util.ArrayList;
import java.util.List;
import org.gooru.nucleus.handlers.assessment.app.components.DataSourceRegistry;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

public final class OASubformatValidationUtil {

  private OASubformatValidationUtil() {
    throw new AssertionError();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(OASubformatValidationUtil.class);
  private static final String QUERY = "select name from collection_subformat_type";
  private static final List<String> VALID_VALUES = new ArrayList<>();

  public static boolean validateOASubformat(Object value) {
    if (value != null) {
      return VALID_VALUES.contains(value);
    }
    return false;
  }

  public static void initialize() {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      List result = Base.firstColumn(QUERY);
      if (result == null) {
        throw new AssertionError("Subformat values not found");
      }
      for (Object subformat : result) {
        VALID_VALUES.add((String) subformat);
      }
    } catch (Throwable e) {
      LOGGER.error("Caught exception while fetching default license value", e);
      throw new IllegalStateException(e);
    } finally {
      Base.close();
    }
  }

}
