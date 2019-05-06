package com.mattmalec.discordmcpurchases.discord;

import com.mattmalec.discordmcpurchases.discord.managers.EventManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class Discord {

    private JavaPlugin plugin;

    private JDA jda;
    private EventManager eventManager;

    public Discord(JavaPlugin plugin, EventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    public void start() throws LoginException, InterruptedException {
        String token = plugin.getConfig().getString("discord.bot-token");
        this.jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(eventManager).build().awaitReady();
    }

    public void reload() {
        this.jda.shutdownNow();
        try {
            start();
        } catch (LoginException | InterruptedException ignored) {}
    }

    public void shutdown() {
        this.jda.shutdownNow();
    }

    public JDA getJDA() {
        return this.jda;
    }
}
