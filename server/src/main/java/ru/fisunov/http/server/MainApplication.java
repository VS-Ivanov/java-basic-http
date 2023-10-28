package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainApplication {
    public static final int PORT = 8189;

    private static final int POOL_SIZE = 10;

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

            //добавляем пул потоков
            ExecutorService serv = Executors.newFixedThreadPool(POOL_SIZE);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(router, socket, logger);
                    serv.execute(handler);

                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

        } catch(IOException e){
                //e.printStackTrace();
                logger.error(e.getMessage());
            }
    }
}
