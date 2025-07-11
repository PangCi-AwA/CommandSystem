package org.command.system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CommandArgs {

    private final Map<String, Object> dataMap = new HashMap<>();
    private static final Map<String, Function<String, Object>> customDeserializeMap = new HashMap<>();

    public static void addCustomDeserialize(String type, Function<String, Object> function) {
        customDeserializeMap.put(type, function);
    }

    public Set<String> keySets() {
        return new HashSet<>(dataMap.keySet());
    }

    public void putString(String key, String value) {
        this.dataMap.put(key, value);
    }

    public void putInt(String key, int value) {
        this.dataMap.put(key, value);
    }

    public void putDouble(String key, double value) {
        this.dataMap.put(key, value);
    }

    public void putBool(String key, boolean value) {
        this.dataMap.put(key, value);
    }

    public void putPlayer(String key, String playerName) {
        this.dataMap.put(key, Bukkit.getPlayer(playerName));
    }

    @Nullable
    public <T> T getT(@NotNull String key, @NotNull Class<T> t) {
        Object obj = this.dataMap.getOrDefault(key, null);
        if (!t.isInstance(obj)) return null;
        return t.cast(obj);
    }

    @Nullable
    public String getString(String key) {
        return getT(key, String.class);
    }

    @Nullable
    public Integer getInt(String key) {
        return getT(key, Integer.class);
    }

    @Nullable
    public Double getDouble(String key) {
        return getT(key, Double.class);
    }

    @Nullable
    public Boolean getBool(String key) {
        return getT(key, Boolean.class);
    }

    @Nullable
    public Player getPlayer(String key) {
        return getT(key, Player.class);
    }


    public void put(String type, String key, String value) {
        switch (type.toLowerCase()) {
            case "str" -> putString(key, value);
            case "int" -> putInt(key, Integer.parseInt(value));
            case "double" -> putDouble(key, Double.parseDouble(value));
            case "bool" -> putBool(key, Boolean.parseBoolean(value));
            case "player" -> putPlayer(key, value);
            default -> {
                if (customDeserializeMap.containsKey(type)) {
                    this.dataMap.put(key, customDeserializeMap.get(type).apply(value));
                }
            }
        }
    }

}
