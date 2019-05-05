package com.mattmalec.discordmcpurchases.discord.managers;

import com.mattmalec.discordmcpurchases.discord.entities.ICommand;
import com.mattmalec.discordmcpurchases.discord.entities.IEvent;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandManager implements IEvent {

    private final Map<String, ICommand> commands = new HashMap<>();
    private Map<UUID, Integer> codeCache;

    public CommandManager(Map<UUID, Integer> codeCache) {
        this.codeCache = codeCache;
        initCommandMap();
    }

    private void initCommandMap() {


    }

    @Override
    public void execute(Event event) {
        GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

        String guildPrefix = "?";
        String content = e.getMessage().getContentRaw();
        String[] args = content.split("\\s+");
        User author = e.getAuthor();
        if (!author.isBot()) {
            if (content.startsWith(guildPrefix)) {
                String[] commandAndInput = content.substring(guildPrefix.length()).split("\\s+");
                ICommand command = commands.get(commandAndInput[0]);
                if (command != null) {
                    command.execute(args, e);
                }
            }
        }
    }
}
