package restgateway.routing.actions;

import com.sun.net.httpserver.HttpExchange;
import common.protocol.AbstractResponse;
import common.protocol.request.ScriptSessionBeginRequest;
import common.util.RequestIdGenerator;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.HttpAction;
import restgateway.routing.RestRouter;
import restgateway.routing.util.UriParser;

import java.util.Map;

public class ScriptSessionBeginAction implements HttpAction {
    private final UdpGateway udpGateway;
    private final JsonMapper jsonMapper;

    public ScriptSessionBeginAction(UdpGateway udpGateway,  JsonMapper jsonMapper   )    {
        this.udpGateway = udpGateway;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void execute(HttpExchange exchange, Map<String, String> pathParams) throws Exception {
        Map<String, String> queryParams = UriParser.parseQuery(exchange.getRequestURI().getQuery());
        String token = queryParams.getOrDefault("token", "");

        ScriptSessionBeginRequest req = new ScriptSessionBeginRequest(RequestIdGenerator.nextId(), token);
        AbstractResponse resp = udpGateway.sendAndReceive(req);
        String responseJson = jsonMapper.toJson(resp);
        RestRouter.sendJson(exchange, 200, responseJson);
    }
}