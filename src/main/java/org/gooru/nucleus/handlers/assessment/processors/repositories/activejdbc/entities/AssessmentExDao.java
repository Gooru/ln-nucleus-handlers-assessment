package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.LOGIN_REQUIRED;
import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.TITLE;
import static org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities.AJEntityAssessment.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.validators.FieldSelector;

/**
 * @author ashish.
 */

public final class AssessmentExDao {

  private static final Set<String> CREATABLE_EX_FIELDS = AssessmentDao.EDITABLE_FIELDS;
  private static final Set<String> MANDATORY_EX_FIELDS = new HashSet<>(
      Arrays.asList(TITLE, URL, LOGIN_REQUIRED));


  public static FieldSelector editExFieldSelector() {
    return () -> Collections.unmodifiableSet(AssessmentDao.EDITABLE_FIELDS);
  }

  public static FieldSelector createExFieldSelector() {
    return new FieldSelector() {
      @Override
      public Set<String> allowedFields() {
        return Collections.unmodifiableSet(CREATABLE_EX_FIELDS);
      }

      @Override
      public Set<String> mandatoryFields() {
        return Collections.unmodifiableSet(MANDATORY_EX_FIELDS);
      }
    };
  }


  private AssessmentExDao() {
    throw new AssertionError();
  }


}
