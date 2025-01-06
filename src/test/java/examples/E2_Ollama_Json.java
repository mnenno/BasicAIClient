package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.AiMessage;
import it.nenno.basicaiclient.v1.models.AiResponse;
import it.nenno.basicaiclient.v1.models.AiRequest;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;

import static it.nenno.basicaiclient.v1.models.AiRequest.JSON_FORMAT;

import java.util.ArrayList;
import java.util.List;


public class E2_Ollama_Json {

    public static void main(String[] args) {

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Important note: For JSON mode you must do two things:
        // 1) mention in the prompt (system or user) to respond in JSON format (e.g. 'Respond in JSON format')
        // 2) setting the request parameter 'responseFormat' to 'JSON_FORMAT'
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant. Respond in JSON format without enclosing in '```'."));
        messages.add(new AiMessage("user", "Give me the following details about Italy: capital, number of inhabitants, name of the official currency, the official main language and the names of states. Do not add other information."));
        System.out.println(MessagePrinter.print(messages));

        // Fill the prompt into the request to send
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1")
                .messages(messages)
                .temperature(0.0)
                .responseFormat(JSON_FORMAT)
                .build();


        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:11434/api/chat")
                .apiKey("dummy")
                .build();

        System.out.println("Waiting for response...");


        // Send the request to the AI API and get the response
        boolean logDetails = false; // show detailed log
        AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);


        System.out.println("Response: " + aiResponse.getChoices().get(0).getMessage().getContent());
    }
}
