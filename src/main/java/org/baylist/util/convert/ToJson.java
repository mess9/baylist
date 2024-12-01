package org.baylist.util.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ToJson {
    public static String toJson(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert to JSON\n";
            log.info(failMessage, e);
        }

        return json;
    }
}
