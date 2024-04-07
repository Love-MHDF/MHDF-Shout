package cn.chengzhiya.mhdfshoutbungeecordhook;

import cn.chengzhiya.mhdfshoutbungeecordhook.Listeners.PluginMessage;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class main extends Plugin {
    public static main main;
    public static HashMap<String, Integer> ShoutDelayTimeHashMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;
        getProxy().getPluginManager().registerListener(this, new PluginMessage());
        getProxy().registerChannel("BungeeCord");
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (!ShoutDelayTimeHashMap.isEmpty()) {
                    for (String PlayerName : ShoutDelayTimeHashMap.keySet()) {
                        if (ShoutDelayTimeHashMap.get(PlayerName) == 0) {
                            ShoutDelayTimeHashMap.remove(PlayerName);
                        } else {
                            ShoutDelayTimeHashMap.put(PlayerName, ShoutDelayTimeHashMap.get(PlayerName) - 1);
                        }
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;
    }
}
