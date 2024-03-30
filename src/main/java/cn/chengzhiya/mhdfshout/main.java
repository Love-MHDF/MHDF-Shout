package cn.chengzhiya.mhdfshout;

import cn.chengzhiya.mhdfshout.Commands.AdminShout;
import cn.chengzhiya.mhdfshout.Commands.ShoutReload;
import cn.chengzhiya.mhdfshout.Listeners.*;
import cn.chengzhiya.mhdfshout.Tasks.ShoutDelayTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.Cipher;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import static cn.chengzhiya.mhdfpluginapi.Util.*;
import static cn.chengzhiya.mhdfpluginapi.YamlFileUtil.*;
import static cn.chengzhiya.mhdfshout.Util.*;

public final class main extends JavaPlugin {
    public static main main;
    public static String Version = "1.0.1";

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        File PluginHome = getDataFolder();
        File ConfigFile = new File(PluginHome, "config.yml");
        File LangFile = new File(PluginHome, "lang.yml");

        if (!PluginHome.exists()) {
            boolean Stats = PluginHome.mkdirs();
            if (!Stats) {
                ColorLog("&c[MHDF-PluginAPI]插件数据文件夹创建失败!");
            }
        }
        if (!ConfigFile.exists()) {
            SaveResource(this.getDataFolder().getPath(), "config.yml", "config.yml", true);
        }
        if (!LangFile.exists()) {
            SaveResource(this.getDataFolder().getPath(), "lang.yml", "lang.yml", true);
        }

        if (getConfig().getBoolean("BungeeCordMode")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordHook());
        } else {
            new ShoutDelayTime().runTaskTimerAsynchronously(this, 0L, 20L);
        }

        String QAQ = "false";
        try {
            PublicKey publicKey;
            {
                HttpURLConnection conn = (HttpURLConnection) new URL("https://mhdf.love:8888/rsa/getpublic").openConnection();

                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONObject Data = JSON.parseObject(in.readLine());

                in.close();
                conn.disconnect();

                byte[] decodedPublicKey = Base64.getDecoder().decode(Data.getString("data"));
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                publicKey = keyFactory.generatePublic(keySpec);
            }
            {
                Cipher encryptCipher = Cipher.getInstance("RSA");
                encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                PluginUser PluginUser = new PluginUser();
                PluginUser.setUserName(Base64.getEncoder().encodeToString(encryptCipher.doFinal(Objects.requireNonNull(getConfig().getString("SignSettings.Username")).getBytes())));
                PluginUser.setUserPassword(Base64.getEncoder().encodeToString(encryptCipher.doFinal(Objects.requireNonNull(getConfig().getString("SignSettings.Password")).getBytes())));
                PluginUser.setPluginName("MHDF-Shout");

                HttpURLConnection conn = (HttpURLConnection) new URL("https://mhdf.love:8888/plugin/verify").openConnection();

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(JSON.toJSONString(PluginUser).getBytes(StandardCharsets.UTF_8));
                out.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();
                    if (JSON.parseObject(response.toString()).get("msg").equals("success")) {
                        QAQ = "true";
                    }
                }

                conn.disconnect();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ColorLog("&f[MHDF-Shout] &d  __  __ _    _ _____  ______    _____ _                 _   \n" +
                "&f[MHDF-Shout] &d |  \\/  | |  | |  __ \\|  ____|  / ____| |               | |  \n" +
                "&f[MHDF-Shout] &d | \\  / | |__| | |  | | |__    | (___ | |__   ___  _   _| |_ \n" +
                "&f[MHDF-Shout] &d | |\\/| |  __  | |  | |  __|    \\___ \\| '_ \\ / _ \\| | | | __|\n" +
                "&f[MHDF-Shout] &d | |  | | |  | | |__| | |       ____) | | | | (_) | |_| | |_ \n" +
                "&f[MHDF-Shout] &d |_|  |_|_|  |_|_____/|_|      |_____/|_| |_|\\___/ \\__,_|\\__|\n" +
                "&f[MHDF-Shout]");

        String LastVersion;
        {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("https://mhdf.love:8888/plugin/version/MHDF-Shout").openConnection();

                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONObject Data = JSON.parseObject(in.readLine());
                LastVersion = Data.getString("data");

                in.close();
                conn.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (QAQ.equals("true")) {
            if (!LastVersion.equals(Version)) {
                ColorLog("&f[MHDF-Shout] &c最新版本: " + LastVersion + " | 当前版本: " + Version);
                ColorLog("&f[MHDF-Shout] &c当前版本已过期,无法启动插件!");
                ColorLog("&f[MHDF-Shout] &c梦东系列插件 交流群号:129139830");
            }else {
                Bukkit.getPluginManager().registerEvents(new UseShout(), this);
                Bukkit.getPluginManager().registerEvents(new SendShoutMessage(), this);
                Bukkit.getPluginManager().registerEvents(new AntiChangeItem(), this);
                Bukkit.getPluginManager().registerEvents(new QuitCancel(), this);
                Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);

                for (String Commands : getConfig().getStringList("HornSettings.AdminShout.Commands")) {
                    registerCommand(this, new AdminShout(), "管理喊话", Commands);
                }

                registerCommand(this, new ShoutReload(), "重载配置", "shoutreload");

                ColorLog("&f[MHDF-Shout] &a已成功通过验证! 加载完成!");
                ColorLog("&f[MHDF-Shout] &a欢迎使用梦东系列插件 交流群号:129139830");
            }
        } else {
            ColorLog("&f[MHDF-Shout] &c验证失败! 请支持正版!");
            ColorLog("&f[MHDF-Shout] &c梦东系列插件 交流群号:129139830");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;

        ColorLog("&f[MHDF-Shout] &d  __  __ _    _ _____  ______    _____ _                 _   \n" +
                "&f[MHDF-Shout] &d |  \\/  | |  | |  __ \\|  ____|  / ____| |               | |  \n" +
                "&f[MHDF-Shout] &d | \\  / | |__| | |  | | |__    | (___ | |__   ___  _   _| |_ \n" +
                "&f[MHDF-Shout] &d | |\\/| |  __  | |  | |  __|    \\___ \\| '_ \\ / _ \\| | | | __|\n" +
                "&f[MHDF-Shout] &d | |  | | |  | | |__| | |       ____) | | | | (_) | |_| | |_ \n" +
                "&f[MHDF-Shout] &d |_|  |_|_|  |_|_____/|_|      |_____/|_| |_|\\___/ \\__,_|\\__|\n" +
                "&f[MHDF-Shout]");
        ColorLog("&f[MHDF-Shout] &9服务已卸载! 感谢您再次支持!");
        ColorLog("&f[MHDF-Shout] &9梦东系列插件 交流群号:129139830");
    }
}
