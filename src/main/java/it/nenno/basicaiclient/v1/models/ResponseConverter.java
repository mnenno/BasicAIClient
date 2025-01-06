/*
 * Copyright 2025 Mario Nenno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static it.nenno.basicaiclient.v1.AiClient.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** standardize a specific response to common response format */
public class ResponseConverter {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResponseConverter.class);

    // **********************************************
    // ---------- Responses of completion ------------
    // **********************************************

    public static AiResponse normalizeOllama(AiResponseOllama respObjOllama) {
        AiResponse aiResponse = new AiResponse();

        if (respObjOllama != null) {
            aiResponse.setId("dummy-id");
            //aiResponse.setObject("dummy-object");
            // created_at is ignored and replaced
            aiResponse.setCreated(System.currentTimeMillis());
            aiResponse.setModel(respObjOllama.getModel());

            // create Message
            AiResponse.Message message = new AiResponse.Message();
            if (respObjOllama.getResponse() == null) {
                // for chat response
                message.setRole(respObjOllama.getMessage().getRole());
                message.setContent(respObjOllama.getMessage().getContent());
            } else {
                // for generate response convert to assistant content
                message.setRole("assistant");
                message.setContent(respObjOllama.getResponse());
            }

            // convert tool response Ollama to OpenAI response format
            // ---------------------- Tool calls (optional) -------------------------
            if (respObjOllama.getMessage() != null && respObjOllama.getMessage().getToolCalls() != null) {
                // create a new list of toolCalls
                List<AiResponse.ToolCall> toolCalls = new ArrayList<>();

                // Loop over ToolCalls from original OpenAI response and copy values
                for (AiResponseOllama.ToolCall toolCallOllama : respObjOllama.getMessage().getToolCalls()) {
                    // --- Create new toolCall
                    AiResponse.ToolCall toolCall = new AiResponse.ToolCall();
                    toolCall.setId("dummy-call-id");
                    toolCall.setType("function");

                    // --- Create new function
                    AiResponse.Function function = new AiResponse.Function();
                    function.setName(toolCallOllama.getFunction().getName());

                    // ---------------------- Function arguments
                    LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();
                    Map<String, Object> argumentsOllama = toolCallOllama.getFunction().getArguments();
                    for (Map.Entry<String, Object> entry : argumentsOllama.entrySet()) {
                        arguments.put(entry.getKey(), entry.getValue());
                    } // for entry
                    function.setArguments(arguments);
                    toolCall.setFunction(function);
                    toolCalls.add(toolCall);
                } // for toolCallOllama
                message.setToolCalls(toolCalls);
            } // if getToolCalls

            // Ollama format:
        /*
        "message": {
            "role": "assistant",
            "content": "",
            "tool_calls": [
              {
                "function": {
                  "name": "getWeather",
                  "arguments": {
                    "location": "New York",
                    "unit": "C"
                  }
                }
              }
            ]
          },
         */
            // OpenAI format:
        /*
        "choices": [
            {
              "index": 0,
              "message": {
                "role": "assistant",
                "content": null,
                "tool_calls": [
                  {
                    "id": "call_xGFGA9oEs5eZnfSNAZCTIHQY",
                    "type": "function",
                    "function": {
                      "name": "getWeather",
                      "arguments": "{\"location\":\"New York\",\"unit\":\"C\"}"
                    }
                  }
                ],
                "refusal": null
              },
              "logprobs": null,
              "finish_reason": "tool_calls"
            }
          ],
         */

            // create Choice
            AiResponse.Choice choice = new AiResponse.Choice();
            choice.setIndex(0);
            choice.setMessage(message);
            choice.setFinishReason(respObjOllama.getDoneReason());
            // add to list of choices
            List<AiResponse.Choice> choices = new ArrayList<>();
            choices.add(choice);
            // add choices to response
            aiResponse.setChoices(choices);


            // Usage
            AiResponse.Usage usage = new AiResponse.Usage();

            int promptTokens = respObjOllama.getPromptEvalCount();
            usage.setPromptTokens(promptTokens);

            int completionTokens = respObjOllama.getEvalCount();
            usage.setCompletionTokens(completionTokens);

            int totalTokens = promptTokens + completionTokens;
            usage.setTotalTokens(totalTokens);
            aiResponse.setUsage(usage);

            // Token detail omitted
        } else LOGGER.warn("respObjOllama is null!");
        return aiResponse;
    }

    public static AiResponse normalizeOpenai(AiResponseOpenai respObjOpenai) {
        AiResponse aiResponse = new AiResponse();
        if (respObjOpenai != null) {
            aiResponse.setId(respObjOpenai.getId());
            aiResponse.setObject(respObjOpenai.getObject());
            aiResponse.setCreated(respObjOpenai.getCreated());
            aiResponse.setModel(respObjOpenai.getModel());

            // ---------------------- Choices with messages -------------------------
            // create new list of choices
            List<AiResponse.Choice> choices = new ArrayList<>();
            // loop over choices in openai response and copy over
            for(AiResponseOpenai.Choice choiceOpenai: respObjOpenai.getChoices()){
                // New choice
                AiResponse.Choice choice = new AiResponse.Choice();
                choice.setIndex(choiceOpenai.getIndex());
                choice.setLogprobs(choiceOpenai.getLogprobs());
                choice.setFinishReason(choiceOpenai.getFinishReason());

                // New message
                AiResponse.Message message = new AiResponse.Message();
                message.setRole(choiceOpenai.getMessage().getRole());
                message.setContent(choiceOpenai.getMessage().getContent());
                message.setRefusal(choiceOpenai.getMessage().getRefusal());

                // ---------------------- Tool calls (optional) -------------------------
                if (choiceOpenai.getMessage().getToolCalls() != null) {
                    // create a new list of toolCalls
                    List<AiResponse.ToolCall> toolCalls = new ArrayList<>();

                    // Loop over ToolCalls from original OpenAI response and copy values
                    for(AiResponseOpenai.ToolCall toolCallOpenai: choiceOpenai.getMessage().getToolCalls()){
                        AiResponse.ToolCall toolCall = new AiResponse.ToolCall();
                        toolCall.setId(toolCallOpenai.getId());
                        toolCall.setType(toolCallOpenai.getType());

                        // ---------------------- Function
                        AiResponse.Function function = new AiResponse.Function();
                        function.setName(toolCallOpenai.getFunction().getName());

                        // ---------------------- Function arguments
                        LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();
                        // loop over arguments form OpenAI toolCall
                        Map<String, Object> argumentsOpenai = toolCallOpenai.getFunction().getArguments();
                        for (Map.Entry<String, Object> entry : argumentsOpenai.entrySet()) {
                            arguments.put(entry.getKey(), entry.getValue());
                        } // for entry

                        function.setArguments(arguments);
                        toolCall.setFunction(function);
                        toolCalls.add(toolCall);
                    } // for toolCallOpenai
                    message.setToolCalls(toolCalls);
                } // if getToolCalls

                choice.setMessage(message);
                choices.add(choice);
            }
            // add choices in new response
            aiResponse.setChoices(choices);

            // ---------------------- usage  -------------------------
            AiResponse.Usage usage = new AiResponse.Usage();
            usage.setPromptTokens(respObjOpenai.getUsage().getPromptTokens());
            usage.setCompletionTokens(respObjOpenai.getUsage().getCompletionTokens());
            usage.setTotalTokens(respObjOpenai.getUsage().getTotalTokens());
            aiResponse.setUsage(usage);
        }
        else LOGGER.warn("respObjOpenai is null!");

        return aiResponse;
    }

    public static AiResponse normalizeAnthropic(AiResponseAnthropic respObjAnthropic) {
        AiResponse aiResponse = new AiResponse();
        if (respObjAnthropic != null) {
            aiResponse.setId(respObjAnthropic.getId());
            aiResponse.setModel(respObjAnthropic.getModel());
            aiResponse.setObject(respObjAnthropic.getType());
            aiResponse.setCreated(System.currentTimeMillis());

            // --------------------- contents/answer ----------------------------------
            // create new list of choices
            List<AiResponse.Choice> choices = new ArrayList<>();

            // extract answer/s by looping over the list of content blocks
            List<AiResponseAnthropic.ContentBlock> content = respObjAnthropic.getContent();

            // declare a list of toolCalls
            List<AiResponse.ToolCall> toolCalls = null;

            for (int i = 0; i < content.size(); i++) {
                AiResponseAnthropic.ContentBlock contentBlock = content.get(i);
                //LOGGER.debug(">>> contentBlock("+i+"): "+ contentBlock);

                // normal text content block
                AiResponse.Message message = new AiResponse.Message();
                if ("text".equals(contentBlock.getType())){
                    message.setRole(respObjAnthropic.getRole());
                    message.setContent(contentBlock.getText());
                }

                // tool use content block
                if ("tool_use".equals(contentBlock.getType())){
                    AiResponse.ToolCall toolCall = new AiResponse.ToolCall();
                    toolCall.setId(contentBlock.getId());
                    toolCall.setType("function");

                    // --- Create new function
                    AiResponse.Function function = new AiResponse.Function();
                    function.setName(contentBlock.getName());

                    // --- Function arguments
                    // example: "input": {"location": "San Francisco, CA", "unit": "celsius"}
                    LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();
                    Map<String, Object> inputsAnthropic = contentBlock.getInput();
                    //LOGGER.debug(">>> inputsAnthropic: "+ inputsAnthropic);
                    for (Map.Entry<String, Object> entry : inputsAnthropic.entrySet()) {
                        arguments.put(entry.getKey(), entry.getValue());
                    } // for entry
                    function.setArguments(arguments);
                    toolCall.setFunction(function);

                    // add to list of tool calls
                    if (toolCalls == null) toolCalls = new ArrayList<>();
                    toolCalls.add(toolCall);
                } // if tool_use

                if (toolCalls != null) {
                    message.setToolCalls(toolCalls);
                }

                // new choice
                AiResponse.Choice choice = new AiResponse.Choice();
                choice.setIndex(i);
                choice.setMessage(message);
                choice.setFinishReason(respObjAnthropic.getStopReason());
                // add to list of choices
                choices.add(choice);
            } // for content blocks
            aiResponse.setChoices(choices);


            // --------------------- Usage ----------------------------------
            AiResponse.Usage usage = new AiResponse.Usage();
            int inputTokens = respObjAnthropic.getUsage().getInputTokens();
            int outputTokens = respObjAnthropic.getUsage().getOutputTokens();
            usage.setPromptTokens(inputTokens);
            usage.setCompletionTokens(outputTokens);
            usage.setTotalTokens(inputTokens + outputTokens);
            aiResponse.setUsage(usage);

            // --------------------- omitted: role, stop_sequence, type
        } else LOGGER.warn("respObjAnthropic is null!");
        return aiResponse;
    }


    // **********************************************
    // ---------- Responses of streaming ------------
    // **********************************************

    /**
     * Return OpenAIResponse from JSON input string or null (for finished)
     * @param line
     * @return
     */
    public static AiResponseOpenai getOpenAIResponseFromStreaming(String line, ObjectMapper objectMapper, String clientType){
        AiResponseOpenai aiResponseOpenai = null;

        if (CLIENT_TYPE_OLLAMA.equals(clientType)){
            try {
                // Deserialize Ollama JSON string to a OllamaResponse object
                AiResponseOllama ollamaResponse = objectMapper.readValue(line, AiResponseOllama.class);
                // convert the OllamaResponse to a OpenAIResponse
                aiResponseOpenai = toOpenaiResponseForStreaming(ollamaResponse);
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        else if (CLIENT_TYPE_OPENAI.equals(clientType)){
            // response from OpenAI/compatible
            if (line.startsWith("data:")) {
                // Remove "data:" prefix
                String json = line.substring(5).trim();
                try {
                    aiResponseOpenai = objectMapper.readValue(json, AiResponseOpenai.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                LOGGER.warn("Line does not start with \"data:\"!");
            }
        }
        // avoid null output
        if (aiResponseOpenai != null && aiResponseOpenai.getChoices().get(0).getDelta().getContent() == null) {
            aiResponseOpenai = null;
        }
        return aiResponseOpenai;
    }

    /**
     * Given a Ollama-type response object the content data is copied into a OpenAI-type response object
     * @param ol
     * @return
     */
    public static AiResponseOpenai toOpenaiResponseForStreaming(AiResponseOllama ol) {
        AiResponseOpenai oa = null;
        if (ol != null) {
            // create a new OpenAIResponse
            oa = new AiResponseOpenai();

            // --- copy content from OllamaResponse to new OpenAIResponse
            oa.setModel( ol.getModel() );
            // Delta
            AiResponseOpenai.Choice.Delta delta = new AiResponseOpenai.Choice.Delta();
            delta.setContent( ol.getMessage().getContent() );
            // Choice
            AiResponseOpenai.Choice choice = new AiResponseOpenai.Choice();
            choice.setDelta( delta );

            // finish reason
            if (ol.isDone()){
                choice.setFinishReason(AiResponseOpenai.FINISH_REASON_STOP);
            }

            // List of choices
            List<AiResponseOpenai.Choice> choices = new ArrayList<>();
            choices.add( choice );
            oa.setChoices(choices);
        }
        else {
            LOGGER.warn("OllamaResponse is null!");
        }
        return oa;
    }

    /** Build a AiResponseOpenai obj from the parsingResult of streaming */
    public static AiResponseOpenai toOpenaiResponseForStreaming(AnthropicSSELineParser.ParsingResult parsingResult) {
        AiResponseOpenai responseOpenai = null;

        if (parsingResult != null){
            String model = parsingResult.getModel();
            String text = parsingResult.getContent();
            String stopReason = parsingResult.getStopReason();

            // create a new OpenAIResponse
            responseOpenai = new AiResponseOpenai();
            // --- copy content to new OpenAIResponse
            responseOpenai.setModel( model );
            // Delta
            AiResponseOpenai.Choice.Delta delta = new AiResponseOpenai.Choice.Delta();
            delta.setContent( text );
            // Choice
            AiResponseOpenai.Choice choice = new AiResponseOpenai.Choice();
            choice.setDelta( delta );

            // finish reason
            if (stopReason != null){
                choice.setFinishReason(AiResponseOpenai.FINISH_REASON_STOP);
            }

            // List of choices
            List<AiResponseOpenai.Choice> choices = new ArrayList<>();
            choices.add( choice );
            responseOpenai.setChoices(choices);
        }
        else LOGGER.warn("streamResp is null!");
        return responseOpenai;
    }
}
