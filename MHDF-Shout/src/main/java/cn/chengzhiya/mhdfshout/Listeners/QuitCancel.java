package cn.chengzhiya.mhdfshout.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class QuitCancel implements Listener {
    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        if (getShoutHashMap().get(event.getPlayer().getName()) != null) {
            getShoutHashMap().remove(event.getPlayer().getName());
        }
    }
}
