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

import java.util.LinkedHashMap;
import java.util.Map;

public class Function {
    private String type;
    @JsonProperty("function")
    private Map<String, Object> functionMap;

    // Getters and setters
    public String getType() { return type; }
    public Map<String, Object> getFunctionMap() { return functionMap; }

    // private constructor
    private Function(Builder builder) {
        this.type = "function";
        this.functionMap = builder.functionMap;
    }

    @Override
    public String toString() {
        return "Function{" +
                "type='" + type + '\'' +
                ", functionMap=" + functionMap +
                '}';
    }

    // BuilderClass
    public static class Builder {
        private Map<String, Object> functionMap = new LinkedHashMap<>();

        public Builder name(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name is required.");
            }
            functionMap.put("name", name);
            return this;
        }

        public Builder description(String description) {
            if (description == null || description.isEmpty()) {
                throw new IllegalArgumentException("Description is required.");
            }
            functionMap.put("description", description);
            return this;
        }

        public Builder parameters(FunctionParameters parameters) {
            functionMap.put("parameters", parameters);
            return this;
        }

        public Function build() {
            return new Function(this);
        }
    }

    // return as one map
/*    public Map<String, Object> toMap() {
        Map<String, Object> functionMap = new LinkedHashMap<>();
        functionMap.put("name", name);
        functionMap.put("description", description);
        functionMap.put("parameters", parameters);

        Map<String, Object> outerMap = new LinkedHashMap<>();
        outerMap.put("type", "function");
        outerMap.put("function", functionMap);

        return outerMap;
    }*/
}

