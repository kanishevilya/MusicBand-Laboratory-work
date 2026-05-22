package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.RemoveRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.util.Map;

public class RemoveAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public RemoveAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        try {
            long key = Long.parseLong(pathParams.get("key"));
            RemoveRequest req = new RemoveRequest(RequestIdGenerator.nextId(), key);
            AbstractResponse resp = udpGateway.sendAndReceive(req);
            String responseJson = jsonMapper.toJson(resp);
            RestRouter.sendJson(exchange, 200, responseJson);
        } catch (NumberFormatException e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Path parameter 'key' must be a valid long integer\"}");
        }
    }
}