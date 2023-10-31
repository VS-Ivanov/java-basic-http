package ru.fisunov.http.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemSqlStorage implements ItemStorage {

    private final static String DB_URL = "jdbc:sqlite::resource:items.db";
    private final static String GET_ITEMS_QUERY = "select Id as id, Title as title from Items order by Id;";

    private Connection dbConn;

    public ItemSqlStorage() {
        try {
            this.dbConn = DriverManager.getConnection(DB_URL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Item> load() {
        List<Item> items = new ArrayList<>();
        try(Statement stmt = dbConn.createStatement()) {
            ResultSet rs = stmt.executeQuery(GET_ITEMS_QUERY);
            while(rs.next()) {
                Item item = new Item(rs.getLong("id"),rs.getString("title"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    protected void finalize() {

        if(dbConn != null) {
            try{
                dbConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
