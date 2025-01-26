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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponseOpenai {
    private String id;
    private String object;
    private long created; // UNIX timestamp
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint;

    public static final String FINISH_REASON_STOP = "stop";


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    // Nested class for Choice
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private int index;
        private Message message;
        private Object logprobs; // Assuming this could be a Map or another object
        private Delta delta;
        private String finishReason;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public Object getLogprobs() {
            return logprobs;
        }

        public void setLogprobs(Object logprobs) {
            this.logprobs = logprobs;
        }

        @JsonProperty("delta")
        public Delta getDelta() {return delta;}
        public void setDelta(Delta delta) {this.delta = delta;}

        @JsonProperty("finish_reason")
        public String getFinishReason() {
            return finishReason;
        }
        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Delta {
            private String content;

            @JsonProperty("content")
            public String getContent() {return content;}
            public void setContent(String content) {this.content = content;}

            @Override
            public String toString() {
                return "Delta{" +
                        "content='" + content + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Choice{" +
                    "index=" + index +
                    ", message=" + message +
                    ", logprobs=" + logprobs +
                    ", finishReason='" + finishReason + '\'' +
                    '}';
        }
    }

    // Message class for choices
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String role;
        private String content;
        private String refusal;
        private List<ToolCall> toolCalls;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRefusal() {
            return refusal;
        }

        public void setRefusal(String refusal) {
            this.refusal = refusal;
        }

        public List<ToolCall> getToolCalls() {
            return toolCalls;
        }

        public void setToolCalls(List<ToolCall> toolCalls) {
            this.toolCalls = toolCalls;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    ", refusal='" + refusal + '\'' +
                    ", toolCalls=" + toolCalls +
                    '}';
        }
    }

    // Nested class for Usage
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        private TokenDetails promptTokensDetails;
        private TokenDetails completionTokensDetails;

        public int getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }

        public int getCompletionTokens() {
            return completionTokens;
        }

        public void setCompletionTokens(int completionTokens) {
            this.completionTokens = completionTokens;
        }

        public int getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }

        public TokenDetails getPromptTokensDetails() {
            return promptTokensDetails;
        }

        public void setPromptTokensDetails(TokenDetails promptTokensDetails) {
            this.promptTokensDetails = promptTokensDetails;
        }

        public TokenDetails getCompletionTokensDetails() {
            return completionTokensDetails;
        }

        public void setCompletionTokensDetails(TokenDetails completionTokensDetails) {
            this.completionTokensDetails = completionTokensDetails;
        }

        @Override
        public String toString() {
            return "Usage{" +
                    "promptTokens=" + promptTokens +
                    ", completionTokens=" + completionTokens +
                    ", totalTokens=" + totalTokens +
                    ", promptTokensDetails=" + promptTokensDetails +
                    ", completionTokensDetails=" + completionTokensDetails +
                    '}';
        }
    }

    // Nested class for TokenDetails
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenDetails {
        private int cachedTokens;
        private int audioTokens;
        private int reasoningTokens;
        private int acceptedPredictionTokens;
        private int rejectedPredictionTokens;

        public int getCachedTokens() {
            return cachedTokens;
        }

        public void setCachedTokens(int cachedTokens) {
            this.cachedTokens = cachedTokens;
        }

        public int getAudioTokens() {
            return audioTokens;
        }

        public void setAudioTokens(int audioTokens) {
            this.audioTokens = audioTokens;
        }

        public int getReasoningTokens() {
            return reasoningTokens;
        }

        public void setReasoningTokens(int reasoningTokens) {
            this.reasoningTokens = reasoningTokens;
        }

        public int getAcceptedPredictionTokens() {
            return acceptedPredictionTokens;
        }

        public void setAcceptedPredictionTokens(int acceptedPredictionTokens) {
            this.acceptedPredictionTokens = acceptedPredictionTokens;
        }

        public int getRejectedPredictionTokens() {
            return rejectedPredictionTokens;
        }

        public void setRejectedPredictionTokens(int rejectedPredictionTokens) {
            this.rejectedPredictionTokens = rejectedPredictionTokens;
        }

        @Override
        public String toString() {
            return "TokenDetails{" +
                    "cachedTokens=" + cachedTokens +
                    ", audioTokens=" + audioTokens +
                    ", reasoningTokens=" + reasoningTokens +
                    ", acceptedPredictionTokens=" + acceptedPredictionTokens +
                    ", rejectedPredictionTokens=" + rejectedPredictionTokens +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;

        public String getId() {return id;}
        public void setId(String id) {this.id = id;}

        public String getType() {return type; }
        public void setType(String type) {this.type = type;}

        public Function getFunction() {return function;}
        public void setFunction(Function function) {this.function = function;}

        @Override
        public String toString() {
            return "ToolCall{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", function=" + function +
                    '}';
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Function {
        private String name;
        // Jackson has an issue with JSON-in-JSON. Therefore, use a custom deserializer
        @JsonDeserialize(using = ResponseArgumentDeserializer.class)
        private LinkedHashMap<String, Object> arguments;

        public String getName() {return name;}
        public void setName(String name) {this.name = name;}

        public LinkedHashMap<String, Object> getArguments() {return arguments;}
        public void setArguments(LinkedHashMap<String, Object> arguments) {this.arguments = arguments;}

        @Override
        public String toString() {
            return "Function{" +
                    "name='" + name + '\'' +
                    ", arguments=" + arguments +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AiResponse{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", choices=" + choices +
                ", usage=" + usage +
                ", systemFingerprint='" + systemFingerprint + '\'' +
                '}';
    }
}

