package cn.chengzhiya.mhdfshout.Listeners;

import cn.chengzhiya.mhdfshout.Shout;
import cn.chengzhiya.mhdfshout.main;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static cn.chengzhiya.mhdfshout.Util.*;

public final class BungeeCordHook implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try {
            String subchannel = in.readUTF();
            if (subchannel.equals("SendShout")) {
                String Color = in.readUTF();
                String Message = in.readUTF();
                String Sound = in.readUTF();
                int ShowTime = in.readInt();

                ShoutList.add(new Shout(BossBar.Color.valueOf(Color), Message, Sound, ShowTime));

                if (ShoutList.size() == 1) {
                    SendShout();
                }
            }
            if (subchannel.equals("SendAdminShout")) {
                String Color = in.readUTF();
                String Message = in.readUTF();
                String[] Sound = in.readUTF().split("\\|");
                int ShowTime = in.readInt();

                BossBar ShoutBossBar = BossBar.bossBar(Component.text(Message), 1f, BossBar.Color.valueOf(Color), BossBar.Overlay.PROGRESS);
                BossBar NullBossBar = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.valueOf(Color), BossBar.Overlay.PROGRESS);
                for (Player OnlinePlayer : Bukkit.getOnlinePlayers()) {
                    OnlinePlayer.showBossBar(NullBossBar);
                    OnlinePlayer.showBossBar(ShoutBossBar);

                    Bukkit.getScheduler().runTaskLaterAsynchronously(main.main, () -> {
                        OnlinePlayer.hideBossBar(NullBossBar);
                        OnlinePlayer.hideBossBar(ShoutBossBar);
                    }, 20L * ShowTime);

                    try {
                        OnlinePlayer.playSound(OnlinePlayer, org.bukkit.Sound.valueOf(Sound[0]), Float.parseFloat(Sound[1]), Float.parseFloat(Sound[2]));
                    } catch (Exception e) {
                        OnlinePlayer.playSound(OnlinePlayer, Sound[0], Float.parseFloat(Sound[1]), Float.parseFloat(Sound[2]));
                    }
                }
            }
            if (subchannel.equals("GetDelayTime")) {
                String PlayerName = in.readUTF();
                int DelayTime = in.readInt();
                if (DelayTime != -1) {
                    getShoutDelayTimeHashMap().put(PlayerName, DelayTime);
                } else {
                    getShoutDelayTimeHashMap().remove(PlayerName);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public void SendShout() {
        Shout Shout = ShoutList.get(0);

        for (Player OnlinePlayer : Bukkit.getOnlinePlayers()) {
            BossBar NullBossBar = BossBar.bossBar(Component.text(""), 1f, Shout.getBossBarColor(), BossBar.Overlay.PROGRESS);
            BossBar ShoutBossBar = BossBar.bossBar(Component.text(Shout.getMessage()), 1f, Shout.getBossBarColor(), BossBar.Overlay.PROGRESS);

            OnlinePlayer.showBossBar(NullBossBar);
            OnlinePlayer.showBossBar(ShoutBossBar);

            try {
                OnlinePlayer.playSound(OnlinePlayer, org.bukkit.Sound.valueOf(Shout.getSound()[0]), Float.parseFloat(Shout.getSound()[1]), Float.parseFloat(Shout.getSound()[2]));
            } catch (Exception e) {
                OnlinePlayer.playSound(OnlinePlayer, Shout.getSound()[0], Float.parseFloat(Shout.getSound()[1]), Float.parseFloat(Shout.getSound()[2]));
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(main.main, () -> {
                OnlinePlayer.hideBossBar(NullBossBar);
                OnlinePlayer.hideBossBar(ShoutBossBar);
            }, 20L * Shout.getShowTime());
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(main.main, () -> {
            ShoutList.remove(Shout);
            if (!ShoutList.isEmpty()) {
                SendShout();
            }
        }, 20L * Shout.getShowTime());
    }
}
