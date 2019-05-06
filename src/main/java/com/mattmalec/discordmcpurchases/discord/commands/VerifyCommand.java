package com.mattmalec.discordmcpurchases.discord.commands;

import com.mattmalec.discordmcpurchases.discord.entities.IEvent;
import com.mattmalec.discordmcpurchases.utils.Caching;
import com.mattmalec.discordmcpurchases.utils.JSONBuilder;
import com.mattmalec.discordmcpurchases.utils.SQLBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class VerifyCommand implements IEvent {

    private JavaPlugin plugin;
    private SQLBuilder sqlBuilder;
    private Caching caching;
    private Map<UUID, Integer> codeCache;

    public VerifyCommand(JavaPlugin plugin, SQLBuilder sqlBuilder, Caching caching, Map<UUID, Integer> codeCache) {
        this.plugin = plugin;
        this.sqlBuilder = sqlBuilder;
        this.caching = caching;
        this.codeCache = codeCache;
    }

    @Override
    public void execute(Event event) {
        GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

        boolean debug = plugin.getConfig().getBoolean("discord.debug");

        RestAction.setPassContext(debug);

        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();
        if(author.isBot()) {
            return;
        }
        TextChannel verifyChannel = e.getJDA().getTextChannelById(plugin.getConfig().getLong("discord.verification-channel-id"));
        if(channel.getIdLong() != verifyChannel.getIdLong()) {
            return;
        }
        Message message = e.getMessage();
        String content = message.getContentRaw();
        try {
            Integer.parseInt(content);
        } catch (NumberFormatException ex) {
            message.delete().queue();
            return;
        }

        int intContent = Integer.parseInt(content);
        int color = Integer.parseInt(plugin.getConfig().getString("messages.discord.color").replace("#", ""), 16);
        if(plugin.getConfig().getBoolean("discord.debug")) System.out.println(color);

        if(codeCache.containsValue(intContent)) {
            codeCache.forEach((uuid, i) -> {
                if(i == intContent) {
                    JSONArray usernames = new JSONBuilder().requestArray(String.format("https://api.mojang.com/user/profiles/%s/names",
                            uuid.toString().replace("-", "")));
                    int length = usernames.length();
                    String username = usernames.getJSONObject(length-1).getString("name");
                    String description = plugin.getConfig().getString("messages.discord.verify-description");
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setDescription(description)
                            .addField("Player", username, false)
                            .setThumbnail(String.format("https://minotar.net/avatar/%s/500.png", uuid.toString().replace("-", "")))
                            .setColor(color)
                            .setTimestamp(Instant.now())
                            .build();
                    channel.sendMessage(embed).queue();
                    String tag = author.getAsTag();
                    String message1 = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.minecraft.link-success").replace("{tag}", tag));
                    Bukkit.getServer().getPlayer(uuid).sendMessage(message1);
                    codeCache.remove(uuid);
                    String database = "`" + sqlBuilder.getDatabase() + "`.`players`";
                    sqlBuilder.prepareExecute("INSERT INTO " + database + " (uuid, discordId) VALUES (?, ?)", false, uuid.toString(), author.getId());
                    caching.addDiscord(uuid, author);
                }
            });
        }
        message.delete().queue();
    }
}
