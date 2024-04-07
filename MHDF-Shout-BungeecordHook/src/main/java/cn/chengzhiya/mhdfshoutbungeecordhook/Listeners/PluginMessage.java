package cn.chengzhiya.mhdfshoutbungeecordhook.Listeners;

import cn.chengzhiya.mhdfshoutbungeecordhook.main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static cn.chengzhiya.mhdfshoutbungeecordhook.main.ShoutDelayTimeHashMap;

public final class PluginMessage implements Listener {
    public ServerInfo getServerInfo(PluginMessageEvent event) {
        ServerInfo server = null;
        for (Map.Entry one : main.main.getProxy().getServers().entrySet()) {
            if (!(((ServerInfo) one.getValue()).getAddress().getAddress().getHostAddress() + ":" + ((ServerInfo) one.getValue()).getAddress().getPort())
                    .equalsIgnoreCase(
                            event.getSender().getAddress().getAddress().getHostAddress() + ":" + event.getSender().getAddress().getPort()
                    )) {
                continue;
            }
            server = (ServerInfo) one.getValue();
        }
        if (server == null || server.getPlayers().isEmpty()) {
            return null;
        }
        return server;
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if (!event.getTag().contains("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

        try {
            String subchannel = in.readUTF();

            if (subchannel.equals("SendShout")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("SendShout");
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeInt(in.readInt());
                for (Map.Entry one : main.main.getProxy().getServers().entrySet()) {
                    ServerInfo Server = (ServerInfo) one.getValue();
                    if (!Server.getPlayers().isEmpty()) {
                        Server.sendData("BungeeCord", out.toByteArray());
                    }
                }
            }
            if (subchannel.equals("SendAdminShout")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("SendAdminShout");
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeUTF(in.readUTF());
                out.writeInt(in.readInt());
                for (Map.Entry one : main.main.getProxy().getServers().entrySet()) {
                    ServerInfo Server = (ServerInfo) one.getValue();
                    if (!Server.getPlayers().isEmpty()) {
                        Server.sendData("BungeeCord", out.toByteArray());
                    }
                }
            }
            if (subchannel.equals("SetDelayTime")) {
                ShoutDelayTimeHashMap.put(in.readUTF(), in.readInt());
            }
            if (subchannel.equals("GetDelayTime")) {
                ServerInfo Server = getServerInfo(event);
                String PlayerName = in.readUTF();

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("GetDelayTime");
                out.writeUTF(PlayerName);
                if (ShoutDelayTimeHashMap.get(PlayerName) != null) {
                    out.writeInt(ShoutDelayTimeHashMap.get(PlayerName));
                } else {
                    out.writeInt(-1);
                }

                Objects.requireNonNull(Server).sendData("BungeeCord", out.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
