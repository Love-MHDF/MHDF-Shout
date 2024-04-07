package cn.chengzhiya.mhdfshout;

import lombok.Data;
import net.kyori.adventure.bossbar.BossBar;

@Data
public final class Shout {
    BossBar.Color BossBarColor;
    String BossBarBackground;
    String Message;
    String[] Sound;
    int ShowTime;

    public Shout(BossBar.Color BossBarColor, String BossBarBackground, String Message, String Sound, int ShowTime) {
        this.BossBarColor = BossBarColor;
        this.BossBarBackground = BossBarBackground;
        this.Message = Message;
        this.Sound = Sound.split("\\|");
        this.ShowTime = ShowTime;
    }
}
