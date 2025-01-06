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

import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponseAnthropic  {
    private List<ContentBlock> content;
    private String id;
    private String model;
    private String role;
    private String stopReason;
    private String type;
    private long created; // UNIX timestamp
    private Usage usage;


    // Getters and Setters
    public List<ContentBlock> getContent() {return content;}
    public void setContent(List<ContentBlock> content) {this.content = content;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}

    public String getStopReason() {return stopReason;}
    public void setStopReason(String stopReason) {this.stopReason = stopReason;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    // Nested class for ContentBlock
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentBlock {

        // properties of normal message blocks
        private String type; // [text, tools_use]
        private String text;

        // properties of tool use
        private String id;
        private String name;
        private LinkedHashMap<String, Object> input;


        // getter and setter
        public String getType() {return type;}
        public void setType(String type) {this.type = type;}

        public String getText() {return text;}
        public void setText(String text) {this.text = text;}

        public String getId() {return id;}
        public void setId(String id) {this.id = id;}

        public String getName() {return name;}
        public void setName(String name) {this.name = name;}

        public LinkedHashMap<String, Object> getInput() {return input;}
        public void setInput(LinkedHashMap<String, Object> input) {this.input = input;}

        @Override
        public String toString() {
            return "ContentBlock{" +
                    "type='" + type + '\'' +
                    ", text='" + text + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", input=" + input +
                    '}';
        }
    }


    // Nested class for Usage
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private int inputTokens;
        private int outputTokens;

        public int getInputTokens() {return inputTokens;}
        public void setInputTokens(int inputTokens) {this.inputTokens = inputTokens;}

        public int getOutputTokens() {return outputTokens;}
        public void setOutputTokens(int outputTokens) {this.outputTokens = outputTokens;}

        @Override
        public String toString() {
            return "Usage{" +
                    "inputTokens=" + inputTokens +
                    ", outputTokens=" + outputTokens +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "AiResponseAnthropic{" +
                "contentBlock=" + content +
                ", id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", role='" + role + '\'' +
                ", stopReason='" + stopReason + '\'' +
                ", type='" + type + '\'' +
                ", created=" + created +
                ", usage=" + usage +
                '}';
    }
}

