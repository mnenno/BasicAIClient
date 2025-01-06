package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class ResponseArgumentDeserializer extends JsonDeserializer<Map<String, Object>> {
    @Override
    public Map<String, Object> deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        // Get the string value of the `arguments` field
        String argumentsJson = jsonParser.getText();

        // Parse the JSON string into a Map
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(argumentsJson, Map.class);
    }
}
