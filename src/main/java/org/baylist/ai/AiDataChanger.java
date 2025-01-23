package org.baylist.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.ai.record.Dictionary;
import org.baylist.ai.record.UserWithDictRequest;
import org.baylist.exception.AiException;
import org.baylist.service.DictionaryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiDataChanger {

	DictionaryService dictionaryService;


	public Dictionary changeDict(UserWithDictRequest userRequest) throws AiException {
		return new Dictionary(dictionaryService.changeDict(userRequest.user().getUserId(), userRequest.dict()));
	}


}
