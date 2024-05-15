package cn.chengzhiya.mhdfshoutbungeecordhook;

import cn.chengzhiya.mhdfshoutbungeecordhook.listener.PluginMessage;
import cn.chengzhiya.mhdfshoutbungeecordhook.task.TakeShoutDelay;
import net.md_5.bungee.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static cn.chengzhiya.mhdfshoutbungeecordhook.util.Util.*;

public final class main extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(main.class);
    public static main main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        saveConfig();
        reloadConfig();

        getProxy().getPluginManager().registerListener(this, new PluginMessage());
        getProxy().registerChannel("BungeeCord");
        getProxy().getScheduler().schedule(this, new TakeShoutDelay(), 1, 1, TimeUnit.SECONDS);

        log.info("============梦回东方-喊话系统-BC连接器============");
        log.info("插件启动完成!");
        log.info("============梦回东方-喊话系统-BC连接器============");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;

        log.info("============梦回东方-喊话系统-BC连接器============");
        log.info("插件已卸载!");
        log.info("============梦回东方-喊话系统-BC连接器============");
    }
}
