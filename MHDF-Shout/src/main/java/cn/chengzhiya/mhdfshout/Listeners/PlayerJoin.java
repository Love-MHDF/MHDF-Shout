package cn.chengzhiya.mhdfshout.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class PlayerJoin implements Listener {
    @EventHandler
    public void OnEvent(PlayerJoinEvent event) {
        getDelayTime(event.getPlayer());
    }
}
