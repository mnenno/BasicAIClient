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

package it.nenno.basicaiclient.v1.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiKey {

    public static final String PUT_YOUR_KEY_HERE = "put-your-key-here";

    @JsonProperty("key")
    private String key;

    @JsonProperty("env_key")
    private String envKey;

    // Constructors
    public ApiKey() {}

    public ApiKey(String key, String envKey) {
        this.key = key;
        this.envKey = envKey;
    }

    // Getters
    public String getKey() {
        return key;
    }

    public String getEnvKey() {
        return envKey;
    }

    // Setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setEnvKey(String envKey) {
        this.envKey = envKey;
    }
}