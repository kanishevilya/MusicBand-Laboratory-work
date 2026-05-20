package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.request.GetByIdRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.util.RequestIdGenerator;

import java.util.Map;

public class GetBandByIdAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public GetBandByIdAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        String idStr = pathParams.get("id");
        if (idStr == null) throw new IllegalArgumentException("Параметр id обязателен");
        
        long id = Long.parseLong(idStr);
        long requestId = RequestIdGenerator.nextId();
        AbstractResponse response = udpGateway.send(new GetByIdRequest(requestId, id));

        if (response instanceof ErrorResponse error) {
            RestRouter.sendJson(exchange, 404, jsonMapper.toJson(error));
        } else {
            RestRouter.sendJson(exchange, 200, jsonMapper.toJson(response));
        }
    }
}