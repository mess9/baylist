//package org.baylist.telegram;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.baylist.dto.telegram.ChatValue;
//import org.baylist.service.DictionaryService;
//import org.baylist.service.FeedbackService;
//import org.baylist.service.TodoistService;
//import org.springframework.stereotype.Component;
//
//import static org.baylist.util.log.TgLog.inputLogMessage;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class TelegramChat {
//
//    private TodoistService todoist;
//	private FeedbackService feedbackService;
//    private Command command;
//	private DictionaryService dictionaryService;
//
//    public void chat(ChatValue chatValue) {
//        inputLogMessage(chatValue.getUpdate());
//
//        if (todoist.storageIsEmpty()) {
//            todoist.syncBuyListData();
//        }
//
//        //todo для обработки чата применить https://refactoring.guru/ru/design-patterns/chain-of-responsibility/java/example
//        command.commandHandler(chatValue);
//
//	    if (!chatValue.isCommandProcess()) {
//		    if (chatValue.getUser().getDialog().isReport()) {
//			    feedbackService.acceptFeedback(chatValue);
//		    } else if (chatValue.getUser().getDialog().isAddCategory()) {
//			    dictionaryService.addDictCategory(chatValue);
//		    }
////			else if (chatState.getUser().getDialog().isAddTaskToCategory()) {
////		    }
//	    } else {
//			    todoist.sendTasksToTodoist(chatValue);
//		    }
//        }
//    }
//
//
//
