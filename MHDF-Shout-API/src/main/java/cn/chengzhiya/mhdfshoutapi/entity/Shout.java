package cn.chengzhiya.mhdfshoutapi.entity;

import lombok.Data;

@Data
public final class Shout {
    String BossBarColor;
    String BossBarBackground;
    String Message;
    String[] Sound;
    int ShowTime;

    public Shout(String BossBarColor, String BossBarBackground, String Message, String Sound, int ShowTime) {
        this.BossBarColor = BossBarColor;
        this.BossBarBackground = BossBarBackground;
        this.Message = Message;
        this.Sound = Sound.split("\\|");
        this.ShowTime = ShowTime;
    }
}
