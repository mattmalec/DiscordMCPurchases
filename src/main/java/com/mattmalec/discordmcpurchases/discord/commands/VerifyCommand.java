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
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONArray;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class VerifyCommand implements IEvent {

    private FileConfiguration config;
    private SQLBuilder sqlBuilder;
    private Caching caching;
    private Map<UUID, Integer> codeCache;

    public VerifyCommand(FileConfiguration config, SQLBuilder sqlBuilder, Caching caching, Map<UUID, Integer> codeCache) {
        this.config = config;
        this.sqlBuilder = sqlBuilder;
        this.caching = caching;
        this.codeCache = codeCache;
    }

    @Override
    public void execute(Event event) {
        GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

        boolean debug = config.getBoolean("discord.debug");

        RestAction.setPassContext(debug);

        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();
        if(author.isBot()) {
            return;
        }
        TextChannel verifyChannel = e.getJDA().getTextChannelById(config.getLong("discord.verification-channel-id"));
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

        if(codeCache.containsValue(intContent)) {
            codeCache.forEach((uuid, i) -> {
                if(i == intContent) {
                    JSONArray usernames = new JSONBuilder().requestArray(String.format("https://api.mojang.com/user/profiles/%s/names",
                            uuid.toString().replace("-", "")));
                    int length = usernames.length();
                    String username = usernames.getJSONObject(length-1).getString("name");
                    String description = config.getString("messages.discord.verify-description");
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setDescription(description)
                            .addField("Player", username, false)
                            .setThumbnail(String.format("https://minotar.net/avatar/%s/500.png", uuid.toString().replace("-", "")))
                            .setColor(Integer.parseInt(config.getString("messages.discord.color").replace("#", ""), 16))
                            .setTimestamp(Instant.now())
                            .build();
                    channel.sendMessage(embed).queue();
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
