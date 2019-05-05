package com.mattmalec.discordmcpurchases.discord.store.impl;

import com.mattmalec.discordmcpurchases.discord.store.Entitlement;
import com.mattmalec.discordmcpurchases.discord.store.SKU;
import com.mattmalec.discordmcpurchases.discord.store.StoreController;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

public class EntitlementImpl implements Entitlement {

    private JDA jda;
    private StoreController controller;
    private JSONObject json;

    public EntitlementImpl(JDA jda, StoreController controller, JSONObject json) {
        this.jda = jda;
        this.controller = controller;
        this.json = json;
    }

    @Override
    public User getUser() {
        return jda.getUserById(json.getLong("user_id"));
    }

    @Override
    public SKU getSKU() {
        return controller.getSKU(json.getString("sku_id"));
    }

    @Override
    public boolean isConsumed() {
        return json.getBoolean("consumed");
    }

    @Override
    public long getId() {
        return json.getLong("id");
    }
}
