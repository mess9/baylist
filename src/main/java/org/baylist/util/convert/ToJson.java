package org.baylist.util.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

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
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("JSON cannot be null or empty");
        }
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
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("YAML cannot be null or empty");
        }
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapperSetting(mapper);

            t = mapper.readValue(string, valueType);
        } catch (JsonProcessingException e) {
            log.info("Failed to convert from YAML\n{}", string, e);
        }
        return t;
    }

    public static <T> List<T> fromJsonList(String string) {
        List<T> list = null;
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("JSON array cannot be null or empty");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapperSetting(mapper);

            list = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert from JSON\n" + string;
            log.info(failMessage, e);
        }
        return list;
    }

    public static <T> List<T> fromJsonList(String string, Class<T> valueType) {
        List<T> list = null;
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("JSON array cannot be null or empty");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
//            mapperSetting(mapper);

            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, valueType);
            list = mapper.readValue(string, listType);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert from JSON\n" + string;
            log.info(failMessage, e);
        }
        return list;
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
