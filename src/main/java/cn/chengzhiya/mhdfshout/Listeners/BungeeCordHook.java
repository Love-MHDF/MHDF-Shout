package cn.chengzhiya.mhdfshout.Listeners;

import cn.chengzhiya.mhdfshout.Shout;
import net.kyori.adventure.bossbar.BossBar;
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
                String Background = in.readUTF();
                String Message = in.readUTF();
                String Sound = in.readUTF();
                int ShowTime = in.readInt();

                ShoutList.add(new Shout(BossBar.Color.valueOf(Color), Background, Message, Sound, ShowTime));

                if (ShoutList.size() == 1) {
                    StartShout();
                }
            }
            if (subchannel.equals("SendAdminShout")) {
                String Color = in.readUTF();
                String Background = in.readUTF();
                String Message = in.readUTF();
                String Sound = in.readUTF();
                int ShowTime = in.readInt();

                StartAdminShout(new Shout(BossBar.Color.valueOf(Color), Background, Message, Sound, ShowTime));
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
}
