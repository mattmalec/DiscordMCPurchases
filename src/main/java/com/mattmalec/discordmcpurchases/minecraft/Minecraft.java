package com.mattmalec.discordmcpurchases.minecraft;

import com.mattmalec.discordmcpurchases.discord.Discord;
import com.mattmalec.discordmcpurchases.discord.managers.EventManager;
import com.mattmalec.discordmcpurchases.discord.store.StoreController;
import com.mattmalec.discordmcpurchases.minecraft.commands.ConfirmCommand;
import com.mattmalec.discordmcpurchases.minecraft.commands.ReloadCommand;
import com.mattmalec.discordmcpurchases.minecraft.commands.UnlinkCommand;
import com.mattmalec.discordmcpurchases.minecraft.commands.VerifyCommand;
import com.mattmalec.discordmcpurchases.minecraft.listeners.PlayerJoin;
import com.mattmalec.discordmcpurchases.utils.Caching;
import com.mattmalec.discordmcpurchases.utils.SQLBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Minecraft extends JavaPlugin {

    private Map<UUID, Integer> codeCache = new ConcurrentHashMap<>();
    private SQLBuilder sqlBuilder;
    private Discord discord;
    private Caching caching = new Caching();

    @Override
    public void onEnable() {

        ConsoleCommandSender sender = getServer().getConsoleSender();

        if (!getDataFolder().exists()) {
            sender.sendMessage(ChatColor.YELLOW + "Generating a new plugin.getConfig() file");
            getDataFolder().mkdir();
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            sender.sendMessage(ChatColor.GREEN + "plugin.getConfig() created");
        }
        boolean canConnect = handleSQL();
        if(canConnect) {
            sender.sendMessage(ChatColor.GREEN + "Successfully connected to MySQL");
            setupSQL();
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "Could not connect to MySQL. Please check your details in the " + ChatColor.RED + "plugin.getConfig().yml" + ChatColor.DARK_RED + " file");
        }

        EventManager eventManager = new EventManager(codeCache, caching, this, sqlBuilder);
        discord = new Discord(this, eventManager);

        sender.sendMessage(ChatColor.YELLOW + "Starting Discord Bot");
        try {
            discord.start();
        } catch (LoginException | InterruptedException e) {
            sender.sendMessage(ChatColor.RED + "Error starting Discord bot");
        }
        StoreController storeController = new StoreController(this, discord.getJDA());
        sender.sendMessage(ChatColor.GREEN + "Discord bot started");
        sender.sendMessage(ChatColor.YELLOW + "Registering events");

        ScheduledConfirm schedule = new ScheduledConfirm(caching, storeController, this);
        schedule.start();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(caching, sqlBuilder), this);

        sender.sendMessage(ChatColor.GREEN + "Events registered");
        sender.sendMessage(ChatColor.YELLOW + "Registering commands");

        getCommand("verify").setExecutor(new VerifyCommand(discord.getJDA().getTextChannelById(getConfig().getLong("discord.verification-channel-id")), caching, codeCache, this));
        getCommand("unlink").setExecutor(new UnlinkCommand(caching, this, sqlBuilder));
        getCommand("dmcp").setExecutor(new ReloadCommand(this, schedule, discord));
        getCommand("confirm").setExecutor(new ConfirmCommand(caching, storeController, this));

        sender.sendMessage(ChatColor.GREEN + "Commands registered");

        sender.sendMessage(ChatColor.YELLOW + "Initializing cache");
        String database = "`" + sqlBuilder.getDatabase() + "`.`players`";
        ResultSet resultSet = sqlBuilder.prepareQuery("SELECT * FROM " + database, false, null);
        caching.clear();
        try {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String discordId = resultSet.getString("discordId");
                caching.addDiscord(uuid, discordId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        sender.sendMessage(ChatColor.GREEN + "Cache initialized");

        sender.sendMessage(ChatColor.GREEN + "Welcome to whatever the fuck this plugin is");

    }

    @Override
    public void onDisable() {

        discord.shutdown();

    }

    private boolean handleSQL() {

        JavaPlugin plugin = this;

        String host = plugin.getConfig().getString("mysql.host");
        int port = plugin.getConfig().getInt("mysql.port");
        String username = plugin.getConfig().getString("mysql.username");
        String password = plugin.getConfig().getString("mysql.password");
        String database = plugin.getConfig().getString("mysql.database");

        this.sqlBuilder = new SQLBuilder(host, port, username, password, database);

        return sqlBuilder.canConnect();

    }
    private void setupSQL() {
        sqlBuilder.prepareExecute(sqlBuilder.getDatabase(), "CREATE TABLE IF NOT EXISTS players (" +
                "uuid varchar(36) NOT NULL, " +
                "discordId varchar(24) NOT NULL" +
                ");", false, null);

    }

}
