package com.mattmalec.discordmcpurchases.minecraft;

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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScheduledConfirm {

    private Caching caching;
    private StoreController controller;
    private JavaPlugin plugin;
    private BukkitTask task;

    public ScheduledConfirm(Caching caching, StoreController controller, JavaPlugin plugin) {
        this.caching = caching;
        this.controller = controller;
        this.plugin = plugin;

    }

    public void start() {
        if(plugin.getConfig().getInt("minecraft.purchases.check-rate") == -1) {
            return;
        }
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
            for (Player p : players) {
                if (caching.exists(p)) {
                    JDA jda = controller.getJDA();
                    User user = jda.getUserById(caching.getDiscordId(p));
                    List<Entitlement> entitlements = controller.getEntitlements(user, false);
                    int appliedEntitlements = 0;
                    for (Entitlement entitlement : entitlements) {
                        if (plugin.getConfig().getBoolean("discord.debug"))
                            System.out.printf("Name: %s\nID: %s\n", entitlement.getSKU().getName(), entitlement.getSKU().getId());
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
                        if(!commands.isEmpty()) {
                            controller.consume(entitlement);
                            appliedEntitlements++;
                        }
                    }
                    if (appliedEntitlements != 0) {
                        p.sendMessage(ChatColor.GREEN + "You have successfully claimed all your purchases!");
                    }
                }
            }
        }, 0, plugin.getConfig().getInt("minecraft.purchases.check-rate") * 20L);
    }
    private void stop() {
        this.task.cancel();
    }
    public void restart() {
        stop();
        start();
    }
}
