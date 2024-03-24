package cn.chengzhiya.mhdfshout.Listeners;

import cn.chengzhiya.mhdfshout.main;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class UseShout implements Listener {
    @EventHandler
    public void OnEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
                ReadWriteNBT ItemNBT = NBT.itemStackToNBT(event.getPlayer().getInventory().getItemInMainHand());
                if (ItemNBT.getCompound("tag") != null) {
                    if (ItemNBT.getCompound("tag").getString("MYTHIC_TYPE") != null) {
                        String MythicType = ItemNBT.getCompound("tag").getString("MYTHIC_TYPE");
                        if (main.main.getConfig().getString("HornSettings." + MythicType + ".MessageFormat") != null) {
                            Player player = event.getPlayer();
                            if (main.main.getConfig().getInt("HornSettings." + MythicType + ".Delay") >= 0 && !player.hasPermission("MHDFShout.Bypass.Delay")) {
                                getDelayTime(player);
                                if (getShoutDelayTimeHashMap().get(player.getName()) != null) {
                                    player.sendMessage(i18n("Delay").replaceAll("\\{Delay\\}", String.valueOf(getShoutDelayTimeHashMap().get(player.getName()))));
                                    return;
                                }
                                setDelayTime(player, main.main.getConfig().getInt("HornSettings." + MythicType + ".Delay"));
                            }
                            player.sendMessage(i18n("InputMessage").replaceAll("\\{Exit\\}", Objects.requireNonNull(main.main.getConfig().getString("InputSettings.ExitMessage"))));
                            getShoutHashMap().put(player.getName(), MythicType);
                        }
                    }
                }
            }
        }
    }
}
