package examples;

import examples.structures.Country;
import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E2_Ollama_Struct_out {

    public static void main(String[] args) {
        // ************************************************************
        // Structured output using a data class:
        // 1) set in request: responseFormat(<data-class>)
        // 2) get response as Object with getContentObj
        // ************************************************************

        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant giving concise answers."));
        messages.add(new AiMessage("user", "Tell me about France."));
        System.out.println(MessagePrinter.print(messages));

        // Show the data class used for the structured output
        System.out.println("Format  : "+ Country.class.getSimpleName());

        // Fill the prompt into the request to send
        // set the class in 'responseFormat'
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1")
                .messages(messages)
                .responseFormat(Country.class)
                .temperature(0.0)
                .build();


        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:11434/api/chat")
                .apiKey("dummy")
                .build();

        // Utility method to print a waiting message like: Calling ...
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));

        // Send the request to the AI API and get the response
        boolean logDetails = false; // show detailed log
        AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);

        // get content deserialized automatically to contentObj as the same class as set in the request
        Country country = aiResponse.getChoices().get(0).getMessage().getContentObj();
        System.out.println("Response: " + country);

    }
}
