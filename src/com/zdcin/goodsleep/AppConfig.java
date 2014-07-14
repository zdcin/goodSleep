package com.zdcin.goodsleep;

import android.content.SharedPreferences;
import android.util.Log;

public class AppConfig {

    public boolean isOn = false;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;

    public int oldMusicVolume;
    public int oldRingVolume;

    /**
     * 读取的时候使用
     * 
     * @param config
     */
    public AppConfig(SharedPreferences pref) {
        Log.v("zd", "SharedPreferences.isOn=" + pref.getBoolean("isOn", false));
        this.isOn = pref.getBoolean("isOn", false);
        this.startHour = pref.getInt("startHour", 23);
        this.startMinute = pref.getInt("startMinute", 0);
        this.endHour = pref.getInt("endHour", 8);
        this.endMinute = pref.getInt("endMinute", 0);
        this.oldMusicVolume = pref.getInt("oldMusicVolume", 0);
        this.oldRingVolume = pref.getInt("oldRingVolume", 0);
    }

    /**
     * 写入的时候使用
     * 
     * @param isOn
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
    public AppConfig(boolean isOn, int startHour, int startMinute, int endHour, int endMinute, int oldMusicVolume,
            int oldRingVolume) {
        this.isOn = isOn;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.oldMusicVolume = oldMusicVolume;
        this.oldRingVolume = oldRingVolume;
    }

    /**
     * 写入的时候使用
     * 
     * @param config
     */
    public void fillSharedPreferences(SharedPreferences config) {
        config.edit().putBoolean("isOn", this.isOn).putInt("startHour", this.startHour)
                .putInt("startMinute", this.startMinute).putInt("endHour", this.endHour)
                .putInt("endMinute", this.endMinute).putInt("oldMusicVolume", this.oldMusicVolume)
                .putInt("oldRingVolume", this.endMinute).apply();
    }

}
