package examples.functions2call;

import java.util.HashMap;
import java.util.Map;

public class Weather {

    public static Map<String, Object> getWeather(Map<String, Object> arguments){
        // --- get input argument
        String location = (String) arguments.get("location");
        String unit = (String) arguments.get("unit");

        // --- process
        // here: simulate fetching weather data
        Map<String, Object> result = new HashMap<>();
        if("C".equals(unit)){
            result.put("temperature", "22°C");
        } else {
            result.put("temperature", "55°F");
        }
        result.put("condition", "Sunny");
        result.put("location", location);

        return result;
    }
}
