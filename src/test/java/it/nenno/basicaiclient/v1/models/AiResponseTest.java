package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.Test;

import static org.junit.Assert.*;

public class AiResponseTest {
    @Test
    public void testOllamaChatResponse_OK() {
        String responseStr = "{\n" +
                "  \"model\": \"llama3.1\",\n" +
                "  \"created_at\": \"2024-12-22T16:59:39.8933455Z\",\n" +
                "  \"message\": {\n" +
                "    \"role\": \"assistant\",\n" +
                "    \"content\": \"The oceans are salty because of the process of evaporation\"\n" +
                "  },\n" +
                "  \"done_reason\": \"stop\",\n" +
                "  \"done\": true,\n" +
                "  \"total_duration\": 5488949100,\n" +
                "  \"load_duration\": 23733500,\n" +
                "  \"prompt_eval_count\": 27,\n" +
                "  \"prompt_eval_duration\": 115000000,\n" +
                "  \"eval_count\": 315,\n" +
                "  \"eval_duration\": 5349000000\n" +
                "}";

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);


        // Deserialize JSON string to Person object
        AiResponse aiResponse = null;
        try {
            AiResponseOllama respObjOllama = objectMapper.readValue(responseStr, AiResponseOllama.class);
            aiResponse = ResponseConverter.normalizeOllama(respObjOllama);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        // Print the deserialized object
        System.out.println(aiResponse);

        assertNotNull(aiResponse);
        assertEquals("llama3.1", aiResponse.getModel());
        AiResponse.Choice choice = aiResponse.getChoices().get(0);
        AiResponse.Message message = choice.getMessage();
        assertEquals("stop", choice.getFinishReason());
        assertNotNull(message);
        assertEquals("assistant", message.getRole());
    }

    @Test
    public void testOllamaToolResponse_OK() {
        String responseStr = "{\n" +
                "  \"model\": \"llama3.1\",\n" +
                "  \"created_at\": \"2024-12-22T17:02:07.2197786Z\",\n" +
                "  \"message\": {\n" +
                "    \"role\": \"assistant\",\n" +
                "    \"content\": \"\",\n" +
                "    \"tool_calls\": [\n" +
                "      {\n" +
                "        \"function\": {\n" +
                "          \"name\": \"getWeather\",\n" +
                "          \"arguments\": {\n" +
                "            \"location\": \"New York\",\n" +
                "            \"unit\": \"C\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"done_reason\": \"stop\",\n" +
                "  \"done\": true,\n" +
                "  \"total_duration\": 4082426900,\n" +
                "  \"load_duration\": 3215554200,\n" +
                "  \"prompt_eval_count\": 208,\n" +
                "  \"prompt_eval_duration\": 162000000,\n" +
                "  \"eval_count\": 24,\n" +
                "  \"eval_duration\": 320000000\n" +
                "}";

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        // Deserialize JSON string to Person object
        AiResponse aiResponse = null;
        try {
            AiResponseOllama respObjOllama = objectMapper.readValue(responseStr, AiResponseOllama.class);
            aiResponse = ResponseConverter.normalizeOllama(respObjOllama);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        // Print the deserialized object
        System.out.println(aiResponse);

        assertNotNull(aiResponse);
        assertEquals("llama3.1", aiResponse.getModel());
        AiResponse.Choice choice = aiResponse.getChoices().get(0);
        AiResponse.Message message = choice.getMessage();
        assertEquals("stop", choice.getFinishReason());
        assertNotNull(message);
        assertEquals("assistant", message.getRole());

        //todo tool data validation
/*
        // tool data
        assertNotNull(aiResponse.getMessage().getToolCalls());
        // Function
        assertNotNull(aiResponse.getMessage().getToolCalls().get(0).getFunction());
        assertEquals("getWeather", aiResponse.getMessage().getToolCalls().get(0).getFunction().getName());
        // Arguments
        assertNotNull(aiResponse.getMessage().getToolCalls().get(0).getFunction().getArguments());
        assertEquals("New York", aiResponse.getMessage().getToolCalls().get(0).getFunction().getArguments().get("location"));
*/
    }
}