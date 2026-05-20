package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.model.MusicBand;
import common.protocol.request.InsertRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.util.RequestIdGenerator;

import java.util.Map;

public class CreateBandAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public CreateBandAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        String key = pathParams.get("key");
        if (key == null) throw new IllegalArgumentException("Параметр key обязателен");
        long keyL = Long.parseLong(key);

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        
        MusicBand band = jsonMapper.fromJson(bodyBytes, MusicBand.class);
        
        if (band == null) throw new IllegalArgumentException("Некорректное тело запроса JSON");

        long requestId = RequestIdGenerator.nextId();
        InsertRequest insertRequest = new InsertRequest(requestId, keyL, band);
        AbstractResponse response = udpGateway.send(insertRequest);

        if (response instanceof ErrorResponse error) {
            RestRouter.sendJson(exchange, 422, jsonMapper.toJson(error));
        } else {
            RestRouter.sendJson(exchange, 201, jsonMapper.toJson(response));
        }
    }
}