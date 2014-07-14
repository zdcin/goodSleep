package com.zdcin.goodsleep;

import java.util.Date;

import android.app.AlarmManager;
import android.media.AudioManager;

public class MyUtils {

    @SuppressWarnings({ "deprecation" })
    public static long getFirstRunTime(int hour, int min) {
        long now = System.currentTimeMillis();
        long tomorow = now + AlarmManager.INTERVAL_DAY;
        Date temp = new Date(tomorow);
        temp.setHours(hour);
        temp.setMinutes(min);
        temp.setSeconds(0);
        return (temp.getTime() - now) % AlarmManager.INTERVAL_DAY + now;
    }
    
    public static void muteAll(AudioManager audioManager) {
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 63);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 63);
    }
    
    public static void unMuteAll(AudioManager audioManager, int ring, int music) {
        audioManager.setStreamVolume(AudioManager.STREAM_RING, ring, 63);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music, 63);
    }
}
