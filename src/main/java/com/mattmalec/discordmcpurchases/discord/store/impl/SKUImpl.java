package com.mattmalec.discordmcpurchases.discord.store.impl;

import com.mattmalec.discordmcpurchases.discord.store.SKU;
import org.json.JSONObject;

public class SKUImpl implements SKU {

    private JSONObject json;

    public SKUImpl(JSONObject json) {
        this.json = json;
    }

    @Override
    public String getName() {
        return json.getString("name");
    }

    @Override
    public long getPriceTier() {
        return json.getLong("price_tier");
    }

    @Override
    public long getId() {
        return json.getLong("id");
    }

    @Override
    public long getDependantId() {
        return json.optLong("dependant_sku_id");
    }
}
