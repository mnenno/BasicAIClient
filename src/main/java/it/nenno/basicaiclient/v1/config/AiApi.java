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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiApi {
    @JsonProperty("provider_name")
    private String providerName;

    @JsonProperty("api_url")
    private String apiUrl;

    @JsonProperty("api_key")
    private ApiKey apiKey;

    @JsonProperty("models")
    private List<Map<String, String>> models;

    // Getters
    public String getProviderName() {
        return providerName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }


    // Helper method to get the actual API key
    public String getApiKeyValue() {
        // If env_key is set, you might want to retrieve from environment
        if (apiKey != null && ApiKey.PUT_YOUR_KEY_HERE.equals(apiKey.getKey()) && apiKey.getEnvKey() != null) {
            return System.getenv(apiKey.getEnvKey());
        }
        // Fallback to the direct key
        return apiKey != null ? apiKey.getKey() : null;
    }

    // get list of all model keys
    public List<String> getModelKeys() {
        List<String> keys = new ArrayList<>();
        for (Map<String, String> model : models) {
            keys.addAll(model.keySet());
        }
        return keys;
    }


    // Helper method to get a specific model by its key
    public String getModel(final String modelKey) {
        for (Map<String, String> model : models) {
            if (model.containsKey(modelKey)) {
                return model.get(modelKey);
            }
        }
        return null;
    }
}
