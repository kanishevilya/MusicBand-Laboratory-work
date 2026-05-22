package restgateway.routing.util;

import java.util.HashMap;
import java.util.Map;

public class UriParser {
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) return params;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                params.put(entry[0], entry[1]);
            } else if (entry.length == 1) {
                params.put(entry[0], "");
            }
        }
        return params;
    }
}