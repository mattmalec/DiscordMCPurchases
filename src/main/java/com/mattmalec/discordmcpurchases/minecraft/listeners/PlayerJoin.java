package com.mattmalec.discordmcpurchases.minecraft.listeners;

import com.mattmalec.discordmcpurchases.utils.Caching;
import com.mattmalec.discordmcpurchases.utils.SQLBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoin implements Listener {

    private Caching caching;
    private SQLBuilder sqlBuilder;

    public PlayerJoin(Caching caching, SQLBuilder sqlBuilder) {
        this.caching = caching;
        this.sqlBuilder = sqlBuilder;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        String database = "`" + sqlBuilder.getDatabase() + "`.`players`";
        if(!caching.exists(p)) {
            ResultSet resultSet = sqlBuilder.prepareQuery("SELECT discordId FROM " + database + " WHERE uuid = ?", false, p.getUniqueId().toString());
            try {
                if (resultSet.first()) {
                    caching.addDiscord(p, resultSet.getString("discordId"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
