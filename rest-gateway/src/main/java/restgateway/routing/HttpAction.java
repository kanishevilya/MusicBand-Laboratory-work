package restgateway.routing;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;

@FunctionalInterface
public interface HttpAction {
    void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception;
}