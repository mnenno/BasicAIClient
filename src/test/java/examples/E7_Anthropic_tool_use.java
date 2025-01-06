package examples;

import examples.functions2call.FunctionRegister;
import examples.functions2call.Weather;
import it.nenno.basicaiclient.v1.AiClient;
import it.nenno.basicaiclient.v1.config.AiApi;
import it.nenno.basicaiclient.v1.config.ConfigManager;
import it.nenno.basicaiclient.v1.models.*;
import it.nenno.basicaiclient.v1.utils.MessagePrinter;
import it.nenno.basicaiclient.v1.utils.PrettyJsonPrinter;
import it.nenno.basicaiclient.v1.utils.WaitingPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class E7_Anthropic_tool_use {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // For Tool calling:
        // 1) Define a function
        // 2) add the function to the list of tools
        // 3) add tools to request
        // ------------------------------------------------------------

        // Read the API configuration for given API provider
        AiApi api = ConfigManager.getApiByProvider("Anthropic");
        if (api != null && api.getApiKeyValue() != null) {

            // Define a function
            String functionName = "getWeather";
            Function getWeatherFunction = new Function.Builder()
                    .name(functionName)
                    .description("Get the current weather for a given location")
                    .parameters(new FunctionParameters.Builder()
                            .addProperty("location", "string", "The location to get weather for.", true)
                            .addProperty("unit", "string", "In degree celsius (C) or fahrenheit (F).", Arrays.asList("C", "F"), true)
                            .build()
                    )
                    .build();

            // debug
            System.out.println("Function: "+ PrettyJsonPrinter.print(getWeatherFunction));

            // add the function to the list of tools
            List<Function> tools = new ArrayList<>();
            tools.add(getWeatherFunction);

            // Define messages to send
            List<AiMessage> messages = new ArrayList<>();
            messages.add(new AiMessage("system", "You are a good assistant in helping to find the right function for a given question."));
            messages.add(new AiMessage("user", "What's the weather in New York in celsius?"));
            System.out.println(MessagePrinter.print(messages));

            // Fill messages into the request to send
            AiRequest aiRequest = new AiRequest.Builder()
                    .model(api.getModel("claude-3-haiku"))
                    .messages(messages)
                    .tools(tools)
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
            AiResponse aiResponse = aiClient.generate(aiRequest, logDetails, true);

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
                    System.out.println("Result1: " + Weather.getWeather(respArguments));

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
        else System.err.println("Either provider or API key not found!");
    }
}
