package com.mattmalec.discordmcpurchases.minecraft.commands;

import com.mattmalec.discordmcpurchases.utils.Caching;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class VerifyCommand implements CommandExecutor {

    private TextChannel textChannel;
    private Caching caching;
    private Map<UUID, Integer> codeCache;
    private JavaPlugin plugin;

    public VerifyCommand(TextChannel textChannel, Caching caching, Map<UUID, Integer> codeCache, JavaPlugin plugin) {
        this.textChannel = textChannel;
        this.caching = caching;
        this.codeCache = codeCache;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;
        if(sender instanceof Player) {
            p = (Player) sender;
            if(command.getName().equalsIgnoreCase("verify")) {
            if(caching.exists(p)) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.account-already-linked"));
                p.sendMessage(message);
                return false;
            }
            int code = generateCode();
                if(codeCache.containsKey(p.getUniqueId())) {
                    String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.verify-pending"));
                    p.sendMessage(message.replace("{code}", Integer.toUnsignedString(codeCache.get(p.getUniqueId())))
                    .replace("{channel}", textChannel.getName()));
                } else {
                    String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.begin-verify"));
                    p.sendMessage(message.replace("{code}", Integer.toUnsignedString(code))
                            .replace("{channel}", textChannel.getName()));
                    codeCache.put(p.getUniqueId(), code);
                }
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "This command is for in-game players only!");
        }
        return false;
    }
    private int generateCode() {
        return new Random().nextInt(900000) + 100000;
    }
}
