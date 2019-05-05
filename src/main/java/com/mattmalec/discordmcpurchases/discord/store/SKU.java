package com.mattmalec.discordmcpurchases.discord.store;

public interface SKU {

    String getName();

    long getPriceTier();

    long getId();
    long getDependantId();

}
