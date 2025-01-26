package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nenno.basicaiclient.v1.utils.MapperWithCaseStrategy;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class ResponseConverterTest {

    @Test
    public void convertOllamaChat_OK() {

        String responseStr = "{\n" +
                "  \"model\": \"llama3.1\",\n" +
                "  \"created_at\": \"2024-12-22T16:59:39.8933455Z\",\n" +
                "  \"message\": {\n" +
                "    \"role\": \"assistant\",\n" +
                "    \"content\": \"The oceans are salty because of the process of evaporation and ...\"\n" +
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
        MapperWithCaseStrategy.setSnakeCase(objectMapper);

        // Deserialize JSON string to Person object
        AiResponse aiResponse = null;
        try {
            // 1) deserialize Ollama response to specific object
            AiResponseOllama respObjOllama = objectMapper.readValue(responseStr, AiResponseOllama.class);
            System.out.println("Ollama resp obj: "+ respObjOllama);

            // 2) normalize to common response object
            aiResponse = ResponseConverter.normalizeOllama(respObjOllama);
            System.out.println("Common resp obj: "+ aiResponse);

        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        assertNotNull(aiResponse);
        assertEquals("llama3.1", aiResponse.getModel());
        AiResponse.Choice choice = aiResponse.getChoices().get(0);
        AiResponse.Message message = choice.getMessage();
        assertEquals("stop", choice.getFinishReason());
        assertNotNull(message);
        assertEquals("assistant", message.getRole());

    }

    @Test
    public void convertOpenaiChat_OK() {
        String responseStr = "{\n" +
                "  \"id\": \"chatcmpl-AhZhoqF351EsMBiBQcbyGnvnoCSUY\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1734948720,\n" +
                "  \"model\": \"gpt-4o-mini-2024-07-18\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"The sky appears blue primarily due to a phenomenon called Rayleigh scattering.\",\n" +
                "        \"refusal\": null\n" +
                "      },\n" +
                "      \"logprobs\": null,\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 23,\n" +
                "    \"completion_tokens\": 276,\n" +
                "    \"total_tokens\": 299,\n" +
                "    \"prompt_tokens_details\": {\n" +
                "      \"cached_tokens\": 0,\n" +
                "      \"audio_tokens\": 0\n" +
                "    },\n" +
                "    \"completion_tokens_details\": {\n" +
                "      \"reasoning_tokens\": 0,\n" +
                "      \"audio_tokens\": 0,\n" +
                "      \"accepted_prediction_tokens\": 0,\n" +
                "      \"rejected_prediction_tokens\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"system_fingerprint\": \"fp_0aa8d3e20b\"\n" +
                "}";

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        MapperWithCaseStrategy.setSnakeCase(objectMapper);

        // Deserialize JSON string to Person object
        AiResponse aiResponse = null;
        try {
            // 1) deserialize Ollama response to specific object
            AiResponseOpenai respObjOpenai = objectMapper.readValue(responseStr, AiResponseOpenai.class);
            System.out.println("OpenAI resp obj: "+ respObjOpenai);

            // 2) normalize to common response object
            aiResponse = ResponseConverter.normalizeOpenai(respObjOpenai);
            System.out.println("Common resp obj: "+ aiResponse);

        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        assertNotNull(aiResponse);
        assertEquals("chatcmpl-AhZhoqF351EsMBiBQcbyGnvnoCSUY", aiResponse.getId());
        assertEquals("gpt-4o-mini-2024-07-18", aiResponse.getModel());
        AiResponse.Choice choice = aiResponse.getChoices().get(0);
        AiResponse.Message message = choice.getMessage();
        assertEquals("stop", choice.getFinishReason());
        assertNotNull(message);
        assertEquals("assistant", message.getRole());

    }
    @Test
    public void convertOpenaiTool_OK() {
        // arguments should be like: "              \"arguments\": {\"location\":\"city\", \"unit\": \"C\"}\n"
        String responseStr = "{\n" +
                "  \"id\": \"chatcmpl-AiObeuNhiDxMOCnlBkFGQ23y4NyWv\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1735144382,\n" +
                "  \"model\": \"gpt-4o-mini-2024-07-18\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": null,\n" +
                "        \"tool_calls\": [\n" +
                "          {\n" +
                "            \"id\": \"call_5uyhpQ27PlJ5QywUjXVHCIP7\",\n" +
                "            \"type\": \"function\",\n" +
                "            \"function\": {\n" +
                "              \"name\": \"getWeather\",\n" +
                "              \"arguments\": \"{\\\"location\\\":\\\"New York\\\",\\\"unit\\\":\\\"C\\\"}\"\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"refusal\": null\n" +
                "      },\n" +
                "      \"logprobs\": null,\n" +
                "      \"finish_reason\": \"tool_calls\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 92,\n" +
                "    \"completion_tokens\": 20,\n" +
                "    \"total_tokens\": 112,\n" +
                "    \"prompt_tokens_details\": {\n" +
                "      \"cached_tokens\": 0,\n" +
                "      \"audio_tokens\": 0\n" +
                "    },\n" +
                "    \"completion_tokens_details\": {\n" +
                "      \"reasoning_tokens\": 0,\n" +
                "      \"audio_tokens\": 0,\n" +
                "      \"accepted_prediction_tokens\": 0,\n" +
                "      \"rejected_prediction_tokens\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"system_fingerprint\": \"fp_0aa8d3e20b\"\n" +
                "}";
        System.out.println("responseStr = " + responseStr);


        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        MapperWithCaseStrategy.setSnakeCase(objectMapper);

        // Deserialize JSON string to Person object
        AiResponse aiResponse = null;
        try {
            // 1) deserialize response to specific object
            AiResponseOpenai respObjOpenai = objectMapper.readValue(responseStr, AiResponseOpenai.class);
            System.out.println("OpenAI resp obj: "+ respObjOpenai);

            // ------
//            // deserialize arguments
//            String argumentsStr = respObjOpenai.getChoices().get(0).getMessage().getToolCalls().get(0).getFunction().getArguments();
//            System.out.println("argumentsStr: " + argumentsStr);
//
//            LinkedHashMap<String, Object> argumentMap = objectMapper.readValue(argumentsStr, LinkedHashMap.class);
//            System.out.println("argumentMap: " + argumentMap);

            // ------
            // 2) normalize to common response object
            aiResponse = ResponseConverter.normalizeOpenai(respObjOpenai);
            //aiResponse = ResponseConverter.normalizeOpenai(respObjOpenai, argumentMap);
            System.out.println("Common resp obj: "+ aiResponse);

        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        /*
        assertNotNull(aiResponse);
        assertEquals("chatcmpl-AhZhoqF351EsMBiBQcbyGnvnoCSUY", aiResponse.getId());
        assertEquals("gpt-4o-mini-2024-07-18", aiResponse.getModel());
        AiResponse.Choice choice = aiResponse.getChoices().get(0);
        AiResponse.Message message = choice.getMessage();
        assertEquals("stop", choice.getFinishReason());
        assertNotNull(message);
        assertEquals("assistant", message.getRole());
*/

    }
}