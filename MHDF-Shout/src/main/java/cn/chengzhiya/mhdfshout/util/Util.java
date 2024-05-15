package cn.chengzhiya.mhdfshout.util;

import cn.chengzhiya.mhdfshout.main;
import cn.chengzhiya.mhdfshoutapi.entity.Shout;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static cn.chengzhiya.mhdfpluginapi.Util.*;

public final class Util {
    public static YamlConfiguration Lang = null;
    @Getter
    public static HashMap<String, List<Shout>> shoutWaitHashMap = new HashMap<>();
    @Getter
    public static HashMap<String, Integer> shoutDelayHashMap = new HashMap<>();

    public static void registerCommand(Plugin plugin, CommandExecutor commandExecutor, String description, String... aliases) {
        PluginCommand command = getCommand(aliases[0], plugin);
        command.setAliases(Arrays.asList(aliases));
        command.setDescription(description);
        getCommandMap().register(plugin.getDescription().getName(), command);
        command.setExecutor(commandExecutor);
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (Exception ignored) {
        }
        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (Exception ignored) {
        }
        return commandMap;
    }

    public static String i18n(String Value) {
        if (Lang == null) {
            File LangFile = new File(main.main.getDataFolder(), "lang.yml");
            Lang = YamlConfiguration.loadConfiguration(LangFile);
        }
        return ChatColor(Lang.getString(Value));
    }

    public static void sendShout(String shoutType, boolean wait, Shout shout) {
        if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
            List<Shout> shoutWaitList = getShoutWaitHashMap().get(shoutType) != null ? getShoutWaitHashMap().get(shoutType) : new ArrayList<>();
            shoutWaitList.add(shout);
            getShoutWaitHashMap().put(shoutType, shoutWaitList);

            if (wait) {
                if (Util.getShoutWaitHashMap().get(shoutType).size() == 1) {
                    startShout(shoutType);
                }
            } else {
                sendShout(shout);
            }
        } else {
            JSONObject data = new JSONObject();
            data.put("action", "sendShout");

            JSONObject params = new JSONObject();
            params.put("shoutType", shoutType);
            params.put("shoutWait", wait);
            params.put("shoutBossBarColor", shout.getBossBarColor());
            params.put("shoutBossBarBackground", shout.getBossBarBackground());
            params.put("shoutMessage", shout.getMessage());
            params.put("shoutSound", shout.getSound());
            params.put("shoutShowTime", shout.getShowTime());
            data.put("params", params);

            sendPluginMessage(data.toJSONString());
        }
    }

    public static void sendShout(Shout shout) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            BossBar NullBossBar = BossBar.bossBar(Component.text(shout.getBossBarBackground()), 1f, BossBar.Color.valueOf(shout.getBossBarColor()), BossBar.Overlay.PROGRESS);
            BossBar ShoutBossBar = BossBar.bossBar(Component.text(shout.getMessage()), 1f, BossBar.Color.valueOf(shout.getBossBarColor()), BossBar.Overlay.PROGRESS);

            onlinePlayer.showBossBar(NullBossBar);
            onlinePlayer.showBossBar(ShoutBossBar);

            String[] sound = shout.getSound().split("\\|");
            try {
                onlinePlayer.playSound(onlinePlayer, org.bukkit.Sound.valueOf(sound[0]), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            } catch (Exception e) {
                onlinePlayer.playSound(onlinePlayer, sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(main.main, () -> {
                onlinePlayer.hideBossBar(NullBossBar);
                onlinePlayer.hideBossBar(ShoutBossBar);
            }, 20L * shout.getShowTime());
        }
    }

    public static void startShout(String shoutType) {
        List<Shout> shoutWaitList = getShoutWaitHashMap().get(shoutType);

        Shout shout = getShoutWaitHashMap().get(shoutType).get(0);

        sendShout(shout);

        Bukkit.getScheduler().runTaskLaterAsynchronously(main.main, () -> {
            shoutWaitList.remove(shout);
            getShoutWaitHashMap().put(shoutType, shoutWaitList);

            if (!shoutWaitList.isEmpty()) {
                startShout(shoutType);
            }
        }, 20L * shout.getShowTime());
    }

    public static void updateDelay(String player) {
        if (main.main.getConfig().getBoolean("BungeeCordMode")) {
            JSONObject data = new JSONObject();
            data.put("action", "getDelay");

            JSONObject params = new JSONObject();
            params.put("player", player);
            data.put("params", params);

            sendPluginMessage(data.toJSONString());
        }
    }

    public static void setDelay(String player, int delay) {
        getShoutDelayHashMap().put(player, delay);
        if (main.main.getConfig().getBoolean("BungeeCordMode")) {
            JSONObject data = new JSONObject();
            data.put("action", "setDelay");

            JSONObject params = new JSONObject();
            params.put("player", player);
            params.put("delay", delay);
            data.put("params", params);

            sendPluginMessage(data.toJSONString());
        }
    }

    public static void sendPluginMessage(String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MHDFShout");
        out.writeUTF(data);

        System.out.println(data);
        Bukkit.getServer().sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
    }
}
