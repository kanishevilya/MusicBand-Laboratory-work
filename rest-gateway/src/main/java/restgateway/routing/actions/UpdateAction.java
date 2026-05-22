package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.UpdateRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UpdateAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public UpdateAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        try {
            long id = Long.parseLong(pathParams.get("id"));

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                MusicBand band = jsonMapper.fromJson(body, MusicBand.class);

                UpdateRequest req = new UpdateRequest(RequestIdGenerator.nextId(), id, band);
                AbstractResponse resp = udpGateway.sendAndReceive(req);
                String responseJson = jsonMapper.toJson(resp);
                RestRouter.sendJson(exchange, 200, responseJson);
            }
        } catch (NumberFormatException e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Path parameter 'id' must be a valid long integer\"}");
        } catch (Exception e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Invalid MusicBand JSON payload structure\"}");
        }
    }
}