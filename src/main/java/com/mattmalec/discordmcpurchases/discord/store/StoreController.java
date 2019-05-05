package com.mattmalec.discordmcpurchases.discord.store;

import com.mattmalec.discordmcpurchases.discord.store.impl.EntitlementImpl;
import com.mattmalec.discordmcpurchases.discord.store.impl.SKUImpl;
import com.mattmalec.discordmcpurchases.utils.JSONBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoreController {

    private FileConfiguration config;
    private JDA jda;

    public StoreController(FileConfiguration config, JDA jda) {
        this.config = config;
        this.jda = jda;
    }
    public List<Entitlement> getEntitlements(String skuId) {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        List<Entitlement> entitlements = new ArrayList<>();
        JSONArray array = new JSONBuilder().requestDiscordArray(String.format("https://discordapp.com/api/v6/applications/%s/entitlements" +
                "?sku_ids=%s&with_payments=true", applicationId, skuId), botToken);
        for(int i=0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            entitlements.add(new EntitlementImpl(jda, this, json));
        }
        return Collections.unmodifiableList(entitlements);
    }
    public List<Entitlement> getEntitlements() {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        List<Entitlement> entitlements = new ArrayList<>();
        JSONArray array = new JSONBuilder().requestDiscordArray(String.format("https://discordapp.com/api/v6/applications/%s/entitlements" +
                "?with_payments=true", applicationId), botToken);
        for(int i=0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            entitlements.add(new EntitlementImpl(jda, this, json));
        }
        return Collections.unmodifiableList(entitlements);
    }
    public List<Entitlement> getEntitlements(User user) {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        List<Entitlement> entitlements = new ArrayList<>();
        JSONArray array = new JSONBuilder().requestDiscordArray(String.format("https://discordapp.com/api/v6/applications/%s/entitlements" +
                "?user_id=%s&with_payments=true", applicationId, user.getId()), botToken);
        for(int i=0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            entitlements.add(new EntitlementImpl(jda, this, json));
        }
        return Collections.unmodifiableList(entitlements);
    }
    public List<Entitlement> getEntitlements(User user, boolean includeConsumed) {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        List<Entitlement> entitlements = new ArrayList<>();
        if(config.getBoolean("discord.debug")) {
            System.out.println("Application ID: " + applicationId);
            System.out.println("Bot Token: " + botToken);
            System.out.println("User ID: " + user.getId());
        }
        JSONArray array = new JSONBuilder().requestDiscordArray(String.format("https://discordapp.com/api/v6/applications/%s/entitlements" +
                "?user_id=%s&with_payments=true", applicationId, user.getId()), botToken);
        for(int i=0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            if(config.getBoolean("discord.debug")) System.out.println(json);
            boolean consumed = json.getBoolean("consumed");
            if (includeConsumed) {
                entitlements.add(new EntitlementImpl(jda, this, json));
            } else {
                if (!consumed) {
                    entitlements.add(new EntitlementImpl(jda, this, json));
                }
            }
        }
        return Collections.unmodifiableList(entitlements);
    }
    public List<SKU> getSKUs() {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        List<SKU> skus = new ArrayList<>();
        JSONArray array = new JSONBuilder().requestDiscordArray(String.format("https://discordapp.com/api/v6/applications/%s/skus", applicationId), botToken);
        for(int i=0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            skus.add(new SKUImpl(json));
        }
        return Collections.unmodifiableList(skus);
    }
    public SKU getSKU(String skuId) {
        List<SKU> skus = getSKUs();
        for(SKU sku : skus) {
            if(sku.getId() == Long.parseUnsignedLong(skuId)) {
                return sku;
            }
        }
        return null;
    }

    public void consume(Entitlement entitlement) {
        String applicationId = config.getString("discord.application-id");
        String botToken = config.getString("discord.bot-token");
        new JSONBuilder().sendDiscord(String.format("https://discordapp.com/api/v6/applications/%s/entitlements/%s/consume",applicationId, entitlement.getId()), botToken);
    }
    public JDA getJDA() {
        return jda;
    }
}
