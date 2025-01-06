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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ConfigManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    private static final String CONFIG_FILE = "aiapis.json";
    private static AiApis aiApis;
    private static LinkedHashMap<Integer, ProviderModel> providerModels = new LinkedHashMap<>();

    static {
        loadConfig();
    }

    private ConfigManager() {}

    // getters
    public static synchronized AiApi getApiByProvider(String providerName) {
        if (aiApis == null) return null;
        return aiApis.getAiApis().stream()
                .filter(api -> api.getProviderName().equals(providerName))
                .findFirst()
                .orElse(null);
    }

    /** Get a map of all provider/model key entries */
    public static LinkedHashMap<Integer, ProviderModel> getProviderModels() {
        return providerModels;
    }

    /** Build a map (providerModels) of all provider/model key entries */
    private static void buildProviderModels() {

        // Build a map like this:
        // 0: Ollama/llama3.1
        // 1: Ollama/llama3.1-8b
        // 2: Ollama/llama3.2

        List<AiApi> apiList = aiApis.getAiApis();
        ProviderModel providerModel = null;
        int i = 0;
        for (AiApi api: apiList) {
            for (String modelKey: api.getModelKeys()) {
                providerModel = new ProviderModel(api.getProviderName(), modelKey);
                //LOGGER.debug("Add provider model("+i+"): " + providerModel);
                providerModels.put(i, providerModel);
                i++;
            }
        }
        //LOGGER.debug("Size of providerModels: "+providerModels.size());
    }

    private static void loadConfig() {
        try {
            String json = readConfigFile(CONFIG_FILE);
            aiApis = new ObjectMapper().readValue(json, AiApis.class);
        } catch (Exception e) {
            LOGGER.error("Configuration load failed", e);
            throw new RuntimeException("Config load error", e);
        }

        if (aiApis != null) {
            buildProviderModels();
        }
    }

    private static String readConfigFile(String fileName) {
        try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new IOException("Config file not found: " + fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            LOGGER.error("Error reading config", e);
            throw new RuntimeException("Config read error", e);
        }
    }

    public static void main(String[] args) {
        if (aiApis != null) {

            System.out.println("========= List of APIs =============");
            aiApis.getAiApis().forEach(api -> {
                System.out.println("Provider Name: " + api.getProviderName());
                System.out.println("API URL: " + api.getApiUrl());
                System.out.println("API key: " + api.getApiKey().getKey());
                System.out.println("API env_key: " + api.getApiKey().getEnvKey());
                System.out.println("API getApiKeyValue: " + api.getApiKeyValue());
                System.out.println("Models:");
                for (String key : api.getModelKeys()) {
                    System.out.println("\t- " + key + ": " + api.getModel(key));
                }
            });

            // test providerModels
            System.out.println("\n\n");
            System.out.println("========= Map of providerModels =============");
            System.out.println("Number of models: "+ providerModels.size());
            for (Map.Entry<Integer, ProviderModel> entry : providerModels.entrySet()) {
                Integer id = entry.getKey();
                ProviderModel providerModel = entry.getValue();
                System.out.println(id +": " + providerModel);
            }

            // test getProviderModels
            System.out.println("\n\n");
            System.out.println("========= test getter getProviderModels =============");
            System.out.println("getProviderModels(): "+ getProviderModels());

        }
    }
}