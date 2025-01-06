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

import it.nenno.basicaiclient.v1.models.AiMessage;

import java.util.List;
import java.util.stream.Collectors;

public class MessagePrinter {
    /**
     * Prints messages in a formatted way using Java 8 streams
     * @param messages List of messages to print
     */
    public static String print(List<AiMessage> messages) {
        return "Messages\n" + messages.stream()
                .map(message -> String.format("%-8s: %s",
                        message.getRole(),
                        message.getContent()))
                .collect(Collectors.joining("\n"));
    }
}
