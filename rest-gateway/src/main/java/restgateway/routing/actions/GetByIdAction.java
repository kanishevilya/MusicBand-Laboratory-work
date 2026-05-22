package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByIdRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.util.Map;

public class GetByIdAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public GetByIdAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        try {
            long id = Long.parseLong(pathParams.get("id"));
            GetByIdRequest req = new GetByIdRequest(RequestIdGenerator.nextId(), id);
            AbstractResponse resp = udpGateway.sendAndReceive(req);
            String responseJson = jsonMapper.toJson(resp);
            RestRouter.sendJson(exchange, 200, responseJson);
        } catch (NumberFormatException e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Path parameter 'id' must be a valid long integer\"}");
        }
    }
}