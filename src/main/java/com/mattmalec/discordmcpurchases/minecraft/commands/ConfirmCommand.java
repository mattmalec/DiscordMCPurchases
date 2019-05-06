package com.mattmalec.discordmcpurchases.minecraft.commands;

import com.mattmalec.discordmcpurchases.discord.store.Entitlement;
import com.mattmalec.discordmcpurchases.discord.store.StoreController;
import com.mattmalec.discordmcpurchases.utils.Caching;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfirmCommand implements CommandExecutor {

    private Caching caching;
    private StoreController controller;
    private JavaPlugin plugin;

    public ConfirmCommand(Caching caching, StoreController controller, JavaPlugin plugin) {
        this.caching = caching;
        this.controller = controller;
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (command.getName().equalsIgnoreCase("confirm")) {
                if (caching.exists(p)) {
                    JDA jda = controller.getJDA();
                    User user = jda.getUserById(caching.getDiscordId(p));
                    List<Entitlement> entitlements = controller.getEntitlements(user, false);
                    for (Entitlement entitlement : entitlements) {
                        if (plugin.getConfig().getBoolean("discord.debug")) System.out.printf("Name: %s\nID: %s\n", entitlement.getSKU().getName(), entitlement.getSKU().getId());
                        List<String> commands = plugin.getConfig().getStringList(String.format("minecraft.purchases.%s.commands", entitlement.getSKU().getId()));
                        List<Long> applyRolesIds = plugin.getConfig().getLongList(String.format("minecraft.purchases.%s.apply-roles", entitlement.getSKU().getId()));
                        List<Long> removeRolesIds = plugin.getConfig().getLongList(String.format("minecraft.purchases.%s.remove-roles", entitlement.getSKU().getId()));
                        TextChannel tc = jda.getTextChannelById(plugin.getConfig().getLong("discord.verification-channel-id"));
                        Guild guild = tc.getGuild();
                        if (!applyRolesIds.isEmpty()) {
                            List<Role> applyRoles = new ArrayList<>();
                            for (Long l : applyRolesIds) applyRoles.add(jda.getRoleById(l));
                            guild.getController().addRolesToMember(guild.getMember(user), applyRoles).queue();
                        }
                        if (!removeRolesIds.isEmpty()) {
                            List<Role> removeRoles = new ArrayList<>();
                            for (Long l : removeRolesIds) removeRoles.add(jda.getRoleById(l));
                            guild.getController().removeRolesFromMember(guild.getMember(user), removeRoles).queue();
                        }
                        commands.forEach(s -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s
                                .replace("{uuid}", p.getUniqueId().toString())
                                .replace("{name}", p.getName())));
                        controller.consume(entitlement);
                    }
                    if (!entitlements.isEmpty()) {
                        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.purchase-successful"));
                        p.sendMessage(message);
                    } else {
                        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.no-purchases"));
                        p.sendMessage(message);
                    }
                } else {
                    String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.account-not-linked"));
                    p.sendMessage(message);
                }
            }
        }
        return false;
    }
}
