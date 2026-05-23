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
        router.addRoute("GET",    "/api/help",                     new HelpAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/info",               new InfoAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands",                    new ShowAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/{id}",               new GetByIdAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/key/{key}",          new GetByKeyAction(udpGateway, jsonMapper));

        router.addRoute("GET",    "/api/bands/albums/average",     new AverageAlbumsAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/albums/descending",  new DescendingAlbumsAction(udpGateway, jsonMapper));
        router.addRoute("GET",    "/api/bands/albums/filter",      new FilterByAlbumsCountAction(udpGateway, jsonMapper));

        router.addRoute("POST",   "/api/bands/key/{key}",          new InsertAction(udpGateway, jsonMapper));
        router.addRoute("PUT",    "/api/bands/{id}",               new UpdateAction(udpGateway, jsonMapper));
        router.addRoute("POST",   "/api/bands/remove-greater",     new RemoveGreaterAction(udpGateway, jsonMapper));
        router.addRoute("PUT",    "/api/bands/key/{key}/replace-greater", new ReplaceGreaterAction(udpGateway, jsonMapper));
        router.addRoute("PUT",    "/api/bands/key/{key}/replace-lower",   new ReplaceLowerAction(udpGateway, jsonMapper));

        router.addRoute("DELETE", "/api/bands",                    new ClearBandsAction(udpGateway, jsonMapper));
        router.addRoute("DELETE", "/api/bands/key/{key}",          new RemoveAction(udpGateway, jsonMapper));

        router.addRoute("POST",   "/api/script/session/begin",     new ScriptSessionBeginAction(udpGateway, jsonMapper));
        router.addRoute("POST",   "/api/script/session/end",       new ScriptSessionEndAction(udpGateway, jsonMapper));
    }
}