package org.gooru.nucleus.handlers.assessment.processors.repositories.activejdbc.entities;

import java.util.UUID;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
@Table("diagnostic_assessment")
public class AJEntityDiagnosticAssessment extends Model {

  private static final String GRADE_ID = "grade_id";
  private static final String GUT_SUBJECT = "gut_subject";
  private static final String FW_CODE = "fw_code";
  private static final String ASSESSMENT_ID = "assessment_id";
  private static final String LANGUAGE_ID = "language_id";
  private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityDiagnosticAssessment.class);

  public Long fetchGradeId() {
    return this.getLong(GRADE_ID);
  }

  public String fetchGutSubject() {
    return this.getString(GUT_SUBJECT);
  }

  public String fetchFwCode() {
    return this.getString(FW_CODE);
  }

  public UUID fetchAssessmentId() {
    return UUID.fromString(this.getString(ASSESSMENT_ID));
  }

  public Long fetchLanguageId() {
    return this.getLong(LANGUAGE_ID);
  }

  public static AJEntityDiagnosticAssessment fetchDiagAsmtByGradeAndFw(Long gradeId,
      String fwCode) {
    LazyList<AJEntityDiagnosticAssessment> results = null;

    if (fwCode != null) {
      LOGGER
          .debug("Diagnostic assessment query based on grade: '{}' and fw: '{}'", gradeId, fwCode);
      results = AJEntityDiagnosticAssessment
          .find("grade_id = ?::bigint and fw_code = ?", gradeId, fwCode);
    }
    if (results == null || results.isEmpty()) {
      LOGGER
          .debug("Diagnostic assessment query based on only grade: '{}' ", gradeId);
      results = AJEntityDiagnosticAssessment.find("grade_id = ?::bigint", gradeId);
    }
    return (results != null && !results.isEmpty()) ? results.get(0) : null;
  }


}
