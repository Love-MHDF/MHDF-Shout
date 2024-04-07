package cn.chengzhiya.mhdfshout.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class AntiChangeItem implements Listener {
    @EventHandler
    public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        if (getShoutHashMap().get(event.getPlayer().getName()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (getShoutHashMap().get(event.getPlayer().getName()) != null) {
            event.setCancelled(true);
        }
    }
}
