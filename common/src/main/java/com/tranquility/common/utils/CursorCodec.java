package com.tranquility.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public <T> String encode(T record) {
        try {
            String json = objectMapper.writeValueAsString(record);
            return Base64.getEncoder()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new CursorCodecException(e);
        }
    }

    public <T> T decode(String encoded, Class<T> type) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encoded);
            String json = new String(decoded, StandardCharsets.UTF_8);

            return objectMapper.readValue(json, type);
        } catch (IOException | IllegalArgumentException e) {
            throw new CursorCodecException(e);
        }
    }
}