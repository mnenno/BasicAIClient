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

import java.util.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.utils.PrettyJsonPrinter;
import it.nenno.basicaiclient.v1.utils.StructuredOutputHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.nenno.basicaiclient.v1.AiClient.*;



public class RequestConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestConverter.class);

    private boolean logDetails;
    private AiRequestBase aiRequestBase; // base class
    private ObjectMapper objectMapper = null;

    public void convertForClient(AiRequest aiRequest, AiClient aiClient, boolean logDetails){
        this.logDetails = logDetails;

        if (aiRequest == null) {
            throw new IllegalArgumentException("Request object cannot be null.");
        }

        // create client-specific request object and cast to base class
        if (CLIENT_TYPE_OLLAMA.equals(aiClient.getClientType())) {

            // Validate that either `prompt` or `messages` is set but not both
            if ((aiRequest.getPrompt() != null && !aiRequest.getPrompt().isEmpty())
                    && (aiRequest.getMessages() != null && !aiRequest.getMessages().isEmpty())) {
                throw new IllegalArgumentException("Request cannot have both 'prompt' and 'messages' set.");
            }

            // create a new request for Ollama
            AiRequestOllama aiRequestOllama = convertToOllama(aiRequest);
            aiRequestBase = aiRequestOllama;
        }
        else if (CLIENT_TYPE_ANTHROPIC.equals(aiClient.getClientType())) {
            // create a new request for OpenAI or similar
            AiRequestAnthropic aiRequestAnthropic = convertToAnthropic(aiRequest);
            aiRequestBase = aiRequestAnthropic;
        }
        else if (CLIENT_TYPE_OPENAI.equals(aiClient.getClientType())) {
            // create a new request for OpenAI or similar
            AiRequestOpenai aiRequestOpenai = convertToOpenai(aiRequest);
            aiRequestBase = aiRequestOpenai;
        }
        else {
            throw new IllegalArgumentException("Unsupported client type: " + aiClient.getClientType());
        }
        if (logDetails) { LOGGER.info(aiRequestBase.toString()); }
    }

    public String getRequestAsJsonStr()  {
        String asString = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Exclude null values from the JSON output
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            // Serialize the OpenAIRequest object into a JSON string
            asString = objectMapper.writeValueAsString(aiRequestBase);

            // for debug
            if (logDetails) {
                LOGGER.info( PrettyJsonPrinter.print(aiRequestBase));
            }
        }
        catch (IllegalArgumentException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return asString;
    }

    AiRequestOpenai convertToOpenai (AiRequest aiRequest){
        AiRequestOpenai aiRequestOpenai = null;
        if (aiRequest != null) {
            // create instance of an Ollama request from the basic request
            aiRequestOpenai = new AiRequestOpenai(aiRequest);

            // For tools
            if(aiRequest.getTools() != null){
                //convert POJO to nested map-structure for easy manipulation
                List<Map<String, Object>> listOfFunctionObj = new ArrayList<>();
                objectMapper = new ObjectMapper();
                for (int i = 0; i < aiRequest.getTools().size(); i++) {
                    Function function =  aiRequest.getTools().get(i);
                    Map<String, Object> functionRoot = objectMapper.convertValue(function, Map.class);
                    //LOGGER.debug(">>> original function map: "+ functionRoot);

                    // ------ OpenAI-specific modifications ------
                    // 1) add strict in function properties
                    Map<String, Object> functionProperties = (Map)functionRoot.get("function");
                    functionProperties.put("strict", true);
                    // 2) add additionalProperties in function parameters
                    Map<String, Object> functionParameters = (Map)functionProperties.get("parameters");
                    functionParameters.put("additionalProperties", false);

                    //LOGGER.debug(">>> functionRoot after modifications : "+ functionRoot);
                    listOfFunctionObj.add(functionRoot);
                }
                aiRequestOpenai.setListOfFunctionObj(listOfFunctionObj);
            }

            // set response format for JSON mode or structured output
            if (aiRequest.getResponseFormat() != null) {
                if (logDetails) LOGGER.info("getResponseFormat():" + aiRequest.getResponseFormat());

                // if instance of HashMap then JSON mode
                if (aiRequest.getResponseFormat() instanceof HashMap) {
                    aiRequestOpenai.setResponseFormat(aiRequest.getResponseFormat());
                }
                else {
                    // else is Object for structured output mode
                    boolean strict = true;
                    Class<?> clazz = (Class<?>) aiRequest.getResponseFormat();
                    JsonNode schemNode = StructuredOutputHelper.genSchema4Openai(clazz, strict, logDetails);
                    aiRequestOpenai.setResponseFormat(schemNode);
                }
            }
        }
        else LOGGER.warn("Input aiRequest is null!");
        return aiRequestOpenai;
    }

    AiRequestOllama convertToOllama(AiRequest aiRequest){
        AiRequestOllama aiRequestOllama = null;
        if (aiRequest != null) {
            // create instance of an Ollama request from the basic request
            aiRequestOllama = new AiRequestOllama(aiRequest);

            // For tools
            if(aiRequest.getTools() != null){
                // new: convert POJO to nested map-structure
                List<Map<String, Object>> listOfFunctionObj = new ArrayList<>();
                objectMapper = new ObjectMapper();
                for (int i = 0; i < aiRequest.getTools().size(); i++) {
                    Function function =  aiRequest.getTools().get(i);
                    Map<String, Object> mapOfFunction = objectMapper.convertValue(function, Map.class);
                    listOfFunctionObj.add(mapOfFunction);
                }
                aiRequestOllama.setListOfFunctionObj(listOfFunctionObj);
            }

            // set response format for JSON mode or structured output
            if (aiRequest.getResponseFormat() != null) {
                if (logDetails) LOGGER.info("getResponseFormat():" + aiRequest.getResponseFormat());

                // if instance of HashMap then JSON mode
                if (aiRequest.getResponseFormat() instanceof HashMap) {
                    HashMap _map = (HashMap) aiRequest.getResponseFormat();
                    if (_map.containsKey("type") && _map.get("type").equals("json_object")) {
                        aiRequestOllama.setFormat("json");
                    }
                }
                else if (aiRequest.getResponseFormat() instanceof Class<?>){
                    // else is a Class for structured output mode
                    Class<?> clazz = (Class<?>) aiRequest.getResponseFormat();
                    JsonNode schemNode = StructuredOutputHelper.genSchema4Ollama(clazz, logDetails);
                    aiRequestOllama.setFormat(schemNode);
                }
            } // if getResponseFormat is not null

            // set prompt for Ollama generate endpoint
            if (aiRequest.getPrompt() != null){
                aiRequestOllama.setPrompt(aiRequest.getPrompt());
            }
        }
        else LOGGER.warn("Input aiRequest is null!");

        return aiRequestOllama;
    }

    AiRequestAnthropic convertToAnthropic(AiRequest aiRequest){
        AiRequestAnthropic aiRequestAnthropic = null;
        if (aiRequest != null) {

            // if message have a system message remove it from messages and put it into root property 'system'
            String systemMessage = null;
            List<AiMessage> messages = aiRequest.getMessages();
            for (Iterator<AiMessage> it = messages.iterator(); it.hasNext();) {
                AiMessage msg = it.next();
                if ("system".equals(msg.getRole())) {
                    systemMessage = msg.getContent();
                    it.remove();
                }
            }

            // create instance of an Ollama request from the basic request
            aiRequestAnthropic = new AiRequestAnthropic(aiRequest);

            // set system message in 'system' property
            if (systemMessage != null) {
                aiRequestAnthropic.setSystem(systemMessage);
            }

            // max_token of 3.5 models is 8192
            if (aiRequest.getModel().contains("-3-5-")) {
                aiRequestAnthropic.setMaxTokens(8192);
            }

            // set list of tools
            if(aiRequest.getTools() != null){
                //convert POJO to nested map-structure for easy manipulation
                List<Map<String, Object>> listOfFunctionObj = new ArrayList<>();
                objectMapper = new ObjectMapper();
                for (int i = 0; i < aiRequest.getTools().size(); i++) {
                    Function function =  aiRequest.getTools().get(i);

                    // --- 1) Use structure starting with function properties ------
                    Map<String, Object> functionRoot = objectMapper.convertValue(function, Map.class);
                    Map<String, Object> functionProperties = (Map)functionRoot.get("function");

                    // -- 2) change function property name from "parameters" to "input_schema"
                    // save parameters
                    Map<String, Object> functionParameters = (Map)functionProperties.get("parameters");
                    // create new function property "input_schema" with parameters
                    functionProperties.put("input_schema", functionParameters);
                    // delete old property "parameters")
                    functionProperties.remove("parameters");

                    listOfFunctionObj.add(functionProperties);
                }
                aiRequestAnthropic.setListOfFunctionObj(listOfFunctionObj);
            }

        }
        else LOGGER.warn("Input aiRequest is null!");
        return aiRequestAnthropic;
    }

}

