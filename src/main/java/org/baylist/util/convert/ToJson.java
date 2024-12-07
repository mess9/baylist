package org.baylist.util.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@UtilityClass
@Slf4j
@SuppressWarnings("unused")
public class ToJson {

    public static String toJson(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapperSetting(mapper);

            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.info("Failed to convert to JSON\n", e);
        }

        return json;
    }

    public static <T> T fromJson(String string, Class<T> valueType) {
        T t = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapperSetting(mapper);

            t = mapper.readValue(string, valueType);
        } catch (JsonProcessingException e) {
            log.info("Failed to convert from JSON\n{}", string, e);
        }
        return t;
    }

    public static <T> T fromYaml(String string, Class<T> valueType) {
        T t = null;
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapperSetting(mapper);

            t = mapper.readValue(string, valueType);
        } catch (JsonProcessingException e) {
            log.info("Failed to convert from YAML\n{}", string, e);
        }
        return t;
    }


    private static void mapperSetting(ObjectMapper mapper) {
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
    }
}
