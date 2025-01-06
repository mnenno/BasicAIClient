package examples;

import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.models.AiResponse;
import it.nenno.basicaiclient.v1.models.AiRequest;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;


public class E1_Ollama_Generate {

    public static void main(String[] args) {

        // Define the prompt to send
        String prompt = "The capital of Canada is";
        System.out.println("Prompt: " + prompt);

        // Fill the prompt into the request to send
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1")
                .prompt(prompt)
                .build();

        // Build client for endpoint
        AiClient aiClient = new AiClient.Builder()
                .apiURL("http://localhost:11434/api/generate")
                .apiKey("dummy")
                .build();

        // Utility method to print a waiting message like: Calling ...
        System.out.println(WaitingPrinter.print(aiRequest, aiClient));

        // Send the request to the AI API and get the response
        AiResponse aiResponse = aiClient.generate(aiRequest, false);
        System.out.println("Response: " + aiResponse.getChoices().get(0).getMessage().getContent());
    }
}
