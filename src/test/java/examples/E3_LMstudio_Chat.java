package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.AiMessage;
import it.nenno.basicaiclient.v1.models.AiResponse;
import it.nenno.basicaiclient.v1.models.AiRequest;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E3_LMstudio_Chat {

    public static void main(String[] args) {


        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant."));
        messages.add(new AiMessage("user", "What stand IBM for?"));
        System.out.println(MessagePrinter.print(messages));

        // Fill the prompt into the request to send
        // With an optional temperature parameter
        AiRequest aiRequest = new AiRequest.Builder()
                .model("meta-llama-3.1-8b-instruct")
                .messages(messages)
                .temperature(0.7)
                // .stream(true)
                .build();

        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:1234/v1/chat/completions")
                .apiKey("dummy")
                .build();

        // Utility method to print a waiting message like: Calling ...
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));

        // Send the request to the AI API and get the response
        boolean logDetails = false; // show detailed log
        AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);

        System.out.println("Response: " + aiResponse.getChoices().get(0).getMessage().getContent());
        System.out.println("Tokens: " + aiResponse.getUsage().getTotalTokens());
    }
}
