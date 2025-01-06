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

package it.nenno.basicaiclient.v1.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;

import java.util.List;

public class MessageJsonSchemaGenerator {

    /**
     * Generates a JSON schema for any given class.
     *
     * @param clazz The class for which to generate the schema
     * @return A string containing the JSON schema
     * @throws JsonSchemaGenerationException if schema generation fails
     */
    public static String generateJsonSchema(Class<?> clazz) throws JsonSchemaGenerationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

            // Generate schema for the provided class
            JsonSchema schema = schemaGen.generateSchema(clazz);

            // If it's an ObjectSchema, remove the ID
            if (schema instanceof ObjectSchema) {
                ObjectSchema objectSchema = (ObjectSchema) schema;
                objectSchema.setId(null);
            }

            // Convert schema to pretty JSON string
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);

        } catch (Exception e) {
            throw new JsonSchemaGenerationException("Failed to generate JSON schema for class: " + clazz.getName(), e);
        }
    }

    /**
     * Custom exception for JSON schema generation errors
     */
    public static class JsonSchemaGenerationException extends Exception {
        public JsonSchemaGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class Country {
        @JsonProperty("capital")
        private String capital;

        @JsonProperty("languages")
        private List<String> languages;

        @JsonProperty("name")
        private String name;

        // Default constructor (needed for Jackson)
        public Country() {
        }

        // Constructor with all fields
        public Country(String capital, List<String> languages, String name) {
            this.capital = capital;
            this.languages = languages;
            this.name = name;
        }

        // Getters and Setters
        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public List<String> getLanguages() {
            return languages;
        }

        public void setLanguages(List<String> languages) {
            this.languages = languages;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

        // Example usage in main method
    public static void main(String[] args) {
        try {
            String schema = generateJsonSchema(Country.class);
            System.out.println(schema);
        } catch (JsonSchemaGenerationException e) {
            e.printStackTrace();
        }
    }
}