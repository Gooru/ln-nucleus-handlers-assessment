package org.gooru.nucleus.handlers.assessment.processors.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.gooru.nucleus.handlers.assessment.constants.MessageConstants;
import org.gooru.nucleus.handlers.assessment.processors.Processor;
import org.gooru.nucleus.handlers.assessment.processors.ProcessorContext;
import org.gooru.nucleus.handlers.assessment.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish on 29/12/16.
 */
public enum CommandProcessorBuilder {

  DEFAULT("default") {
    private final Logger LOGGER = LoggerFactory.getLogger(CommandProcessorBuilder.class);
    private final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages");

    @Override
    public Processor build(ProcessorContext context) {
      return () -> {
        LOGGER.error("Invalid operation type passed in, not able to handle");
        return MessageResponseFactory
            .createInvalidRequestResponse(RESOURCE_BUNDLE.getString("invalid.operation"));
      };
    }
  },
  EXT_ASSESSMENT_DELETE(MessageConstants.MSG_OP_EXT_ASSESSMENT_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new ExAssessmentDeleteProcessor(context);
    }
  },
  EXT_ASSESSMENT_UPDATE(MessageConstants.MSG_OP_EXT_ASSESSMENT_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new ExAssessmentUpdateProcessor(context);
    }
  },
  EXT_ASSESSMENT_CREATE(MessageConstants.MSG_OP_EXT_ASSESSMENT_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new ExAssessmentCreateProcessor(context);
    }
  },
  EXT_ASSESSMENT_GET(MessageConstants.MSG_OP_EXT_ASSESSMENT_GET) {
    @Override
    public Processor build(ProcessorContext context) {
      return new ExAssessmentGetProcessor(context);
    }
  },
  ASSESSMENT_COLLABORATOR_UPDATE(MessageConstants.MSG_OP_ASSESSMENT_COLLABORATOR_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentCollaboratorUpdateProcessor(context);
    }
  },
  ASSESSMENT_QUESTION_REORDER(MessageConstants.MSG_OP_ASSESSMENT_QUESTION_REORDER) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentQuestionReorderProcessor(context);
    }
  },
  ASSESSMENT_QUESTION_ADD(MessageConstants.MSG_OP_ASSESSMENT_QUESTION_ADD) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentQuestionAddProcessor(context);
    }
  },
  ASSESSMENT_DELETE(MessageConstants.MSG_OP_ASSESSMENT_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentDeleteProcessor(context);
    }
  },
  ASSESSMENT_UPDATE(MessageConstants.MSG_OP_ASSESSMENT_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentUpdateProcessor(context);
    }
  },
  ASSESSMENT_CREATE(MessageConstants.MSG_OP_ASSESSMENT_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentCreateProcessor(context);
    }
  },
  ASSESSMENT_MASTERY_ACCRUAL_GET(MessageConstants.MSG_OP_ASSESSMENT_MASTERY_ACCRUAL_GET) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentMasteryAccrualProcessor(context);
    }
  },
  ASSESSMENT_GET(MessageConstants.MSG_OP_ASSESSMENT_GET) {
    @Override
    public Processor build(ProcessorContext context) {
      return new AssessmentGetProcessor(context);
    }
  },
  MSG_OP_OA_GET_SUMMARY(MessageConstants.MSG_OP_OA_GET_SUMMARY) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OAFetchSummaryProcessor(context);
    }
  },
  MSG_OP_OA_GET_DETAIL(MessageConstants.MSG_OP_OA_GET_DETAIL) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OAFetchDetailProcessor(context);
    }
  },
  MSG_OP_OA_CREATE(MessageConstants.MSG_OP_OA_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OACreateProcessor(context);
    }
  },
  MSG_OP_OA_UPDATE(MessageConstants.MSG_OP_OA_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OAUpdateProcessor(context);
    }
  },
  MSG_OP_OA_DELETE(MessageConstants.MSG_OP_OA_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OADeleteProcessor(context);
    }
  },
  MSG_OP_OA_COLLABORATOR_UPDATE(MessageConstants.MSG_OP_OA_COLLABORATOR_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OAUpdateCollaboratorProcessor(context);
    }
  },
  MSG_OP_OA_TASK_CREATE(MessageConstants.MSG_OP_OA_TASK_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OATaskCreateProcessor(context);
    }
  },
  MSG_OP_OA_TASK_DELETE(MessageConstants.MSG_OP_OA_TASK_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OATaskDeleteProcessor(context);
    }
  },
  MSG_OP_OA_TASK_UPDATE(MessageConstants.MSG_OP_OA_TASK_UPDATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OATaskUpdateProcessor(context);
    }
  },
  MSG_OP_OA_TASK_SUBMISSION_CREATE(MessageConstants.MSG_OP_OA_TASK_SUBMISSION_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OATaskSubmissionCreateProcessor(context);
    }
  },
  MSG_OP_OA_TASK_SUBMISSION_DELETE(MessageConstants.MSG_OP_OA_TASK_SUBMISSION_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OATaskSubmissionDeleteProcessor(context);
    }
  },
  MSG_OP_OA_REF_DELETE(MessageConstants.MSG_OP_OA_REF_DELETE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OARefDeleteProcessor(context);
    }
  },
  MSG_OP_OA_REF_CREATE(MessageConstants.MSG_OP_OA_REF_CREATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OARefCreateProcessor(context);
    }
  },
  MSG_OP_OA_TEACHER_RUBRIC_ASSOCIATE(MessageConstants.MSG_OP_OA_TEACHER_RUBRIC_ASSOCIATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OARubricTeacherAssociateProcessor(context);
    }
  },
  MSG_OP_OA_STUDENT_RUBRIC_ASSOCIATE(MessageConstants.MSG_OP_OA_STUDENT_RUBRIC_ASSOCIATE) {
    @Override
    public Processor build(ProcessorContext context) {
      return new OARubricStudentAssociateProcessor(context);
    }
  };

  private String name;

  CommandProcessorBuilder(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  private static final Map<String, CommandProcessorBuilder> LOOKUP = new HashMap<>();

  static {
    for (CommandProcessorBuilder builder : values()) {
      LOOKUP.put(builder.name, builder);
    }
  }

  public static CommandProcessorBuilder lookupBuilder(String name) {
    CommandProcessorBuilder builder = LOOKUP.get(name);
    if (builder == null) {
      return DEFAULT;
    }
    return builder;
  }

  public abstract Processor build(ProcessorContext context);
}
