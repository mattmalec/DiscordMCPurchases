package com.mattmalec.discordmcpurchases.utils;

import net.dv8tion.jda.core.entities.User;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Caching {

    private Map<UUID, String> discordIdMap = new ConcurrentHashMap<>();

    public void addDiscord(Player player, String userId) {
        discordIdMap.put(player.getUniqueId(), userId);
    }
    public void addDiscord(String uuid, String userId) {
        discordIdMap.put(UUID.fromString(uuid), userId);
    }
    public void addDiscord(UUID uuid, User user) {
        discordIdMap.put(uuid, user.getId());
    }
    public String getDiscordId(Player player) {
        return discordIdMap.get(player.getUniqueId());
    }
    public boolean exists(Player player) {
        return discordIdMap.containsKey(player.getUniqueId());
    }
    public void clear() {
        discordIdMap.clear();
    }
}
