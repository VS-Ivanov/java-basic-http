package ru.fisunov.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ItemsWebApplication implements MyWebApplication {
    private String name;
    private List<Item> items;

    public ItemsWebApplication() {
        this.name = "Items Web Application";

        //подключаем хранилище
        ItemStorage db = new ItemSqlStorage();
        this.items = db.load();
    }

    @Override
    public void execute(Request request, OutputStream output) throws IOException {

        Gson gs = new GsonBuilder().create();
        String json = gs.toJson(items);

        output.write(("" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" +
                json
        ).getBytes(StandardCharsets.UTF_8));
    }

}
