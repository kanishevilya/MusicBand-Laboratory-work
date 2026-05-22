package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.FilterByAlbumsCountRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.routing.util.UriParser;

import java.util.Map;

public class FilterByAlbumsCountAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public FilterByAlbumsCountAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        Map<String, String> queryParams = UriParser.parseQuery(exchange.getRequestURI().getQuery());
        String valueStr = queryParams.get("value");

        if (valueStr == null || valueStr.isBlank()) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Missing required query parameter 'value'\"}");
            return;
        }

        try {
            int albumsCount = Integer.parseInt(valueStr);
            FilterByAlbumsCountRequest req = new FilterByAlbumsCountRequest(RequestIdGenerator.nextId(), albumsCount);
            AbstractResponse resp = udpGateway.sendAndReceive(req);
            String responseJson = jsonMapper.toJson(resp);
            RestRouter.sendJson(exchange, 200, responseJson);
        } catch (NumberFormatException e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Query parameter 'value' must be a valid integer\"}");
        }
    }
}