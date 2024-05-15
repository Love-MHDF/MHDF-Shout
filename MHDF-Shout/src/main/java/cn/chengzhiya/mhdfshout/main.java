package cn.chengzhiya.mhdfshout;

import cn.chengzhiya.mhdfshout.command.ShoutReload;
import cn.chengzhiya.mhdfshout.listener.PluginMessage;
import cn.chengzhiya.mhdfshout.task.TakeShoutDelay;
import cn.chengzhiya.mhdfshout.util.Util;
import cn.chengzhiya.mhdfshoutapi.entity.Shout;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static cn.chengzhiya.mhdfpluginapi.Util.*;
import static cn.chengzhiya.mhdfpluginapi.YamlFileUtil.*;
import static cn.chengzhiya.mhdfshout.util.Util.*;

public final class main extends JavaPlugin {
    public static main main;
    public static String Version = "1.0.1";

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        File PluginHome = getDataFolder();
        File ConfigFile = new File(PluginHome, "config.yml");
        File LangFile = new File(PluginHome, "lang.yml");

        if (!PluginHome.exists()) {
            boolean Stats = PluginHome.mkdirs();
            if (!Stats) {
                ColorLog("&c[MHDF-PluginAPI]插件数据文件夹创建失败!");
            }
        }
        if (!ConfigFile.exists()) {
            SaveResource(this.getDataFolder().getPath(), "config.yml", "config.yml", true);
        }
        if (!LangFile.exists()) {
            SaveResource(this.getDataFolder().getPath(), "lang.yml", "lang.yml", true);
        }

        registerCommand(this, new ShoutReload(), "重载配置", "shoutreload");

        for (String shouts : Objects.requireNonNull(getConfig().getConfigurationSection("HornSettings")).getKeys(false)) {
            CommandExecutor commandExecutor = (sender, command, label, args) -> {
                if (sender.hasPermission(Objects.requireNonNull(getConfig().getString("HornSettings." + shouts + ".Permission")))) {
                    if (Util.getShoutDelayHashMap().get(sender.getName()) != null && !sender.hasPermission("MHDFShout.Bypass.Delay")) {
                        sender.sendMessage(i18n("Delay").replaceAll("\\{Delay}", String.valueOf(Util.getShoutDelayHashMap().get(sender.getName()))));
                        return false;
                    }
                    if (args.length != 0) {
                        StringBuilder shoutMessageBuilder = new StringBuilder();

                        for (String messages : args) {
                            shoutMessageBuilder.append(messages);
                            if (!messages.equals(args[args.length - 1])) {
                                shoutMessageBuilder.append(" ");
                            }
                        }

                        if (getConfig().getInt("HornSettings." + shouts + ".MaxLength") != -1 &&
                                shoutMessageBuilder.toString().length() > getConfig().getInt("HornSettings." + shouts + ".MaxLength") &&
                                !sender.hasPermission("MHDFShout.Bypass.Length")) {
                            sender.sendMessage(i18n("OutLength").replaceAll("\\{Length}", String.valueOf(getConfig().getInt("HornSettings." + shouts + ".MaxLength"))));
                            return false;
                        }

                        if (!sender.hasPermission("MHDFShout.Bypass.BlackWord")) {
                            for (String blackWord : getConfig().getStringList("ShoutSettings.BlackWordList")) {
                                if (shoutMessageBuilder.toString().contains(blackWord)) {
                                    sender.sendMessage(i18n("BlackWord"));
                                    return false;
                                }
                            }
                        }

                        String shoutMessage = ChatColor(PlaceholderAPI.setPlaceholders(
                                sender instanceof Player ? (Player) sender : null,
                                Objects.requireNonNull(getConfig().getString("HornSettings." + shouts + ".MessageFormat"))
                        )).replaceAll("\\{Message}", getConfig().getBoolean("HornSettings." + shouts + ".Color") ? ChatColor(shoutMessageBuilder.toString()) : shoutMessageBuilder.toString());

                        Shout shout = new Shout(
                                getConfig().getString("HornSettings." + shouts + ".BossBarColor"),
                                getConfig().getString("HornSettings." + shouts + ".NullBossBarMessage"),
                                shoutMessage,
                                Objects.requireNonNull(getConfig().getString("HornSettings." + shouts + ".Sound")),
                                getConfig().getInt("HornSettings." + shouts + ".ShowTime")
                        );

                        setDelay(sender.toString(), getConfig().getInt("ShoutSettings.BlackWordList"));

                        if (getConfig().getBoolean("HornSettings." + shouts + ".Wait") && Util.getShoutWaitHashMap().get(shouts) != null && !Util.getShoutWaitHashMap().get(shouts).isEmpty()) {
                            sender.sendMessage(i18n("DoneInQueue").replaceAll("\\{Size}", String.valueOf(Util.getShoutWaitHashMap().get(shouts).size())));
                        } else {
                            sender.sendMessage(i18n("Done"));
                        }
                        sendShout(shouts, getConfig().getBoolean("HornSettings." + shouts + ".Wait"), shout);
                    } else {
                        sender.sendMessage(i18n("UsageError").replaceAll("\\{Command}", label));
                    }
                } else {
                    sender.sendMessage(i18n("NotPermission"));
                }
                return false;
            };

            for (String commands : getConfig().getStringList("HornSettings." + shouts + ".Commands")) {
                registerCommand(this, commandExecutor, shouts, commands);
            }
        }

