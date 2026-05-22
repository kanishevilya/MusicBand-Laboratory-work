package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.InsertRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class InsertAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public InsertAction(UdpGateway udpGateway, JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        try {
            long key = Long.parseLong(pathParams.get("key"));

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                MusicBand band = jsonMapper.fromJson(body, MusicBand.class);

                InsertRequest req = new InsertRequest(RequestIdGenerator.nextId(), key, band);
                AbstractResponse resp = udpGateway.sendAndReceive(req);
                String responseJson = jsonMapper.toJson(resp);
                RestRouter.sendJson(exchange, 201, responseJson);
            }
        } catch (NumberFormatException e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Path parameter 'key' must be a valid long integer\"}");
        } catch (Exception e) {
            RestRouter.sendJson(exchange, 400, "{\"error\": \"Invalid MusicBand JSON payload structure\"}");
        }
    }
}