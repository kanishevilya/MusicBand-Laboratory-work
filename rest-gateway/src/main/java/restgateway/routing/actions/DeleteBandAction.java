package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.request.RemoveRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.util.RequestIdGenerator;

import java.util.Map;

public class DeleteBandAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public DeleteBandAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        String key = pathParams.get("key");
        if (key == null) throw new IllegalArgumentException("Ключ для удаления не указан");
        long keyLong = Long.parseLong(key);

        AbstractResponse response = udpGateway.send(new RemoveRequest(RequestIdGenerator.nextId(),keyLong));

        if (response instanceof ErrorResponse error) {
            RestRouter.sendJson(exchange, 400, jsonMapper.toJson(error));
        } else {
            RestRouter.sendJson(exchange, 200, jsonMapper.toJson(response));
        }
    }
}