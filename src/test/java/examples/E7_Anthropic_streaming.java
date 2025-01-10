package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.config.AiApi;
import it.nenno.basicaiclient.v1.config.ConfigManager;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E7_Anthropic_streaming {

    public static void main(String[] args) {

        // Read the API configuration for given API provider
        AiApi api = ConfigManager.getApiByProvider("Anthropic");
        if (api != null && api.getApiKeyValue() != null) {

            // ------------------------------------------------------------
            // To get a streaming response:
            // 1) set stream(true) in the request
            // 2) use the aiClient.streamChat() method and print the chunks
            // ------------------------------------------------------------

            // Define messages to send
            List<AiMessage> messages = new ArrayList<>();
            messages.add(new AiMessage("system", "You are a helpful assistant."));
            messages.add(new AiMessage("user", "What colors are RGB?"));
            System.out.println(MessagePrinter.print(messages));

            // Fill messages into the request to send
            AiRequest aiRequest = new AiRequest.Builder()
                    .model(api.getModel("claude-3-5-sonnet"))
                    .messages(messages)
                    .temperature(0.7)
                    .stream(true)
                    .build();

            // Build client for endpoint
            AiClient aiClient = new AiClient.Builder()
                    .apiURL(api.getApiUrl())
                    .apiKey(api.getApiKeyValue())
                    .build();

            // Utility method to print a waiting message like: Calling ...
            System.out.println(WaitingPrinter.print(aiRequest, aiClient));


            // ----------------------------------------------
            // call the client and get the streaming response
            final StringBuilder accumulator = new StringBuilder();
            try {
                boolean logDetails = true; // do not show detailed log
                aiClient.streamChat(aiRequest, logDetails, new StreamingResponseHandler() {
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
                        accumulator.append(accumulatedChunks);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ----------------------------------------------
            //System.out.println("\n\nAccumulated chunks: "+ accumulator);
        }
        else System.err.println("Either provider or API key not found!");
    }
}
