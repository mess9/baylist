package org.baylist.telegram.hanlder.config;

import org.baylist.dto.telegram.State;
import org.baylist.telegram.hanlder.ClearHandler;
import org.baylist.telegram.hanlder.DefaultHandler;
import org.baylist.telegram.hanlder.ErrorHandler;
import org.baylist.telegram.hanlder.FriendsHandler;
import org.baylist.telegram.hanlder.MainMenuHandler;
import org.baylist.telegram.hanlder.StartHandler;
import org.baylist.telegram.hanlder.ViewHandler;
import org.baylist.telegram.hanlder.dictionary.DictAddCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictAddTasksToCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictRemoveCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictRemoveVariantHandler;
import org.baylist.telegram.hanlder.dictionary.DictRenameCategoryHandler;
import org.baylist.telegram.hanlder.dictionary.DictMenuHandler;
import org.baylist.telegram.hanlder.dictionary.DictViewHandler;
import org.baylist.telegram.hanlder.feedback.FeedbackAnswerHandler;
import org.baylist.telegram.hanlder.feedback.FeedbackRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
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
			DictMenuHandler dictMenuHandler,
			DictAddCategoryHandler dictAddCategoryHandler,
			DictAddTasksToCategoryHandler dictAddTasksToCategoryHandler,
			DictViewHandler dictViewHandler,
			DictRemoveCategoryHandler dictRemoveCategoryHandler,
			DictRenameCategoryHandler dictRenameCategoryHandler,
			DictRemoveVariantHandler dictRemoveVariantHandler,
			MainMenuHandler mainMenuHandler,
			FriendsHandler friendsHandler
	) {
		Map<State, DialogHandler> stateHandlers = new HashMap<>();
		stateHandlers.put(State.START, startHandler);
		stateHandlers.put(State.ERROR, errorHandler);
		stateHandlers.put(State.DEFAULT, defaultHandler);
		stateHandlers.put(State.CLEAR, clearHandler);
		stateHandlers.put(State.VIEW, viewHandler);
		stateHandlers.put(State.FEEDBACK_REQUEST, feedbackRequestHandler);
		stateHandlers.put(State.FEEDBACK_ANSWER, feedbackAnswerHandler);
		stateHandlers.put(State.DICT_SETTING, dictMenuHandler);
		stateHandlers.put(State.DICT_ADD_CATEGORY, dictAddCategoryHandler);
		stateHandlers.put(State.DICT_ADD_TASK_TO_CATEGORY, dictAddTasksToCategoryHandler);
		stateHandlers.put(State.DICT_VIEW, dictViewHandler);
		stateHandlers.put(State.DICT_REMOVE_CATEGORY, dictRemoveCategoryHandler);
		stateHandlers.put(State.DICT_RENAME_CATEGORY, dictRenameCategoryHandler);
		stateHandlers.put(State.DICT_REMOVE_VARIANT, dictRemoveVariantHandler);
		stateHandlers.put(State.MENU, mainMenuHandler);
		stateHandlers.put(State.FRIENDS, friendsHandler);

		return stateHandlers;
	}

}
