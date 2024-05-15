package cn.chengzhiya.mhdfshout.task;

import cn.chengzhiya.mhdfshout.main;
import cn.chengzhiya.mhdfshout.util.Util;
import org.bukkit.scheduler.BukkitRunnable;

import static cn.chengzhiya.mhdfshout.util.Util.*;

public final class TakeShoutDelay extends BukkitRunnable {
    @Override
    public void run() {
        for (String player : Util.getShoutDelayHashMap().keySet()) {
            if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
                if (Util.getShoutDelayHashMap().get(player) > 0) {
                    Util.getShoutDelayHashMap().put(player, Util.getShoutDelayHashMap().get(player) - 1);
                } else {
                    Util.getShoutDelayHashMap().remove(player);
                }
            } else {
                updateDelay(player);
            }
        }
    }
}
