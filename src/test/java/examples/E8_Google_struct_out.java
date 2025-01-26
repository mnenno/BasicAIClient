package examples;

import examples.structures.Country;
import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.config.AiApi;
import it.nenno.basicaiclient.v1.config.ConfigManager;
import it.nenno.basicaiclient.v1.models.AiMessage;
import it.nenno.basicaiclient.v1.models.AiRequest;
import it.nenno.basicaiclient.v1.models.AiResponse;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E8_Google_struct_out {

    public static void main(String[] args) {

        // ************************************************************
        // Structured output using a data class:
        // 1) set in request: responseFormat(<data-class>)
        // 2) get response as Object with getContentObj
        // ************************************************************

        // Read the API configuration for given API provider
        AiApi api = ConfigManager.getApiByProvider("Google");
        if (api != null && api.getApiKeyValue() != null) {

            // Define messages to send
            List<AiMessage> messages = new ArrayList<>();
            messages.add(new AiMessage("system", "You are a helpful assistant giving concise answers."));
            messages.add(new AiMessage("user", "Tell me about France."));
            System.out.println(MessagePrinter.print(messages));

            // Show the data class used for the structured output
            System.out.println("Format  : "+ Country.class.getSimpleName());

            // Fill messages into the request to send
            // set the class in 'responseFormat'
            AiRequest aiRequest = new AiRequest.Builder()
                    .model(api.getModel("gemini-2-flash-exp"))
                    .messages(messages)
                    .responseFormat(Country.class)
                    .temperature(0.0)
                    .build();

            // Build client for endpoint
            AiClient aiClient = new AiClient.Builder()
                    .apiURL(api.getApiUrl())
                    .apiKey(api.getApiKeyValue())
                    .build();

            // Utility method to print a waiting message like: Calling ...
            System.out.println(WaitingPrinter.print(aiRequest, aiClient));

            // Send the request to the AI API and get the response
            boolean logDetails = false; // show detailed log
            AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);

            // get content deserialized automatically to contentObj as the same class as set in the request
            Country country = aiResponse.getChoices().get(0).getMessage().getContentObj();
            System.out.println("Response: " + country);
            System.out.println("Tokens: " + aiResponse.getUsage().getTotalTokens());
        }
        else System.err.println("Either provider or API key not found!");
    }
}
