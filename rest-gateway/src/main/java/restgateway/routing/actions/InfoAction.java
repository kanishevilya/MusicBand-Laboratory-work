package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.InfoRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.util.Map;

public class InfoAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public InfoAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        InfoRequest req = new InfoRequest(RequestIdGenerator.nextId());
        AbstractResponse resp = udpGateway.sendAndReceive(req);
        String responseJson = jsonMapper.toJson(resp);
        RestRouter.sendJson(exchange, 200, responseJson);
    }
}