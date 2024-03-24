package cn.chengzhiya.mhdfshout;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
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
    @Getter
    public static final HashMap<String, String> ShoutHashMap = new HashMap<>();
    @Getter
    public static final HashMap<String, Integer> ShoutDelayTimeHashMap = new HashMap<>();
    public static final List<Shout> ShoutList = new ArrayList<>();
    public static YamlConfiguration Lang = null;

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

    public static void SendShout(Player player, String BossBarColor, String Message, String Sound, int ShowItem) {
        if (main.main.getConfig().getBoolean("ShoutSettings.Color")) {
            Message = ChatColor(Message);
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SendShout");
        out.writeUTF(BossBarColor);
        out.writeUTF(Message);
        out.writeUTF(Sound);
        out.writeInt(ShowItem);

        player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
    }

    public static void SendAdminShout(Player player, String BossBarColor, String Message, String Sound, int ShowItem) {
        Message = ChatColor(Message);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SendAdminShout");
        out.writeUTF(BossBarColor);
        out.writeUTF(Message);
        out.writeUTF(Sound);
        out.writeInt(ShowItem);

        player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
    }

    public static void setDelayTime(Player player, int DelayTime) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetDelayTime");
        out.writeUTF(player.getName());
        out.writeInt(DelayTime);

        player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
        getShoutDelayTimeHashMap().put(player.getName(), DelayTime);
    }

    public static void getDelayTime(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetDelayTime");
        out.writeUTF(player.getName());

        player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
    }
}
