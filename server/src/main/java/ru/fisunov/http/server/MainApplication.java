package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainApplication {
    public static final int PORT = 8189;

    // + К домашнему задания:
    // Добавить логирование!!!
    private static final Logger logger = LogManager.getLogger(MainApplication.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Map<String, MyWebApplication> router = new HashMap<>();
            router.put("/calculator", new CalculatorWebApplication());
            router.put("/greetings", new GreetingsWebApplication());
            //System.out.println("Сервер запущен, порт: " + PORT);
            logger.info("Сервер запущен, порт: " + PORT);
            try (Socket socket = serverSocket.accept()) {
                //System.out.println("Клиент подключился");
                logger.info("Клиент подключился");
                byte[] buffer = new byte[2048];
                int n = socket.getInputStream().read(buffer);
                String rawRequest = new String(buffer, 0, n);
                Request request = new Request(rawRequest);
                //System.out.println("Получен запрос:");
                logger.info("Получен запрос:");
                request.show(logger);
                boolean executed = false;
                for (Map.Entry<String, MyWebApplication> e : router.entrySet()) {
                    if (request.getUri().startsWith(e.getKey())) {
                        e.getValue().execute(request, socket.getOutputStream());
                        executed = true;
                        break;
                    }
                }
                if (!executed) {
                    socket.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
            }
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
