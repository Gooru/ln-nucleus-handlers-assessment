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
    ASSESSMENT_GET(MessageConstants.MSG_OP_ASSESSMENT_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new AssessmentGetProcessor(context);
        }
    },
    ASSESSMENT_QUESTION_TAG_AGGREGATE(MessageConstants.MSG_OP_ASSESSMENT_QUESTION_TAG_AGGREGATE) {
        @Override
        public Processor build(ProcessorContext context) {
            return new AssessmentQuestionTagAggregateProcessor(context);
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
