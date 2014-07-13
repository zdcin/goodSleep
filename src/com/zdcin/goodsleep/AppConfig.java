package com.zdcin.goodsleep;

import android.content.SharedPreferences;

public class AppConfig {
    
    public boolean isOn;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    
    /**
     * 读取的时候使用
     * @param config
     */
    public AppConfig(SharedPreferences config) {
        this.isOn = config.getBoolean("isOn", false);
        this.startHour = config.getInt("startHour", 23);
        this.startMinute = config.getInt("startMinute", 0);
        this.endHour = config.getInt("endHour", 8);
        this.endMinute = config.getInt("endMinute", 0);
    }
    
    /**
     * 写入的时候使用
     * @param isOn
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
    public AppConfig(boolean isOn, int startHour, int startMinute, int endHour, int endMinute) {
        this.isOn = isOn;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }
    
    /**
     * 写入的时候使用
     * @param config
     */
    public void fillSharedPreferences(SharedPreferences config) {
        config.edit().putBoolean("isOn", this.isOn).putInt("startHour", this.startHour)
                .putInt("startMinute", this.startMinute).putInt("endHour", this.endHour)
                .putInt("endMinute", this.endMinute);
    }

}
