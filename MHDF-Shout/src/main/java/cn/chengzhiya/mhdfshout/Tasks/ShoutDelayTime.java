package cn.chengzhiya.mhdfshout.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class ShoutDelayTime extends BukkitRunnable {
    @Override
    public void run() {
        if (!getShoutDelayTimeHashMap().isEmpty()) {
            for (String PlayerName : getShoutDelayTimeHashMap().keySet()) {
                if (getShoutDelayTimeHashMap().get(PlayerName) <= 0) {
                    getShoutDelayTimeHashMap().remove(PlayerName);
                } else {
                    getShoutDelayTimeHashMap().put(PlayerName, getShoutDelayTimeHashMap().get(PlayerName) - 1);
                }
            }
        }
    }
}
