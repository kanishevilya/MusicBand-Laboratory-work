package restgateway.routing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import restgateway.json.JacksonMapper;
import restgateway.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestRouter implements HttpHandler {
    
    private final Map<RouteKey, HttpAction> routes = new LinkedHashMap<>();
    private final JsonMapper jsonMapper = new JacksonMapper();

    public void addRoute(String method, String pathPattern, HttpAction action) {
        routes.put(new RouteKey(method.toUpperCase(), pathPattern), action);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            for (Map.Entry<RouteKey, HttpAction> entry : routes.entrySet()) {
                RouteKey routeKey = entry.getKey();
                if (routeKey.method.equals(method)) {
                    Matcher matcher = routeKey.pattern.matcher(path);
                    if (matcher.matches()) {
                        Map<String, String> pathParams = new HashMap<>();
                        for (String groupName : routeKey.variableNames) {
                            pathParams.put(groupName, matcher.group(groupName));
                        }
                        entry.getValue().execute(exchange, pathParams);
                        return;
                    }
                }
            }
            sendError(exchange, 404, "Ресурс не найден: " + method + " " + path);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, "Некорректный запрос: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    public static void sendJson(HttpExchange exchange, int statusCode, String jsonText) throws IOException {
        byte[] bytes = jsonText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, String> errorResponse = Map.of("error", message);
        sendJson(exchange, statusCode, jsonMapper.toJson(errorResponse));
    }

    private static class RouteKey {
        final String method;
        final Pattern pattern;
        final java.util.List<String> variableNames = new java.util.ArrayList<>();

        RouteKey(String method, String rawPattern) {
            this.method = method;
            Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(rawPattern);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                String varName = m.group(1);
                variableNames.add(varName);
                m.appendReplacement(sb, "(?<" + varName + ">[^/]+)");
            }
            m.appendTail(sb);
            this.pattern = Pattern.compile("^" + sb.toString() + "$");
        }
    }
}