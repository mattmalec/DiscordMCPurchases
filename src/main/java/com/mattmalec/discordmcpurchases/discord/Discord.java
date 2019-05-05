package com.mattmalec.discordmcpurchases.discord;

import com.mattmalec.discordmcpurchases.discord.managers.EventManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;

public class Discord {

    private FileConfiguration config;

    private JDA jda;
    private EventManager eventManager;

    public Discord(FileConfiguration config, EventManager eventManager) {
        this.config = config;
        this.eventManager = eventManager;
    }

    public void start() throws LoginException, InterruptedException {
        String token = config.getString("discord.bot-token");
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
