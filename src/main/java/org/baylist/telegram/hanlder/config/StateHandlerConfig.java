package org.baylist.telegram.hanlder.config;

import org.baylist.dto.telegram.State;
import org.baylist.telegram.hanlder.ClearHandler;
import org.baylist.telegram.hanlder.DefaultHandler;
import org.baylist.telegram.hanlder.ErrorHandler;
import org.baylist.telegram.hanlder.StartHandler;
import org.baylist.telegram.hanlder.ViewHandler;
import org.baylist.telegram.hanlder.dictionary.DictAddCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictAddTasksToCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictSettingHandler;
import org.baylist.telegram.hanlder.feedback.FeedbackAnswerHandler;
import org.baylist.telegram.hanlder.feedback.FeedbackRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateHandlerConfig {

	@Bean
	public Map<State, DialogHandler> stateHandlers(
			StartHandler startHandler,
			ErrorHandler errorHandler,
			DefaultHandler defaultHandler,
			ClearHandler clearHandler,
			ViewHandler viewHandler,
			FeedbackRequestHandler feedbackRequestHandler,
			FeedbackAnswerHandler feedbackAnswerHandler,
			DictSettingHandler dictSettingHandler,
			DictAddCategoryHandler dictAddCategoryHandler,
			DictAddTasksToCategoryHandler dictAddTasksToCategoryHandler
	) {
		return Map.of(
				State.START, startHandler,
				State.ERROR, errorHandler,
				State.DEFAULT, defaultHandler,
				State.CLEAR, clearHandler,
				State.VIEW, viewHandler,
				State.FEEDBACK_REQUEST, feedbackRequestHandler,
				State.FEEDBACK_ANSWER, feedbackAnswerHandler,
				State.DICT_SETTING, dictSettingHandler,
				State.DICT_ADD_CATEGORY, dictAddCategoryHandler,
				State.DICT_ADD_TASK_TO_CATEGORY, dictAddTasksToCategoryHandler
		);
	}

}
