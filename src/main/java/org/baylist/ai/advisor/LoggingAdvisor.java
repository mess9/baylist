package org.baylist.ai.advisor;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

public class LoggingAdvisor implements CallAroundAdvisor {

	private final static Logger logger = LoggerFactory.getLogger(LoggingAdvisor.class);

	@NotNull
	@Override
	public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
		before(advisedRequest);
		AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
		after(advisedResponse);
		return advisedResponse;
	}

	private void before(AdvisedRequest advisedRequest) {
		logger.info(advisedRequest.userText());
	}

	private void after(AdvisedResponse advisedResponse) {
		assert advisedResponse.response() != null;
		logger.info(advisedResponse.response()
				.getResult()
				.getOutput()
				.getContent());

	}

	@NotNull
	@Override
	public String getName() {
		return "LoggingAdvisor";
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}
}
