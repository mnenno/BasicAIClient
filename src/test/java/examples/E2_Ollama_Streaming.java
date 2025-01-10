package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;


import java.util.ArrayList;
import java.util.List;

public class E2_Ollama_Streaming {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // To get a streaming response:
        // 1) set stream(true) in the request
        // 2) use the aiClient.streamChat() method and print the chunks
        // ------------------------------------------------------------


        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant."));
        messages.add(new AiMessage("user", "What colors are RGB?"));
        System.out.println(MessagePrinter.print(messages));

        // Fill the prompt into the request to send
        // With an optional temperature parameter
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1")
                .messages(messages)
                .temperature(0.7)
                .stream(true)
                .build();

        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:11434/api/chat")
                .apiKey("dummy")
                .build();

        // Utility method to print a waiting message like: Calling ...
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));

        // ----------------------------------------------
        // call the client and get the streaming response (same as for OpenAi client)
        final StringBuilder accumulator = new StringBuilder();
        try {
            boolean logDetails = false; // do not show detailed log
            aiClient.streamChat(aiRequest, logDetails, new StreamingResponseHandler() {
                StringBuilder memory;
                @Override
                public void onMessage(String chunk) {
                    System.out.print(chunk);
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onComplete(String accumulatedChunks) {
                    System.out.println("\n[Streaming Complete]");
                    // return the accumulated chunks outside the method
                    accumulator.append(accumulatedChunks);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n======\nAccumulated chunks: "+ accumulator);
    }
}
