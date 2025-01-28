# BasicAIClient

![BasicAIClient logo](http://www.nenno.it/img/basicaiclient_logo.jpg)<br>
A basic Java client library to access both local inference servers and 
remote Generative AI providers with a simple and unified interface. 

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
  - [Local inference server](#local-inference-server)
  - [API keys](#api-keys) 
- [Examples](#examples) 
  - [Basic example](#basic-example)
  - [Common example](#common-example)
  - [Example for remote API provider and use of ConfigManager](#example-for-remote-api-provider-and-use-of-configmanager)
  - [API configuration file](#api-configuration-file)
- [License](#license)

---

## Overview
`BasicAIClient` is a Java client library for text-to-text generation features with low requirements 
that is compatible to local inferences servers like Ollama and LM Studio and remote API providers 
like OpenAI and compatible as well as Anthropic. 

The simple and unified interface of the client to the APIs makes it easy to use with different LLMs of different providers.

The basic idea for this project was also inspired by the Python project [aisuite](https://github.com/andrewyng/aisuite) 
but extended to further API features of JSON mode, function/tool calling, and structured output.

---

## Features
- it can be useful for learning the basics of text-to-text generative AI with minimal resources
- has low requirements of JDK and external libraries
- tested with local inference servers as Ollama and LM Studio
- tested with OpenAI-compatible remote API providers as Groq and OpenRouter
- tested with OpenAI and Anthropic 
- supported API features: temperature, streaming, JSON mode, function/tool calling, structured output (Ollama and OpenAI)
- a logDetails variable, that shows detailed logging when set to true
- has an optional API configuration manager for multiple API providers with a resource file in JSON format 
- the unified API and the API configuration manager makes it easy to test the same prompt with multiple APIs programmatically
---

## Requirements
- **JDK Version**: JDK 8 or later.
- **Dependencies**:
    - [FasterXML Jackson JSON library](https://github.com/FasterXML/jackson) version 2.8.8 or later
    - SLF4J with log4j 2.20.0 (or with bridge dependency to log4j 1.x)

---

## Installation

### Manual installation of the BasicAIClient
- Install the dependencies in the [pom.xml](pom.xml) file
- Download the latest BasicAIClient jar file from the release section
- be sure you have a logging configuration file
- Add the dependencies and the BasicAIClient jar file to your project's classpath

---

## Usage

### Local inference server

If you want to use a local inference server like [Ollama](https://ollama.com/) or [LM Studio](https://lmstudio.ai/), first install 
it according the instructions provided by their documentation.

### API keys
For use with local inference servers you don’t need an API key. However, in order to use remote APIs, you’ll need to obtain an API key from the provider. Refer to the provider’s documentation for instructions on how to get an API key.

Some providers, like [Groq](https://groq.com/) and [OpenRouter](https://openrouter.ai/), offer free access (with rate limits) to certain 
open-source or open-weight models. In contrast, owners of proprietary LLMs such as [OpenAI](https://platform.openai.com/docs/quickstart) 
or [Anthropic](https://docs.anthropic.com/en/docs/initial-setup), 
only offer paid access to their APIs.

There are two ways to set up the API key/s:

1. As environment variable/s depending on your operating system:
   * For macOS or Linux system:
     ```bash
     export GROQ_API_KEY="your-groq-api-key"
     export OPENROUTER_API_KEY="your-openrouter-api-key"
     export OPENAI_API_KEY="your-openai-api-key"
     export ANTHROPIC_API_KEY="your-anthropic-api-key"
     ```
   * For Windows:
     ```cmd
     set GROQ_API_KEY=your-groq-api-key
     set OPENROUTER_API_KEY=your-openrouter-api-key
     set OPENAI_API_KEY=your-openai-api-key
     set ANTHROPIC_API_KEY=your-anthropic-api-key
     ```
2. or save it in the [API configuration file](#api-configuration-file).

## Examples

For the complete list of examples see the folder: [src/test/java/examples](src/test/java/examples)

### Basic example
Below is the most basic example calling a local inference server (here Ollama) with API settings given as literals.

```java
// imports omitted
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
```

Expected output:
```bash
Prompt: The capital of Canada is
Calling : llama3.1 at http://localhost:11434/api/generate. Waiting for response...

Response: Ottawa.
```

### Common example
Below is a common example using a system and a user message calling a local inference server. 
The request has an optional setting for the temperature parameter.
In order to see more details of the request and response the 'logDetails' parameter is set to true.

```java
// imports omitted
public class E2_Ollama_Chat {
  public static void main(String[] args) {

    //  Define the prompt/messages to send
    List<AiMessage> messages = new ArrayList<>();
    messages.add(new AiMessage("system", "You are a helpful assistant."));
    messages.add(new AiMessage("user", "What stands RGB for?"));
    System.out.println(MessagePrinter.print(messages));

    // Fill the prompt into the request to send
    // With an optional temperature parameter
    AiRequest aiRequest = new AiRequest.Builder()
            .model("llama3.1")
            .messages(messages)
            .temperature(0.7)
            .build();

    // Build client for endpoint
    AiClient aiClient = new AiClient.Builder()
            .apiURL("http://localhost:11434/api/chat")
            .apiKey("dummy")
            .build();

    // Utility method to print a waiting message like: Calling ...
    System.out.println(WaitingPrinter.print(aiRequest, aiClient));

    // Send the request to the AI API and get the response
    boolean logDetails = false; // do not show detailed log
    AiResponse aiResponse = aiClient.generate(aiRequest, logDetails);
    
    System.out.println("Response: " + aiResponse.getChoices().get(0).getMessage().getContent());
    System.out.println("Tokens: " + aiResponse.getUsage().getTotalTokens());
  }
}
```

### Example for remote API provider and use of ConfigManager
```java
// imports omitted
public class E4_Groq {

  public static void main(String[] args) {

    // Read the API configuration for given API provider
    AiApi api = ConfigManager.getApiByProvider("Groq");
    if (api != null && api.getApiKeyValue() != null) {

      // Define messages to send
      List<AiMessage> messages = new ArrayList<>();
      messages.add(new AiMessage("system", "You are a helpful assistant."));
      messages.add(new AiMessage("user", "Why the sky is blue?"));
      System.out.println(MessagePrinter.print(messages));

      // Fill messages into the request to send
      AiRequest aiRequest = new AiRequest.Builder()
              .model(api.getModel("llama3.3-70b"))
              .messages(messages)
              .temperature(0.7)
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

      System.out.println("Response: " + aiResponse.getChoices().get(0).getMessage().getContent());
      System.out.println("Tokens: " + aiResponse.getUsage().getTotalTokens());
    }
    else System.err.println("Either provider or API key not found!");
  }
}
```

### Other examples
In the folder [src/test/java/examples](src/test/java/examples) you find more examples for other supported features:
- JSON mode
- response streaming
- tool/function calling
- structured output
- use of other remote API providers (OpenRouter, OpenAI and Anthropic)

Note, that not all API providers do support all features  (e.g. Anthropic has no explicit JSON mode) 


### API configuration file
To centralize the API configurations you can create the file 'aiapis.json' in the directory 'resources'.
Below you find an example of a configuration file with a local API (Ollama) and a remote API provider (Groq).
Local APIs usually do not require a key, while for remote APIs a key is mandatory.

You can define the API key of remote APIs either as literal ("key") or as an environment variable ("env_key"). 
Check the documentation of your IDE how to set an environment variable.

```json
{
  "ai_apis": [
    {
      "provider_name": "Ollama",
      "api_url": "http://localhost:11434/api/chat",
      "api_key": {"key": "dummy-key", "env_key": null},
      "models": [
        {"llama3.1": "llama3.1"}
      ]
    },
    {
      "provider_name": "Groq",
      "api_url": "https://api.groq.com/openai/v1/chat/completions",
      "api_key": {"key": "put-your-key-here", "env_key": "GROQ_API_KEY"},
      "models": [
        {"llama3.3-70b": "llama-3.3-70b-versatile"}
      ]
    },
    {
      "provider_name": "OpenAI",
      "api_url": "https://api.openai.com/v1/chat/completions",
      "api_key": {"key": "put-your-key-here", "env_key": "OPENAI_API_KEY"},
      "models": [
        {"gpt-4o-mini": "gpt-4o-mini"}
      ]
    }
  ]
}
```

---

## License
This project is licensed under the [Apache License, Version 2.0](LICENSE.txt).




