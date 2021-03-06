package com.mattmalec.discordmcpurchases.discord.managers;

import com.mattmalec.discordmcpurchases.discord.commands.VerifyCommand;
import com.mattmalec.discordmcpurchases.discord.entities.IEvent;
import com.mattmalec.discordmcpurchases.utils.Caching;
import com.mattmalec.discordmcpurchases.utils.SQLBuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EventManager implements EventListener {

    private final Map<Class<? extends Event>, List<IEvent>> events = new HashMap<>();
    private Map<UUID, Integer> codeCache;
    private Caching caching;
    private JavaPlugin plugin;
    private SQLBuilder sqlBuilder;

    public EventManager(Map<UUID, Integer> codeCache, Caching caching, JavaPlugin plugin, SQLBuilder sqlBuilder) {
        this.codeCache = codeCache;
        this.caching = caching;
        this.plugin = plugin;
        this.sqlBuilder = sqlBuilder;
        initEventMap();
    }

    private void initEventMap() {
        events.put(GuildMessageReceivedEvent.class, Arrays.asList(new CommandManager(codeCache), new VerifyCommand(plugin, sqlBuilder, caching, codeCache)));
    }

    @Override
    public void onEvent(Event e) {
        List<IEvent> eventList = events.get(e.getClass());
        if(eventList != null) {
            eventList.forEach(ev -> {
                if (ev != null) ev.execute(e);
            });
        }
    }

}
