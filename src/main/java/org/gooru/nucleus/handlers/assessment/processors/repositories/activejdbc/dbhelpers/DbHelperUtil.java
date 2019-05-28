package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.dbhelpers;

import java.util.Collection;
import java.util.Iterator;

public final class DbHelperUtil {

  private DbHelperUtil() {
    throw new AssertionError();
  }

  public static String toPostgresArrayString(Collection<String> input) {
    int approxSize = ((input.size() + 1) * 36); // Length of UUID is around
    // 36 chars
    Iterator<String> it = input.iterator();
    if (!it.hasNext()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder(approxSize);
    sb.append('{');
    for (; ; ) {
      String s = it.next();
      sb.append('"').append(s).append('"');
      if (!it.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',');
    }
  }

  public static String toPostgresArrayLong(Collection<Long> input) {
    int approxSize = ((input.size() + 1) * 16); // Length of UUID is around
    // 36 chars
    Iterator<Long> it = input.iterator();
    if (!it.hasNext()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder(approxSize);
    sb.append('{');
    for (; ; ) {
      Long s = it.next();
      sb.append(s);
      if (!it.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',');
    }
  }

}
