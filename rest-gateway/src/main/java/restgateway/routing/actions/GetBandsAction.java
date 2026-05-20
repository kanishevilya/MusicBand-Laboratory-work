package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.request.ShowRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.util.RequestIdGenerator;

import java.util.Map;

public class GetBandsAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public GetBandsAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        AbstractResponse response = udpGateway.send(new ShowRequest(RequestIdGenerator.nextId()));

        if (response instanceof ErrorResponse error) {
            RestRouter.sendJson(exchange, 400, jsonMapper.toJson(error));
        } else {
            RestRouter.sendJson(exchange, 200, jsonMapper.toJson(response));
        }
    }
}