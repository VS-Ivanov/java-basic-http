package ru.fisunov.http.server;

import java.util.List;

public interface ItemStorage {
    public List<Item> load();
}
