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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;

public class AnthropicSSELineParser {
    private final ObjectMapper mapper;
    private ParsingResult result;

    // Object to be returned
    public static class ParsingResult {
        private String model;
        private int inputTokens;
        private int outputTokens;
        private String stopReason;
        private String content;
        private boolean isComplete;

        public ParsingResult() {
            this.content = "";
        }

        // Getters
        public String getModel() { return model; }
        public int getInputTokens() { return inputTokens; }
        public int getOutputTokens() { return outputTokens; }
        public String getStopReason() { return stopReason; }
        public String getContent() { return content; }
        public boolean isComplete() { return isComplete; }

        // Setters
        void setModel(String model) { this.model = model; }
        void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
        void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
        void setStopReason(String stopReason) { this.stopReason = stopReason; }
        void setContent(String content) { this.content = content; }
        void setComplete(boolean complete) { this.isComplete = complete; }

        public void resetContent() {this.content = "";}
    }

    // constructor
    public AnthropicSSELineParser() {
        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.result = new ParsingResult();
    }

    // getter and setters
    public ParsingResult getResult() {
        return result;
    }

    // methods
    public void reset() {
        this.result = new ParsingResult();
    }

    public void parseLine(String line) throws IOException {
        // clear the content
       result.resetContent();

        if (line == null || !line.startsWith("data: ")) {
            return;
        }

        String data = line.substring(6);
        AnthroipcStreamingResponses.StreamingResponse response =
                mapper.readValue(data, AnthroipcStreamingResponses.StreamingResponse.class);

        processResponse(response);
    }

    private void processResponse(AnthroipcStreamingResponses.StreamingResponse response) {
        if (response instanceof AnthroipcStreamingResponses.MessageStart) {
            AnthroipcStreamingResponses.MessageStart messageStart = (AnthroipcStreamingResponses.MessageStart) response;
            result.setModel(messageStart.getMessage().getModel());
            result.setInputTokens(messageStart.getMessage().getUsage().getInputTokens());
            result.setOutputTokens(messageStart.getMessage().getUsage().getOutputTokens());
        }
        else if (response instanceof AnthroipcStreamingResponses.ContentBlockDelta) {
            AnthroipcStreamingResponses.ContentBlockDelta delta = (AnthroipcStreamingResponses.ContentBlockDelta) response;
            result.setContent(delta.getDelta().getText());
        }
        else if (response instanceof AnthroipcStreamingResponses.MessageDelta) {
            AnthroipcStreamingResponses.MessageDelta messageDelta = (AnthroipcStreamingResponses.MessageDelta) response;
            if (messageDelta.getDelta().getStopReason() != null) {
                result.setStopReason(messageDelta.getDelta().getStopReason());
            }
            if (messageDelta.getUsage() != null) {
                result.setOutputTokens(messageDelta.getUsage().getOutputTokens());
            }
        }
        else if (response instanceof AnthroipcStreamingResponses.MessageStop) {
            result.setComplete(true);
        }
    }
}