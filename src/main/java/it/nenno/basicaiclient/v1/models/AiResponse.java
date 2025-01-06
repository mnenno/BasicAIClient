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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiResponse {
    private String id;
    private String object; // not used here
    private long created; // UNIX timestamp
    private String model;
    private List<Choice> choices; // Array of Choice objects
    private Usage usage; // Nested Usage object


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

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

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
    public static class Choice {
        private int index;
        private Message message;
        private Object logprobs; // Assuming this could be a Map or another object
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

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
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

    // Modified Message class for choices
    public static class Message {
        private String role;
        private String content;
        private String refusal;
        private List<ToolCall> toolCalls;
        @JsonIgnore
        private Object contentObj; // only set for structured output mode

        // getter and setter
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {return content;}
        public void setContent(String content) {this.content = content;}

        public <T> T getContentObj() {return (T) contentObj;}
        public void setContentObj(Object contentObj) {this.contentObj = contentObj;}

        public String getRefusal() {
            return refusal;
        }
        public void setRefusal(String refusal) {
            this.refusal = refusal;
        }

        public List<ToolCall> getToolCalls() {return toolCalls;}
        public void setToolCalls(List<ToolCall> toolCalls) {this.toolCalls = toolCalls;}

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
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;

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

        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }

        public int getTotalTokens() {
            return totalTokens;
        }



        @Override
        public String toString() {
            return "Usage{" +
                    "promptTokens=" + promptTokens +
                    ", completionTokens=" + completionTokens +
                    ", totalTokens=" + totalTokens +
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
                '}';
    }
}

