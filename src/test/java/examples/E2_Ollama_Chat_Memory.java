package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.AiMessage;
import it.nenno.basicaiclient.v1.models.AiRequest;
import it.nenno.basicaiclient.v1.models.AiResponse;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.List;

public class E2_Ollama_Chat_Memory {

    public static void main(String[] args) {

        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant."));
        messages.add(new AiMessage("user", "Hello, my name is John Doe. My hobbies are cars and football."));
        System.out.println(MessagePrinter.print(messages));

        // Fill the prompt into the request to send
        // With an optional temperature parameter
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1")
                .messages(messages)
                .temperature(0.7)
                //.stream(true)
                .build();

        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:11434/api/chat")
                .apiKey("dummy")
                .build();

        // Utility method to print a waiting message like: Calling ...
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));

        // -------- First turn
        // Send the request to the AI API and get the response
        boolean logDetails = true; // do not show detailed log
        AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);
        System.out.println("Response1: " + aiResponse.getChoices().get(0).getMessage().getContent());


        // -------- Second turn
        messages.add(new AiMessage("user", "Do you remember what was is name any my favorite sport?"));
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));
        aiResponse = aiClient.generate(aiRequest, logDetails);
        System.out.println("Response2: " + aiResponse.getChoices().get(0).getMessage().getContent());

    }
}
