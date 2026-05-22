package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.ScriptSessionEndRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.routing.util.UriParser;

import java.util.Map;

public class ScriptSessionEndAction implements HttpAction {
    private final UdpGateway udpGateway;
    private  final JsonMapper jsonMapper;

    public ScriptSessionEndAction(UdpGateway udpGateway,  JsonMapper jsonMapper) {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        Map<String, String> queryParams = UriParser.parseQuery(exchange.getRequestURI().getQuery());
        String token = queryParams.getOrDefault("token", "");

        ScriptSessionEndRequest req = new ScriptSessionEndRequest(RequestIdGenerator.nextId(), token);
        AbstractResponse resp = udpGateway.sendAndReceive(req);
        String responseJson = jsonMapper.toJson(resp);
        RestRouter.sendJson(exchange, 200, responseJson);
    }
}