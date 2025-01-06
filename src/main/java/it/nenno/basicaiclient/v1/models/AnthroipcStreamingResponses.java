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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public class AnthroipcStreamingResponses {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MessageStart.class, name = "message_start"),
            @JsonSubTypes.Type(value = ContentBlockStart.class, name = "content_block_start"),
            @JsonSubTypes.Type(value = ContentBlockDelta.class, name = "content_block_delta"),
            @JsonSubTypes.Type(value = ContentBlockStop.class, name = "content_block_stop"),
            @JsonSubTypes.Type(value = MessageDelta.class, name = "message_delta"),
            @JsonSubTypes.Type(value = MessageStop.class, name = "message_stop"),
            @JsonSubTypes.Type(value = PingEvent.class, name = "ping")
    })
    public static abstract class StreamingResponse {
        private String type;
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class MessageStart extends StreamingResponse {
        private Message message;

        public static class Message {
            private String id;
            private String type;
            private String role;
            private List<Object> content;
            private String model;
            private String stopReason;
            private String stopSequence;
            private Usage usage;

            // Getters and setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }
            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getRole() { return role; }
            public void setRole(String role) { this.role = role; }
            public List<Object> getContent() { return content; }
            public void setContent(List<Object> content) { this.content = content; }
            public String getModel() { return model; }
            public void setModel(String model) { this.model = model; }
            public String getStopReason() { return stopReason; }
            public void setStopReason(String stopReason) { this.stopReason = stopReason; }
            public String getStopSequence() { return stopSequence; }
            public void setStopSequence(String stopSequence) { this.stopSequence = stopSequence; }
            public Usage getUsage() { return usage; }
            public void setUsage(Usage usage) { this.usage = usage; }
        }

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }

    public static class ContentBlockStart extends StreamingResponse {
        private int index;
        private ContentBlock contentBlock;

        public static class ContentBlock {
            private String type;
            private String text;

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public ContentBlock getContentBlock() { return contentBlock; }
        public void setContentBlock(ContentBlock contentBlock) { this.contentBlock = contentBlock; }
    }

    public static class ContentBlockDelta extends StreamingResponse {
        private int index;
        private Delta delta;

        public static class Delta {
            private String type;
            private String text;

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public Delta getDelta() { return delta; }
        public void setDelta(Delta delta) { this.delta = delta; }
    }

    public static class ContentBlockStop extends StreamingResponse {
        private int index;

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
    }

    public static class MessageDelta extends StreamingResponse {
        private MessageDeltaInfo delta;
        private Usage usage;

        public static class MessageDeltaInfo {
            private String stopReason;
            private String stopSequence;

            public String getStopReason() { return stopReason; }
            public void setStopReason(String stopReason) { this.stopReason = stopReason; }
            public String getStopSequence() { return stopSequence; }
            public void setStopSequence(String stopSequence) { this.stopSequence = stopSequence; }
        }

        public MessageDeltaInfo getDelta() { return delta; }
        public void setDelta(MessageDeltaInfo delta) { this.delta = delta; }
        public Usage getUsage() { return usage; }
        public void setUsage(Usage usage) { this.usage = usage; }
    }

    public static class MessageStop extends StreamingResponse {}

    public static class PingEvent extends StreamingResponse {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private int inputTokens;
        private int outputTokens;

        public int getInputTokens() { return inputTokens; }
        public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
        public int getOutputTokens() { return outputTokens; }
        public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
    }
}
