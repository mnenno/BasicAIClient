package examples.functions2call;

import java.util.HashMap;
import java.util.Map;

public class Orders {

    public static String getOrder(Map<String, Object> arguments) {
        // --- get input argument
        // orderid
        int orderid = (Integer) arguments.get("id");

        // --- process
        String orderDetails = "Order details for id " + orderid;


        return orderDetails;
    }

    public static void main(String[] args) {
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("id", 1);
        System.out.println("Order details: " + getOrder(arguments));
    }
}
