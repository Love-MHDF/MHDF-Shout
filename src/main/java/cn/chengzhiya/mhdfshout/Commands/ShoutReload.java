package cn.chengzhiya.mhdfshout.Commands;

import cn.chengzhiya.mhdfshout.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class ShoutReload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        main.main.reloadConfig();
        File LangFile = new File(main.main.getDataFolder(), "lang.yml");
        Lang = YamlConfiguration.loadConfiguration(LangFile);
        sender.sendMessage(i18n("ReloadDone"));
        return false;
    }
}
