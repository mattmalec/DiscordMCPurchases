package com.mattmalec.discordmcpurchases.minecraft.commands;

import com.mattmalec.discordmcpurchases.utils.Caching;
import com.mattmalec.discordmcpurchases.utils.SQLBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UnlinkCommand implements CommandExecutor {

    private Caching caching;
    private FileConfiguration config;
    private SQLBuilder sqlBuilder;

    public UnlinkCommand(Caching caching, FileConfiguration config, SQLBuilder sqlBuilder) {
        this.caching = caching;
        this.config = config;
        this.sqlBuilder = sqlBuilder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;
        if (sender instanceof Player) {
            if (command.getName().equalsIgnoreCase("unlink")) {
                p = (Player) sender;
                if (!caching.exists(p)) {
                    String message = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.account-not-linked"));
                    p.sendMessage(message);
                    return false;
                }
                String message = ChatColor.translateAlternateColorCodes('&', config.getString("messages.minecraft.unlink-success"));
                p.sendMessage(message);
                caching.removeDiscord(p);
                String database = "`" + sqlBuilder.getDatabase() + "`.`players`";
                sqlBuilder.prepareExecute("DELETE FROM " + database + " WHERE uuid = ?", false, p.getUniqueId().toString());
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "This command is for in-game players only!");
        }
        return false;
    }
}
