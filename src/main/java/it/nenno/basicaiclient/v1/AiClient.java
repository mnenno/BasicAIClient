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

package it.nenno.basicaiclient.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MapperWithCaseStrategy;
import it.nenno.basicaiclient.v1.utils.PrettyJsonPrinter;
import it.nenno.basicaiclient.v1.utils.StructuredOutputHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiClient.class);

    public static final String CLIENT_TYPE_OPENAI = "openai";
    public static final String CLIENT_TYPE_OLLAMA= "ollama";
    public static final String CLIENT_TYPE_ANTHROPIC= "anthropic";

    private static final String VERSION = Version.VERSION;
    private String apiKey;
    private String apiURL;
    private String clientType;

    public AiResponse generate(AiRequest aiRequest, boolean logDetails) {
        return generate(aiRequest, logDetails, true);
    }

    public AiResponse generate(AiRequest aiRequest, boolean logDetails, boolean doSend){
        AiResponse aiResponse = null;

        // log the client
        if (logDetails) LOGGER.info("aiClient = " + this);

        if (aiRequest != null) {
            // ----------------------------------

            // modify request type if necessary (default: CHAT)
            if (aiRequest.getResponseFormat() != null && aiRequest.getResponseFormat() instanceof Class<?>){
                aiRequest.setRequestType(RequestType.STRUCTURED_OUTPUT);
            }

            // build the request
            RequestConverter requestConverter = new RequestConverter();
            requestConverter.convertForClient(aiRequest, this, logDetails);
            String requestAsJsonStr = requestConverter.getRequestAsJsonStr();
            if (logDetails) LOGGER.info("requestAsJsonStr = " + requestAsJsonStr);

            if (doSend) {
                if (requestAsJsonStr != null && !requestAsJsonStr.isEmpty()) {
                    // -----------------------------------
                    // Send and get the response from the AI API
                    // -----------------------------------
                    String responseStr = send(requestAsJsonStr, logDetails);
                    if (logDetails) LOGGER.info("responseStr: "+ responseStr);

                    if (responseStr != null) {
                        // ----------------------------------------------
                        // Deserialize JSON string to AiResponseOllama object
                        // Create an ObjectMapper instance
                        ObjectMapper objectMapper = new ObjectMapper();
                        MapperWithCaseStrategy.setSnakeCase(objectMapper);
                        //objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

                        try {
                            // convert to common AiResponse
                            if (CLIENT_TYPE_OLLAMA.equals(this.clientType)) {
                                AiResponseOllama respObjOllama = objectMapper.readValue(responseStr, AiResponseOllama.class);
                                aiResponse = ResponseConverter.normalizeOllama(respObjOllama);
                            }
                            else if (CLIENT_TYPE_ANTHROPIC.equals(this.clientType)){
                                AiResponseAnthropic respObjAnthropic = objectMapper.readValue(responseStr, AiResponseAnthropic.class);
                                aiResponse = ResponseConverter.normalizeAnthropic(respObjAnthropic);
                            }
                            else {
                                // else OpenAI-type
                                AiResponseOpenai respObjOpenai = objectMapper.readValue(responseStr, AiResponseOpenai.class);
                                aiResponse = ResponseConverter.normalizeOpenai(respObjOpenai);
                            }

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        // Print the deserialized object for debug
                        if (logDetails){
                            if (aiResponse != null) {
                                LOGGER.info( PrettyJsonPrinter.print(aiResponse));
                            } else {
                                LOGGER.warn("aiResponse object is null!");
                            }
                        }

                        // experimental: set 'contentObj' if request is of type structured output
                        if (RequestType.STRUCTURED_OUTPUT.equals(aiRequest.getRequestType())){
                            // get the class from the request
                            Class<?> clazz = (Class<?>) aiRequest.getResponseFormat();

                            // read JSON object, convert to class
                            String jsonString = aiResponse.getChoices().get(0).getMessage().getContent();

                            // set class to contentObj
                            aiResponse.getChoices().get(0).getMessage().setContentObj( StructuredOutputHelper.fromJson(jsonString, clazz) );
                        }
                    }
                    else LOGGER.warn("responseStr is null!");
                }
                else LOGGER.warn("requestAsJsonStr is blank!");
            }
            else LOGGER.warn("Request not send since doSend is false.");
        }
        else LOGGER.warn("aiRequest is null!");

        if (logDetails) LOGGER.info("aiResponse = " + aiResponse);

        return aiResponse;
    }

    /** set authorization according the client type */
    private void setAuthorization(HttpURLConnection connection, String apiKey){
        if (CLIENT_TYPE_OPENAI.equals(this.clientType)) {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }
        else if (CLIENT_TYPE_ANTHROPIC.equals(this.clientType)) {
            connection.setRequestProperty("x-api-key", apiKey);
        }
    }

    private void setAnthropicVersion(HttpURLConnection connection){
        connection.setRequestProperty("anthropic-version", "2023-06-01");
    }

    private String send(String json, boolean logDetails) {
        HttpURLConnection connection = null;
        String response = "";

        if (logDetails) LOGGER.info("apiURL = " + apiURL +", apiKey = " + apiKey);
        try {
            URL url = new URL(apiURL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            setAuthorization(connection, apiKey);
            if (CLIENT_TYPE_ANTHROPIC.equals(this.clientType)) {
                setAnthropicVersion(connection);
            }
            connection.setRequestProperty("User-Agent", "Application");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }

            if (logDetails) LOGGER.info(connection.getResponseCode()+" "+connection.getResponseMessage());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                StringBuilder errorMessages = new StringBuilder()
                        .append("Unexpected HTTP response: ")
                        .append(connection.getResponseCode())
                        .append("\n");

                String errorResponse = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream(),
                        StandardCharsets.UTF_8
                )).lines().collect(Collectors.joining("\n"));
                throw new Exception(errorResponse);
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                // convert response into a single line
                response = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));
                if (logDetails)  LOGGER.info("response = " + response);
                return response;
            } else {
                LOGGER.warn("No input stream available");
                //return null;
            }

        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return null;
    }

    // ------- Streaming -------
    public void streamChat(AiRequest aiRequest, boolean logDetails, StreamingResponseHandler handler) {
        streamChat(aiRequest, logDetails, true, handler);
    }

    public void streamChat(AiRequest aiRequest, boolean logDetails, boolean doSend, StreamingResponseHandler handler) {
        // set true only for debugging
        boolean logLines = false;

        // log the client
        if (logDetails) LOGGER.info("aiClient = " + this);

        boolean isOllama = CLIENT_TYPE_OLLAMA.equals(this.clientType);
        if (logDetails) LOGGER.info("isOllama = " + isOllama);

        if (aiRequest != null) {
            // ------ build the request  ------
            RequestConverter requestConverter = new RequestConverter();
            requestConverter.convertForClient(aiRequest, this, logDetails);
            String requestAsJsonStr = requestConverter.getRequestAsJsonStr();
            if (logDetails) LOGGER.info("requestAsJsonStr = " + requestAsJsonStr);

            if (requestAsJsonStr != null && !requestAsJsonStr.isEmpty()){
                if (doSend) {
                    HttpURLConnection connection = null;
                    AnthropicSSELineParser.ParsingResult anthropicSSEparsingResult = null;
                    try {
                        // Instantiate an SSE-parser for Anthropic client if needed
                        AnthropicSSELineParser anthropicSSEparser = null;
                        if (CLIENT_TYPE_ANTHROPIC.equals(this.clientType)) {
                            anthropicSSEparser = new AnthropicSSELineParser();
                        }

                        // Setup connection
                        URL url = new URL(apiURL);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        setAuthorization(connection, apiKey);
                        if (CLIENT_TYPE_ANTHROPIC.equals(this.clientType)) {
                            setAnthropicVersion(connection);
                        }
                        connection.setRequestProperty("User-Agent", "Application");
                        connection.setDoOutput(true);

                        // ------ Send the request ---
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(requestAsJsonStr.getBytes("UTF-8"));
                            os.flush();
                        }

                        // Check response code
                        int responseCode = connection.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            try (BufferedReader errorReader = new BufferedReader(
                                    new InputStreamReader(connection.getErrorStream()))) {
                                StringBuilder errorResponse = new StringBuilder();
                                String errorLine;
                                while ((errorLine = errorReader.readLine()) != null) {
                                    errorResponse.append(errorLine).append("\n");
                                }
                                String message = "HTTP error code: " + responseCode +
                                        "\nError response: " + errorResponse.toString();
                                LOGGER.warn(message);
                                throw new RuntimeException(message);
                            }
                        }

                        // Get the response
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Read response stream
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            AiResponseOpenai response;
                            // TEST
                            StringBuilder chunksBuilder = new StringBuilder();

                            while ((line = reader.readLine()) != null) {
                                if (!line.isEmpty()) {
                                    // For debug (but comment the normal output to see just the response)
                                    if (logLines) LOGGER.info("line:\n"+line);

                                    if (!CLIENT_TYPE_ANTHROPIC.equals(this.clientType)) {
                                        // -------- For OpenAi-like responses (JSONL format)

                                        // stop anyway if response is [DONE] due to strange responses of Gemini
                                        int positionOfDone = line.indexOf("[DONE]");
                                        if (positionOfDone != -1 && positionOfDone < 10) {
                                            if (logDetails) LOGGER.info(" DONE reached");
                                            handler.onComplete(chunksBuilder.toString());
                                            break;
                                        }

                                        response = ResponseConverter.getOpenAIResponseFromStreaming(line, objectMapper, this.clientType);
                                        if (response != null) {
                                            String chunk = response.getChoices().get(0).getDelta().getContent();
                                            handler.onMessage(chunk);
                                            // accumulate the chunks
                                            chunksBuilder.append(chunk);

                                            // check if was the last response
                                            if (AiResponseOpenai.FINISH_REASON_STOP.equals(response.getChoices().get(0).getFinishReason())) {
                                                if (logDetails) LOGGER.info("[Streaming Complete]");
                                                handler.onComplete(chunksBuilder.toString());
                                                break;
                                            }
                                        }
                                        else {
                                            if (logDetails) LOGGER.info("[Streaming Complete response null]");
                                            handler.onComplete(chunksBuilder.toString());
                                            break;
                                        } // else-if response not null
                                    }
                                    else {
                                        // ------ Anthropic-type responses (mix of text and JSON lines)
                                        // get the parsing result object of the line from the streaming SSE response
                                        anthropicSSEparser.parseLine(line);
                                        anthropicSSEparsingResult = anthropicSSEparser.getResult();

                                        // convert the parsing result object to an Openai-type response
                                        response = ResponseConverter.toOpenaiResponseForStreaming(anthropicSSEparsingResult);
                                        if (response != null) {
                                            String chunk = response.getChoices().get(0).getDelta().getContent();
                                            handler.onMessage(chunk);
                                            // accumulate the chunks
                                            chunksBuilder.append(chunk);

                                            // check if was the last response
                                            if (AiResponseOpenai.FINISH_REASON_STOP.equals(response.getChoices().get(0).getFinishReason())) {
                                                if (logDetails) LOGGER.info("[Streaming Complete]");
                                                handler.onComplete(chunksBuilder.toString());
                                                break;
                                            }
                                        } // if response is not null
                                    } // else-if Anthropic client
                                } // if line is not empty
                            } // while
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage());
                        handler.onError(e);
                    }
                    finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                } // if doSend
            } // if requestAsJsonStr is not blank
        }
        else LOGGER.warn("aiRequest is null!");
    }

    public String getApiURL() { return apiURL;}

    public String getClientType() {return clientType;}

    // build a client
    private AiClient(Builder builder) {
        this.apiKey = builder.apiKey;
        this.apiURL = builder.apiURL;
        this.clientType = builder.clientType;
    }

    // The builder class
    public static class Builder {

        // all optional parameters
        private String apiKey;
        private String apiURL;
        private String clientType;


        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiURL(String apiURL) {
            this.apiURL = apiURL;
            
            // set client type by URL
            if (apiURL.contains(":11434/")) {
                this.clientType = CLIENT_TYPE_OLLAMA;
            }
            else if (apiURL.contains("anthropic.com/")) {
                this.clientType = CLIENT_TYPE_ANTHROPIC;
            }
            else {
                this.clientType = CLIENT_TYPE_OPENAI;
            }
            return this;
        }

        public AiClient build() {
            return new AiClient(this);
        }
    }

    @Override
    public String toString() {
        return "BasicAiClient0{" +
                "VERSION='" + VERSION + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", apiURL='" + apiURL + '\'' +
                ", clientType='" + clientType + '\'' +
                '}';
    }
}
