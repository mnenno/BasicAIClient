package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.config.AiApi;
import it.nenno.basicaiclient.v1.config.ConfigManager;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E4_Groq_streaming {

    public static void main(String[] args) {

        // Read the API configuration for given API provider
        AiApi api = ConfigManager.getApiByProvider("Groq");
        if (api != null && api.getApiKeyValue() != null) {

            // ------------------------------------------------------------
            // To get a streaming response:
            // 1) set stream(true) in the request
            // 2) use the aiClient.streamChat() method and print the chunks
            // ------------------------------------------------------------

            // Define messages to send
            List<AiMessage> messages = new ArrayList<>();
            messages.add(new AiMessage("system", "You are a helpful assistant."));
            messages.add(new AiMessage("user", "What stands RGB for? Answer in just one sentence."));
            System.out.println(MessagePrinter.print(messages));

            // Fill messages into the request to send
            AiRequest aiRequest = new AiRequest.Builder()
                    .model(api.getModel("llama3.3-70b"))
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
            try {
                boolean logDetails = false; // do not show detailed log
                aiClient.streamChat(aiRequest, logDetails, new StreamingResponseHandler() {
                    @Override
                    public void onMessage(AiResponseOpenai response) {
                        // get response content
                        System.out.print(response.getChoices().get(0).getDelta().getContent());
                    }

                    @Override
                    public void onError(Exception e) {
                        System.err.println("An error occurred: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("\n[Streaming Complete]");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ----------------------------------------------

        }
        else System.err.println("Either provider or API key not found!");
    }
}
