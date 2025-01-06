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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AiRequest {

    public static final HashMap<String, String> JSON_FORMAT = new HashMap<>();
    static {
        JSON_FORMAT.put("type", "json_object");
    }

    private final String model;
    private final String prompt; // Only for "generate" requests
    private List<AiMessage> aiMessages; // Only for "chat" requests
    @JsonProperty("tools")
    private final List<Function> tools;
    private final Double temperature; // Optional parameter, default is 0.7
    private final boolean stream;
    private String format;

    @JsonProperty("response_format")
    private Object responseFormat;

    @JsonIgnore
    private RequestType requestType = RequestType.CHAT; // default


    // Private constructor
    private AiRequest(Builder builder) {
        this.model = builder.model;
        this.prompt = builder.prompt;
        this.aiMessages = builder.aiMessages != null ? new ArrayList<>(builder.aiMessages) : null;
        this.temperature = builder.temperature;
        this.responseFormat = builder.responseFormat;
        this.tools = builder.tools;
        this.stream = builder.stream;
    }

    // Public getters
    public String getModel() {
        return model;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<AiMessage> getMessages() {
        return aiMessages != null ? aiMessages : null;
    }

    public Double getTemperature() {return temperature; }

    public Object getResponseFormat() {return responseFormat;}

    public boolean isStream() { return stream; }

    // for defining JSON mode for OpenAI
    public void setResponseFormat(HashMap<String, String> format) {
        this.responseFormat = format;
    }

    // for defining JSON mode for Ollama
    public void setFormat(String format) {this.format = format;}
    public String getFormat() { return format; }

    public List<Function> getTools() {return tools;}

    public RequestType getRequestType() {return requestType;}
    public void setRequestType(RequestType requestType) {this.requestType = requestType;}

    // Builder Class
    public static class Builder {
        private String model;
        private String prompt;
        private List<AiMessage> aiMessages;
        private List<Function> tools;
        private Double temperature = 0.7; // Default temperature
        private boolean stream = false;
        private Object responseFormat;

        // Set model name
        public Builder model(String model) {
            if (model == null || model.isEmpty()) {
                throw new IllegalArgumentException("Model name is required.");
            }
            this.model = model;
            return this;
        }

        // Set prompt (for generate requests)
        public Builder prompt(String prompt) {
            if (aiMessages != null) {
                throw new IllegalArgumentException("Cannot set both prompt and aiMessages. Use either for a valid request.");
            }
            this.prompt = prompt;
            return this;
        }

        // Set aiMessages (for chat requests)
        public Builder messages(List<AiMessage> aiMessages) {
            if (prompt != null) {
                throw new IllegalArgumentException("Cannot set both prompt and aiMessages. Use either for a valid request.");
            }
            if (aiMessages == null || aiMessages.isEmpty()) {
                throw new IllegalArgumentException("Messages list cannot be null or empty.");
            }
            this.aiMessages = new ArrayList<>(aiMessages);
            return this;
        }

        public Builder tools(List<Function> tools) {
            this.tools = tools;
            return this;
        }

        // Set temperature (optional)
        public Builder temperature(Double temperature) {
            if (temperature != null && (temperature < 0.0 || temperature > 2.0)) {
                throw new IllegalArgumentException("Temperature must be between 0.0 and 2.0.");
            }
            this.temperature = temperature;
            return this;
        }

        // Set response format (optional)
        public Builder responseFormat(Object object) {
            if (object != null){
                this.responseFormat = object;
            }
            return this;
        }

        // Set response format (optional)
        public Builder stream(boolean stream) {
            this.stream = stream;
            return this;
        }

        // Build method
        public AiRequest build() {

            if (model == null || model.isEmpty()) {
                throw new IllegalArgumentException("Model name is required.");
            }
            if (prompt == null && aiMessages == null) {
                throw new IllegalArgumentException("Either prompt (for generate requests) or aiMessages (for chat requests) must be set.");
            }
            return new AiRequest(this);
        }
    }

    @Override
    public String toString() {
        return "AiRequest{" +
                "modelName='" + model + '\'' +
                ", prompt='" + prompt + '\'' +
                ", aiMessages=" + aiMessages +
                ", temperature=" + temperature +
                ", stream=" + stream +
                ", format=" + (format==null ? "null" : '\''+ format +'\'') +
                ", responseFormat=" + (responseFormat==null ? "null" : responseFormat) +
                '}';
    }
}
