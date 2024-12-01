package org.baylist.util.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@UtilityClass
@Slf4j
public class ToJson {
    public static String toJson(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(NON_NULL);
            mapper.setSerializationInclusion(NON_DEFAULT);
            mapper.setSerializationInclusion(NON_EMPTY);

            mapper.registerModule(new JavaTimeModule());

            SimpleModule localDateTimeDeserializer = new SimpleModule();
            SimpleModule localDateTimeSerializer = new SimpleModule();

            localDateTimeDeserializer.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            localDateTimeSerializer.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

            mapper.registerModule(localDateTimeDeserializer);
            mapper.registerModule(localDateTimeSerializer);

            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert to JSON\n";
            log.info(failMessage, e);
        }

        return json;
    }
}
