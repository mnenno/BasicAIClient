package examples;

import it.nenno.basicaiclient.v1.AiClient;
import examples.functions2call.FunctionRegister;
import examples.functions2call.Orders;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.PrettyJsonPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class E2_Ollama_Tool_calling2 {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // For Tool calling:
        // 1) Define a function
        // 2) add the function to the list of tools
        // 3) add tools to request
        // ------------------------------------------------------------

        //  Define the prompt/messages to send
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a good assistant in helping to find the right function for a given question."));
        messages.add(new AiMessage("user", "Get details about the order with the id: 24745"));
        System.out.println(MessagePrinter.print(messages));


        // Define a function
        String functionName = "getOrder";
        Function getOrder = new Function.Builder()
                .name(functionName)
                .description("Get order details for given order id")
                .parameters(new FunctionParameters.Builder()
                        .addProperty("id", "integer", "The id of an order, e.g. 9999", true)
                        .build()
                )
                .build();

        // for debug
        System.out.println("Function: "+ PrettyJsonPrinter.print(getOrder));

        // add function to list of tools
        List<Function> tools = new ArrayList<>();
        tools.add(getOrder);;

        // Fill the prompt into the request to send
        AiRequest aiRequest = new AiRequest.Builder()
                .model("llama3.1:8b-instruct-q8_0")
                .messages(messages)
                .temperature(0.0)
                .tools(tools)
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


        // get first message
        AiResponse.Message message = aiResponse.getChoices().get(0).getMessage();
        if (message.getToolCalls() != null) {
            // ******************************************
            // get function name and arguments to execute
            // ******************************************

            // get first function
            AiResponse.Function responseFunction = message.getToolCalls().get(0).getFunction();
            System.out.println("responseFunction: " + responseFunction);
            // expected: Function{name='getWeather', arguments={location=New York, unit=C}}

            // extract arguments from response
            Map<String,Object> respArguments = responseFunction.getArguments();
            System.out.println("respArguments: " + respArguments);
            // example: respArguments = {location=New York, unit=C}

            if (responseFunction != null && responseFunction.getName().equals(functionName)) {

                // -----------------  Example-1: Direct function calling  -----------------
                System.out.println("Result1: " + Orders.getOrder(respArguments));

                // -----------------  Example-2: Dynamic method invocation  -----------------
                // register a Java function for the given function name
                FunctionRegistry registry = new FunctionRegistry();
                FunctionRegister.register(functionName, registry);

                // get result from function by invoking the function
                Object result = null;
                try {
                    result = registry.invoke(functionName, respArguments);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }
                System.out.println("Result2: " + result);
                // optional - ask LLM to generate an answer to question and result of function call
            }
            else System.err.println("No function found or has not the name: " + functionName);
        }
        else System.err.println("No tools found");

    }
}
