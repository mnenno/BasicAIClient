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

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private final Map<String, FunctionHandler> functions = new HashMap<>();

    public void registerFunction(String name, FunctionHandler handler) {
        functions.put(name, handler);
    }

    public Object invoke(String name, Map<String, Object> arguments) {
        FunctionHandler handler = functions.get(name);
        if (handler != null) {
            return handler.handle(arguments);
        }
        throw new IllegalArgumentException("Function not found: " + name);
    }

    public interface FunctionHandler {
        Object handle(Map<String, Object> arguments);
    }
}

