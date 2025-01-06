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

import java.util.*;

public class FunctionParameters {
    private String type;
    private Map<String, Object> properties;
    private List<String> required;


    // Getters and setters
    public String getType() {return type; }
    public Map<String, Object> getProperties() {return properties;}
    public List<String> getRequired() { return required;}

    // private Constructor
    private FunctionParameters(Builder builder) {
        this.type = "object";
        this.properties = builder.properties;
        this.required = builder.required;
    }


    // public Builder
    public static class Builder {
        private Map<String, Object> properties= new LinkedHashMap<>();
        private List<String> required= new ArrayList<>();

        public Builder addProperty(String name, String type, String description, List<String> enums, boolean isRequired) {
            Map<String, Object> property = new HashMap<>();
            property.put("type", type);
            property.put("description", description);
            if (enums != null && !enums.isEmpty()) {
                property.put("enum", enums);
            }

            properties.put(name, property);

            if (isRequired) {
                required.add(name);
            }
            return this;
        }

        public Builder addProperty(String name, String type, String description, boolean isRequired) {
            return addProperty(name, type, description, null, isRequired);
        }

        public FunctionParameters build() {
            return new FunctionParameters(this);
        }

    }

    @Override
    public String toString() {
        return "FunctionParameters{" +
                "type='" + type + '\'' +
                ", properties=" + properties +
                ", required=" + required +
                '}';
    }

    // Test
    public static void main(String[] args) {

        FunctionParameters parameters = new FunctionParameters.Builder()
            .addProperty("location", "string", "The location to get weather for.", true)
            .addProperty("unit", "string", "In degree celcius (C) or fahrenheit (F).", Arrays.asList("C", "F"), true)
            .build();

        // Print as JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String parametersAsJson = null;
        try {
            parametersAsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parameters);
            System.out.println(parametersAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // System.out.println(parameters.getParameters());
    }
}
