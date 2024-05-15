package cn.chengzhiya.mhdfshoutbungeecordhook.listener;

import cn.chengzhiya.mhdfshoutbungeecordhook.main;
import cn.chengzhiya.mhdfshoutbungeecordhook.util.Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class PluginMessage implements Listener {
    public ServerInfo getServerInfo(PluginMessageEvent event) {
        ServerInfo server = null;
        for (Map.Entry one : main.main.getProxy().getServers().entrySet()) {
            if (!(((ServerInfo) one.getValue()).getAddress().getAddress().getHostAddress() + ":" + ((ServerInfo) one.getValue()).getAddress().getPort())
                    .equalsIgnoreCase(
                            event.getSender().getAddress().getAddress().getHostAddress() + ":" + event.getSender().getAddress().getPort()
                    )) {
                continue;
            }
            server = (ServerInfo) one.getValue();
        }
        if (server == null || server.getPlayers().isEmpty()) {
            return null;
        }
        return server;
    }

    public void sendMessage(String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MHDFShout");
        out.writeUTF(data);

        for (Map.Entry one : main.main.getProxy().getServers().entrySet()) {
            ServerInfo Server = (ServerInfo) one.getValue();
            if (!Server.getPlayers().isEmpty()) {
                Server.sendData("BungeeCord", out.toByteArray());
            }
        }
    }

    public void sendMessage(String data, PluginMessageEvent event) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MHDFShout");
        out.writeUTF(data);

        Objects.requireNonNull(getServerInfo(event)).sendData("BungeeCord", out.toByteArray());
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if (!event.getTag().contains("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

        try {
            String subchannel = in.readUTF();
            if (subchannel.equals("MHDFShout")) {
                JSONObject data = JSON.parseObject(in.readUTF());
                switch (data.getString("action")) {
                    case "sendShout": {
                        sendMessage(data.toJSONString());
                        break;
                    }
                    case "setDelay": {
                        Util.getShoutDelayHashMap().put(data.getJSONObject("params").getString("player"), data.getJSONObject("params").getIntValue("delay"));
                        break;
                    }
                    case "getDelay": {
                        String player = data.getJSONObject("params").getString("player");

                        JSONObject sendData = new JSONObject();
                        sendData.put("action", "getDelay");

                        JSONObject params = new JSONObject();
                        params.put("player", player);
                        params.put("delay", Util.getShoutDelayHashMap().get(player) != null ? Util.getShoutDelayHashMap().get(player) : -1);
                        sendData.put("params", params);

                        sendMessage(sendData.toJSONString(), event);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}