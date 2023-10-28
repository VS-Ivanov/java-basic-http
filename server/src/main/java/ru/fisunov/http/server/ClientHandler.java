package ru.fisunov.http.server;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Map<String, MyWebApplication> router;
    private Socket socket;

    private Logger logger;

    public ClientHandler(Map router, Socket socket, Logger logger) {
        this.router = router;
        this.socket = socket;
        this.logger = logger;
    }


    @Override
    public void run() {
        try {
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

            socket.close();
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}
