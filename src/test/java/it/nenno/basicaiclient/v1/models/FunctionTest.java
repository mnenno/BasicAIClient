package it.nenno.basicaiclient.v1.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FunctionTest {

    @Test
    public void testFunctionBuilder() throws JsonProcessingException {
        Function function = new Function.Builder()
                .name("getWeather")
                .description("Get the current weather for a given location")
                .parameters(new FunctionParameters.Builder()
                        .addProperty("location", "string", "The location to get weather for.", true)
                        .addProperty("unit", "string", "In degree celcius (C) or fahrenheit (F).", Arrays.asList("C", "F"), true)
                        .build()
                )
                .build();


        String asJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(function);
        System.out.println("asJson:\n" + asJson);
        assertNotNull(asJson);
        assertFalse(asJson.isEmpty());

    }
}