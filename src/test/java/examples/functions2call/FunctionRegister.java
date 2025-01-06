package examples.functions2call;

import it.nenno.basicaiclient.v1.models.FunctionRegistry;
import java.util.Map;


public class FunctionRegister {

    public static void register(String functionName , FunctionRegistry registry){
        if ("getWeather".equals(functionName)) {
            registry.registerFunction("getWeather", new FunctionRegistry.FunctionHandler() {
                @Override
                public Object handle(Map<String, Object> arguments) {
                    return Weather.getWeather(arguments);
                }
            });
        }
        else if ("getOrder".equals(functionName)) {
            registry.registerFunction("getOrder", new FunctionRegistry.FunctionHandler() {
                @Override
                public Object handle(Map<String, Object> arguments) {
                    return Orders.getOrder(arguments);
                }
            });
        }
    }
}
