package com.mattmalec.discordmcpurchases.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class JSONBuilder {

    OkHttpClient client = new OkHttpClient();


    public JSONObject request(String url) {
        try {
            String source = client.newCall(new Request.Builder().url(url).header("Content-Type", "application/json").build()).execute().body().string();
            return new JSONObject(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray requestArray(String url) {
        try {
            String source = client.newCall(new Request.Builder().url(url).header("Content-Type", "application/json").build()).execute().body().string();
            return new JSONArray(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject requestDiscord(String url, String auth) {
        try {
            String source = client.newCall(new Request.Builder().url(url).header("Authorization", "Bot " + auth).header("Content-Type", "application/json").build()).execute().body().string();
            return new JSONObject(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void sendDiscord(String url, String auth) {
        try {
            client.newCall(new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), "")).header("Authorization", "Bot " + auth).header("Content-Type", "application/json").build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public JSONArray requestDiscordArray(String url, String auth) {
        try {
            String source = client.newCall(new Request.Builder().url(url).header("Authorization", "Bot " + auth).header("Content-Type", "application/json").build()).execute().body().string();
            return new JSONArray(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
