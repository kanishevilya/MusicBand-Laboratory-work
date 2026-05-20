package restgateway;

import com.sun.net.httpserver.HttpServer;
import restgateway.config.RouteConfigurator;
import restgateway.json.JacksonMapper;
import restgateway.json.JsonMapper;
import restgateway.network.UdpGateway;
import restgateway.network.UdpGatewayImpl;
import restgateway.routing.RestRouter;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RestApp {
    private static final int HTTP_PORT = 8080;
    private static final String UDP_HOST = "localhost";
    private static final int UDP_PORT = 5555;
    private static final int THREAD_POOL_SIZE = 20;

    public static void main(String[] args) {
        try {
            JsonMapper jsonMapper = new JacksonMapper();
            UdpGateway udpGateway = new UdpGatewayImpl(UDP_HOST, UDP_PORT);
            RestRouter router = new RestRouter();

            RouteConfigurator.configure(router, udpGateway, jsonMapper);

            HttpServer server = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
            server.createContext("/", router);
            server.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));

            logStartupSuccess();
            server.start();

        } catch (Exception e) {
            System.err.println("Критический сбой при запуске HTTP-сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logStartupSuccess() {
        System.out.println("=================================================");
        System.out.println(" 🚀 REST Gateway Engine запущен успешно!");
        System.out.println(" HTTP Порт: " + HTTP_PORT);
        System.out.println(" Проксирование на UDP: " + UDP_HOST + ":" + UDP_PORT);
        System.out.println("=================================================");
    }
}