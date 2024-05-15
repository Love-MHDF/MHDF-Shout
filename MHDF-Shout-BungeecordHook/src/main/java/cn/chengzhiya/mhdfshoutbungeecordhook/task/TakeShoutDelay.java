package cn.chengzhiya.mhdfshoutbungeecordhook.task;

import cn.chengzhiya.mhdfshoutbungeecordhook.util.Util;

public final class TakeShoutDelay implements Runnable {
    @Override
    public void run() {
        for (String player : Util.getShoutDelayHashMap().keySet()) {
            if (Util.getShoutDelayHashMap().get(player) > 0) {
                Util.getShoutDelayHashMap().put(player, Util.getShoutDelayHashMap().get(player) - 1);
            } else {
                Util.getShoutDelayHashMap().remove(player);
            }
        }
    }
}
