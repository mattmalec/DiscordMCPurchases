package com.mattmalec.discordmcpurchases.discord.entities;

import net.dv8tion.jda.core.events.Event;

public interface IEvent {

    void execute(Event event);

}
