package com.mattmalec.discordmcpurchases.discord.store;

import net.dv8tion.jda.core.entities.User;

public interface Entitlement {

    User getUser();
    SKU getSKU();
    long getId();
    boolean isConsumed();

}