        new TakeShoutDelay().runTaskTimerAsynchronously(this, 0L, 20L);

        ColorLog("&f[MHDF-Shout] &d  __  __ _    _ _____  ______    _____ _                 _   ");
        ColorLog("&f[MHDF-Shout] &d |  \\/  | |  | |  __ \\|  ____|  / ____| |               | |  ");
        ColorLog("&f[MHDF-Shout] &d | \\  / | |__| | |  | | |__    | (___ | |__   ___  _   _| |_ ");
        ColorLog("&f[MHDF-Shout] &d | |\\/| |  __  | |  | |  __|    \\___ \\| '_ \\ / _ \\| | | | __|");
        ColorLog("&f[MHDF-Shout] &d | |  | | |  | | |__| | |       ____) | | | | (_) | |_| | |_ ");
        ColorLog("&f[MHDF-Shout] &d |_|  |_|_|  |_|_____/|_|      |_____/|_| |_|\\___/ \\__,_|\\__|");
        ColorLog("&f[MHDF-Shout]");
        if (getConfig().getBoolean("BungeeCordMode")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage());
            ColorLog("&f[MHDF-Shout] &a已连接BC!");

        }
        ColorLog("&f[MHDF-Shout] &a插件加载完成!");
        ColorLog("&f[MHDF-Shout] &a欢迎使用梦东系列插件 交流群号:129139830");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;

        ColorLog("&f[MHDF-Shout] &d  __  __ _    _ _____  ______    _____ _                 _   ");
        ColorLog("&f[MHDF-Shout] &d |  \\/  | |  | |  __ \\|  ____|  / ____| |               | |  ");
        ColorLog("&f[MHDF-Shout] &d | \\  / | |__| | |  | | |__    | (___ | |__   ___  _   _| |_ ");
        ColorLog("&f[MHDF-Shout] &d | |\\/| |  __  | |  | |  __|    \\___ \\| '_ \\ / _ \\| | | | __|");
        ColorLog("&f[MHDF-Shout] &d | |  | | |  | | |__| | |       ____) | | | | (_) | |_| | |_ ");
        ColorLog("&f[MHDF-Shout] &d |_|  |_|_|  |_|_____/|_|      |_____/|_| |_|\\___/ \\__,_|\\__|");
        ColorLog("&f[MHDF-Shout]");
        ColorLog("&f[MHDF-Shout] &9插件已卸载! 感谢您再次支持!");
        ColorLog("&f[MHDF-Shout] &9梦东系列插件 交流群号:129139830");
    }
}
