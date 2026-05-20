package restgateway.config;

import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.routing.RestRouter;
import restgateway.routing.actions.*;

/**
 * Класс отвечает ИСКЛЮЧИТЕЛЬНО за маппинг URL на бизнес-логику (Actions).
 * Сюда мы будем дописывать новые эндпоинты.
 */
public class RouteConfigurator {

    public static void configure(RestRouter router, UdpGateway udpGateway, JsonMapper jsonMapper) {
        router.addRoute("GET",    "/api/bands",      new GetBandsAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/{id}", new GetBandByIdAction(udpGateway, jsonMapper));
        router.addRoute("POST",   "/api/bands",      new CreateBandAction(udpGateway, jsonMapper));
        router.addRoute("DELETE", "/api/bands/{key}", new DeleteBandAction(udpGateway, jsonMapper));
    }
}