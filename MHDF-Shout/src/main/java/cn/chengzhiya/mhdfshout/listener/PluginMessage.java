package cn.chengzhiya.mhdfshout.listener;

import cn.chengzhiya.mhdfshout.util.Util;
import cn.chengzhiya.mhdfshoutapi.entity.Shout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.chengzhiya.mhdfshout.util.Util.*;

public final class PluginMessage implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try {
            String subchannel = in.readUTF();
            if (subchannel.equals("MHDFShout")) {
                JSONObject data = JSON.parseObject(in.readUTF());
                System.out.println(data);
                switch (data.getString("action")) {
                    case "sendShout":
                        String shoutType = data.getJSONObject("params").getString("shoutType");
                        Shout shout = new Shout(
                                data.getJSONObject("params").getString("shoutBossBarColor"),
                                data.getJSONObject("params").getString("shoutBossBarBackground"),
                                data.getJSONObject("params").getString("shoutMessage"),
                                data.getJSONObject("params").getString("shoutSound"),
                                data.getJSONObject("params").getInteger("shoutShowTime")
                        );
                        List<Shout> shoutWaitList = Util.getShoutWaitHashMap().get(shoutType) != null ? Util.getShoutWaitHashMap().get(shoutType) : new ArrayList<>();
                        shoutWaitList.add(shout);
                        Util.getShoutWaitHashMap().put(shoutType, shoutWaitList);
                        if (data.getJSONObject("params").getBoolean("shoutWait")) {
                            if (Util.getShoutWaitHashMap().get(shoutType).size() == 1) {
                                startShout(shoutType);
                            }
                        } else {
                            sendShout(shout);
                        }
                        break;
                    case "getDelay":
                        Util.getShoutDelayHashMap().put(data.getJSONObject("params").getString("player"), data.getJSONObject("params").getIntValue("delay"));
                        break;
                }
            }
        } catch (IOException ignored) {
        }
    }
}
