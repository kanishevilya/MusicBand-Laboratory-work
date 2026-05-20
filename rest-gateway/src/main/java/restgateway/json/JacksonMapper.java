package restgateway.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.exception.DeserializationException;

import java.io.IOException;

public class JacksonMapper implements JsonMapper {
    private final ObjectMapper mapper;

    public JacksonMapper() {
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации в JSON", e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new DeserializationException("Ошибка десериализации из JSON строки: " + e.getMessage());
        }
    }

    @Override
    public <T> T fromJson(byte[] jsonBytes, Class<T> clazz) {
        try {
            return mapper.readValue(jsonBytes, clazz);
        } catch (IOException e) {
            throw new DeserializationException("Ошибка десериализации из JSON байт: " + e.getMessage());
        }
    }
}