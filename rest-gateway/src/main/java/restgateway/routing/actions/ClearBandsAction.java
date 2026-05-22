package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.ClearRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.util.Map;

public class ClearBandsAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;


    public ClearBandsAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        ClearRequest req = new ClearRequest(RequestIdGenerator.nextId());
        AbstractResponse resp = udpGateway.sendAndReceive(req);
        String responseJson = jsonMapper.toJson(resp);
        RestRouter.sendJson(exchange, 200, responseJson);
    }
}