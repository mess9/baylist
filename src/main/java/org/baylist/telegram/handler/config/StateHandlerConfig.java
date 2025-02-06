package org.baylist.telegram.handler.config;

import org.baylist.dto.telegram.State;
import org.baylist.telegram.handler.AiHandler;
import org.baylist.telegram.handler.ClearHandler;
import org.baylist.telegram.handler.DefaultHandler;
import org.baylist.telegram.handler.ErrorHandler;
import org.baylist.telegram.handler.FriendsHandler;
import org.baylist.telegram.handler.HelpHandler;
import org.baylist.telegram.handler.MainMenuHandler;
import org.baylist.telegram.handler.StartHandler;
import org.baylist.telegram.handler.ViewHandler;
import org.baylist.telegram.handler.dictionary.DictAddCategoryHandler;
import org.baylist.telegram.handler.dictionary.DictAddVariantToCategoryHandler;
import org.baylist.telegram.handler.dictionary.DictMenuHandler;
import org.baylist.telegram.handler.dictionary.DictRemoveCategoryHandler;
import org.baylist.telegram.handler.dictionary.DictRemoveVariantHandler;
import org.baylist.telegram.handler.dictionary.DictRenameCategoryHandler;
import org.baylist.telegram.handler.dictionary.DictViewHandler;
import org.baylist.telegram.handler.feedback.FeedbackAnswerHandler;
import org.baylist.telegram.handler.feedback.FeedbackRequestHandler;
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
			DictAddVariantToCategoryHandler dictAddVariantToCategoryHandler,
			DictViewHandler dictViewHandler,
			DictRemoveCategoryHandler dictRemoveCategoryHandler,
			DictRenameCategoryHandler dictRenameCategoryHandler,
			DictRemoveVariantHandler dictRemoveVariantHandler,
			MainMenuHandler mainMenuHandler,
			FriendsHandler friendsHandler,
			HelpHandler helpHandler,
			AiHandler aiHandler
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
		stateHandlers.put(State.DICT_ADD_TASK_TO_CATEGORY, dictAddVariantToCategoryHandler);
		stateHandlers.put(State.DICT_VIEW, dictViewHandler);
		stateHandlers.put(State.DICT_REMOVE_CATEGORY, dictRemoveCategoryHandler);
		stateHandlers.put(State.DICT_RENAME_CATEGORY, dictRenameCategoryHandler);
		stateHandlers.put(State.DICT_REMOVE_VARIANT, dictRemoveVariantHandler);
		stateHandlers.put(State.MENU, mainMenuHandler);
		stateHandlers.put(State.FRIENDS, friendsHandler);
		stateHandlers.put(State.HELP, helpHandler);
		stateHandlers.put(State.AI, aiHandler);

		return stateHandlers;
	}

}
