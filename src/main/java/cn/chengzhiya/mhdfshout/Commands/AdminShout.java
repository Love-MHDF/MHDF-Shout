package cn.chengzhiya.mhdfshout.Commands;

import cn.chengzhiya.mhdfshout.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class AdminShout implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(i18n("InputMessage").replaceAll("\\{Exit\\}", Objects.requireNonNull(main.main.getConfig().getString("InputSettings.ExitMessage"))));
            getShoutHashMap().put(player.getName(), "AdminShout");
        } else {
            sender.sendMessage(i18n("OnlyPlayer"));
        }
        return false;
    }
}
