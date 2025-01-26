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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Creates JSON schema from given class and API provider
 */
public class StructuredOutputHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(StructuredOutputHelper.class);

    /**
     * Build JSON schema for structured output according the Ollama API
     * @see <a href="https://ollama.com/blog/structured-outputs">https://ollama.com/blog/structured-outputs</a>
     * @param clazz from responseFormat()
     * @return JSON schema object
     */
    public static JsonNode genSchema4Ollama(Class<?> clazz, boolean logDetails){
        JsonNode schemaNode = null;

        if (logDetails) LOGGER.info("clazz: "+clazz);
        String originalSchema = "";
        try {
            originalSchema = MessageJsonSchemaGenerator.generateJsonSchema(clazz);
            if (logDetails) LOGGER.info("original schema: "+ originalSchema);
            // Example: {
            //  "type" : "object",
            //  "properties" : {
            //    "capital" : { "type" : "string" },
            //    "languages" : { "type" : "array", "items" : { "type" : "string" } },
            //    "name" : { "type" : "string" }
            //  }
            //}

            // Parse the schema into JsonNode
            ObjectMapper mapper = new ObjectMapper();
            schemaNode = mapper.readTree(originalSchema);

            // Get all property names from the schema
            List<String> propertyNames = getPropertyNames((ObjectNode) schemaNode);

            // Add the required property
            addRequiredProperty((ObjectNode) schemaNode, propertyNames);
            // Result: {
            //   "type" : "object",
            //   "properties" : {
            //     "capital" : { "type" : "string" },
            //     "languages" : { "type" : "array", "items" : { "type" : "string" } },
            //     "name" : { "type" : "string" }
            //   },
            //   "required": [ "capital","languages", "name" ]
            // }

            // Pretty print the modified schema
            String modifiedSchemaPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
            if (logDetails) LOGGER.info("modified schema: "+modifiedSchemaPretty);

        } catch (MessageJsonSchemaGenerator.JsonSchemaGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return schemaNode;
    }

    /**
     * Build JSON schema for structured output according the OpenAI API
     * @see <a href="https://platform.openai.com/docs/guides/structured-outputs">https://platform.openai.com/docs/guides/structured-outputs</a>
     * @param clazz
     * @param logDetails
     * @return
     */
    public static JsonNode genSchema4Openai(Class<?> clazz, boolean strict, boolean logDetails){
        ObjectNode wrapper = null;

        if (clazz != null) {
            try {

                if (logDetails) LOGGER.info("clazz: "+clazz);

                // ----------------------- build OpenAi structured output wrapper
                //
                // "type": "json_schema",
                //    "json_schema": {
                //      "name": "math_response",
                //      "strict": true,
                //      "schema": [class-schema]

                // Create the outer wrapper JSON object
                ObjectMapper mapper = new ObjectMapper();
                wrapper = mapper.createObjectNode();
                wrapper.put("type", "json_schema");

                // build json_schema property
                ObjectNode jsonSchema = mapper.createObjectNode();
                jsonSchema.put("name", clazz.getSimpleName()+ "_response");

                // ----------------------- build object schema
                String originalSchema = MessageJsonSchemaGenerator.generateJsonSchema(clazz);
                if (logDetails) LOGGER.info("original schema: "+ originalSchema);
                // Example: {
                //  "type" : "object",
                //  "properties" : {
                //    "capital" : { "type" : "string" },
                //    "languages" : { "type" : "array", "items" : { "type" : "string" } },
                //    "name" : { "type" : "string" }
                //  }
                //}

                // Parse the schema into JsonNode
                JsonNode schemaNode = mapper.readTree(originalSchema);

                // Get all property names from the schema
                List<String> propertyNames = getPropertyNames((ObjectNode) schemaNode);

                // Add the required property
                addRequiredProperty((ObjectNode) schemaNode, propertyNames);
                // Result: {
                //   "type" : "object",
                //   "properties" : {
                //     "name" : { "type" : "string" }
                //     "capital" : { "type" : "string" },
                //     "languages" : { "type" : "array", "items" : { "type" : "string" } },
                //   },
                //   "required": [ "capital","languages", "name" ]
                // }
                ((ObjectNode) schemaNode).put("additionalProperties", false);

                // add the object schema
                jsonSchema.set("schema", schemaNode);

                // add strict mode
                jsonSchema.put("strict", strict);

                // add jsonSchema into wrapper
                wrapper.set("json_schema", jsonSchema);


                // Pretty print the modified schema
                String modifiedSchemaPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);
                if (logDetails) LOGGER.info("modified schema: "+modifiedSchemaPretty);

            } catch (MessageJsonSchemaGenerator.JsonSchemaGenerationException e) {
                throw new RuntimeException(e);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else LOGGER.warn("Given clazz is null!");
        return wrapper;
    }

    private static List<String> getPropertyNames(ObjectNode schemaNode) {
        List<String> propertyNames = new ArrayList<>();
        JsonNode propertiesNode = schemaNode.get("properties");

        if (propertiesNode != null && propertiesNode.isObject()) {
            Iterator<String> fieldNames = propertiesNode.fieldNames();
            while (fieldNames.hasNext()) {
                propertyNames.add(fieldNames.next());
            }
        }

        return propertyNames;
    }

    private static void addRequiredProperty(ObjectNode schemaNode, List<String> propertyNames) {
        ArrayNode requiredArray = schemaNode.putArray("required");
        propertyNames.forEach(requiredArray::add);
    }

    public static  <T> T fromJson(String jsonString, Class<?> clazz)  {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Deserialize JSON to POJO
            return (T) objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to " + clazz.getName());
        }
    }
}
