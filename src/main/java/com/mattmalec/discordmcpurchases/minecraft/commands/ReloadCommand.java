package com.mattmalec.discordmcpurchases.minecraft.commands;

import com.mattmalec.discordmcpurchases.discord.Discord;
import com.mattmalec.discordmcpurchases.minecraft.ScheduledConfirm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ReloadCommand implements CommandExecutor {

    private JavaPlugin plugin;
    private ScheduledConfirm schedule;
    private Discord discord;

    public ReloadCommand(JavaPlugin plugin, ScheduledConfirm schedule, Discord discord) {
        this.plugin = plugin;
        this.schedule = schedule;
        this.discord = discord;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("dmcp")) {
            if(args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "DiscordMCPurchases v1.0 by Matt");
                return false;
            }
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")){
                    String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.begin-reload"));
                    sender.sendMessage(message);
                    File configFile = new File(plugin.getDataFolder(), "config.yml");
                    try {
                        plugin.getConfig().load(configFile);
                    } catch (IOException | InvalidConfigurationException ex) {
                        ex.printStackTrace();
                    }
                    discord.reload();
                    schedule.restart();
                    String message2 = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.finish-reload"));
                    sender.sendMessage(message2);
                }
            }
        }
        return false;
    }
}
