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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AiRequestBase {
    private String model;
    @JsonProperty("messages")
    private List<AiMessage> aiMessages;
    private Double temperature;
    private Object format;
    private boolean stream;

    public AiRequestBase(AiRequest basicRequest) {
        this.model = basicRequest.getModel();
        this.aiMessages = basicRequest.getMessages();
        this.temperature = basicRequest.getTemperature();
        this.format = basicRequest.getFormat();
        this.stream = basicRequest.isStream();
    }

    // getters and setters
    public String getModel() {return model;}
    public void setModel(String model) {this.model = model;}

    public List<AiMessage> getAiMessages() {return aiMessages;}
    public void setAiMessages(List<AiMessage> aiMessages) {this.aiMessages = aiMessages;}

    public Double getTemperature() {return temperature;}
    public void setTemperature(Double temperature) {this.temperature = temperature;}


    public Object getFormat() {return format;}
    public void setFormat(Object format) {this.format = format;}

    public boolean isStream() {return stream;}
    public void setStream(boolean stream) {this.stream = stream;}


    @Override
    public String toString() {
        return "AiRequestBase{" +
                "model='" + model + '\'' +
                ", aiMessages=" + aiMessages +
                ", temperature=" + temperature +
                ", format='" + format + '\'' +
                ", stream=" + stream +
                '}';
    }
}
