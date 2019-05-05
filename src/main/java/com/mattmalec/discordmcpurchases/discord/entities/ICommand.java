package com.mattmalec.discordmcpurchases.discord.entities;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {

    void execute(String[] args, GuildMessageReceivedEvent e);

}
