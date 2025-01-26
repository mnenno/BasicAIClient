package it.nenno.basicaiclient.v1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Used in AiClient and AnthropicSSELineParser
 */
public class MapperWithCaseStrategy {
  public  static void setSnakeCase(ObjectMapper objectMapper) {
    try {
      // Check for PropertyNamingStrategies (newer version)
      Class<?> strategiesClass = Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategies");
      Object snakeCaseStrategy = strategiesClass.getField("SNAKE_CASE").get(null);
      objectMapper.setPropertyNamingStrategy((PropertyNamingStrategy) snakeCaseStrategy);
    }
    catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {

      // Fallback to older version: PropertyNamingStrategy
      Class<?> strategyClass = null;
      try {
        strategyClass = Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategy");
        Object snakeCaseStrategy = strategyClass.getField("SNAKE_CASE").get(null);
        objectMapper.setPropertyNamingStrategy((PropertyNamingStrategy) snakeCaseStrategy);
      }
      catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
