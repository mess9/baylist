package org.baylist.telegram2.hanlder.config;

import org.baylist.dto.telegram.State;
import org.baylist.telegram2.hanlder.ClearHandler;
import org.baylist.telegram2.hanlder.DefaultHandler;
import org.baylist.telegram2.hanlder.FeedbackHandler;
import org.baylist.telegram2.hanlder.ReportHandler;
import org.baylist.telegram2.hanlder.StartHandler;
import org.baylist.telegram2.hanlder.ViewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateHandlerConfig {

	@Bean
	public Map<State, DialogHandler> stateHandlers(
			StartHandler startHandler,
			DefaultHandler defaultHandler,
			ClearHandler clearHandler,
			ViewHandler viewHandler,
			ReportHandler reportHandler,
			FeedbackHandler feedbackHandler
	) {
		return Map.of(
				State.START, startHandler,
				State.DEFAULT, defaultHandler,
				State.CLEAR, clearHandler,
				State.VIEW, viewHandler,
				State.REPORT, reportHandler,
				State.FEEDBACK, feedbackHandler
		);
	}

}
