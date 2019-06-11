package org.gooru.nucleus.handlers.assessment.processors.utils;

import java.util.UUID;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;

/**
 * @author ashish on 29/12/16.
 */
public final class ValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    private ValidationUtils() {
        throw new AssertionError();
    }

    public static boolean validateUser(String userId) {
        return !(userId == null || userId.isEmpty())
            && (userId.equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS) || validateUuid(userId));
    }

    public static boolean validateId(String id) {
        return !(id == null || id.isEmpty()) && validateUuid(id);
    }

    private static boolean validateUuid(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateContext(ProcessorContext context) {
        return validateContext(context, false);
    }

    public static boolean validateContext(ProcessorContext context, boolean shouldHaveQuestion) {
        if (!validateId(context.assessmentId())) {
            LOGGER.error("Invalid request, assessment id not available/incorrect format. Aborting");
            return false;
        }
        if (shouldHaveQuestion) {
            if (!validateId(context.questionId())) {
                LOGGER.error("Invalid request, question id not available/incorrect format. Aborting");
                return false;
            }
        }
        return true;
    }

    public static boolean validateAssessmentMasteryAccrualContext(ProcessorContext context) {
        JsonArray assessmentIds = context.request().getJsonArray(MessageConstants.ASSESSMENT_IDS);
        if (assessmentIds != null && !assessmentIds.isEmpty()) {
            for (Object assessmentId : assessmentIds) {
                if (!validateId((String) assessmentId)) {
                    LOGGER.error("Invalid request, assessment ids incorrect format. Aborting");
                    return false;
                }
            }
        }

        return true;
    }

}
