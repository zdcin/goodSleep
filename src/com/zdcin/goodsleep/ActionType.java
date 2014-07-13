package com.zdcin.goodsleep;

import com.zdcin.androidtool.Nocast;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 把action封装成枚举的方式，让action的功能高内聚，代码集中，不同组件上的几组action可以考虑用多个类似的枚举来实现
 * 
 * @author leo
 * 
 */
public enum ActionType {

    START_GOOD_SLEEP_INTENT_ACTION("com.zdcin.goodsleep.START_GOOD_SLEEP_INTENT_ACTION") {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 启动good sleep服务
            Log.v("zd", "on START_GOOD_SLEEP_INTENT_ACTION");

            // 1. 把铃声和音乐静音，记录之前值
            final AudioManager audioManager = Nocast.getSystemService(context, Context.AUDIO_SERVICE);

            final int OLD_MUSIC_VOLUME = intent.getIntExtra(Keys.music_volum.name(), 0);
            final int OLD_RING_VOLUME = intent.getIntExtra(Keys.ring_volum.name(), 0);
            MyUtils.muteAll(audioManager);
            Log.v("zd", "静音铃声和音乐");

            // 2. 注册电话响铃事件，响铃时，把铃声音量恢复, 注册电话挂断事件，挂断后，把铃声静音
            TelephonyManager tm = Nocast.getSystemService(context, Context.TELEPHONY_SERVICE);
            tm.listen(MyPhoneStateListener.get(audioManager, OLD_RING_VOLUME, OLD_MUSIC_VOLUME),
                    PhoneStateListener.LISTEN_CALL_STATE);
        }
    },

    END_GOOD_SLEEP_INTENT_ACTION("com.zdcin.goodsleep.END_GOOD_SLEEP_INTENT_ACTION") {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 停止good sleep服务
            Log.v("zd", "on END_GOOD_SLEEP_INTENT_ACTION");
            // 1. 把铃声和音乐音量恢复
            final AudioManager audioManager = Nocast.getSystemService(context, Context.AUDIO_SERVICE);

            final int OLD_MUSIC_VOLUME = intent.getIntExtra(Keys.music_volum.name(), 0);
            final int OLD_RING_VOLUME = intent.getIntExtra(Keys.ring_volum.name(), 0);
            MyUtils.unMuteAll(audioManager, OLD_RING_VOLUME, OLD_MUSIC_VOLUME);
            Log.v("zd", "恢复铃声和音乐");

            // 2. 取消监听电话响铃事件
            TelephonyManager tm = Nocast.getSystemService(context, Context.TELEPHONY_SERVICE);
            tm.listen(MyPhoneStateListener.get(audioManager, OLD_RING_VOLUME, OLD_MUSIC_VOLUME),
                    PhoneStateListener.LISTEN_NONE);
        }
    };

    /**
     * action 值
     */
    public final String action;

    private ActionType(String action) {
        this.action = action;
    }

    /**
     * 通过action值获得action类型
     * 
     * @param action
     */
    public static ActionType get(String action) {
        for (ActionType t : ActionType.values()) {
            if (action.equals(t.action)) {
                return t;
            }
        }
        return null;
    }

    /**
     * 执行action对应的工作，可以考虑把调用这个方法的BroadcastReceiver作为参数传入，这样上下文就完整了
     * 
     * @param context
     * @param intent
     */
    public abstract void onReceive(Context context, Intent intent);

    private static class MyPhoneStateListener extends PhoneStateListener {
        private static MyPhoneStateListener i;

        public static synchronized MyPhoneStateListener get(AudioManager audioManager, int OLD_RING_VOLUME,
                int OLD_MUSIC_VOLUME) {
            if (i == null) {
                i = new MyPhoneStateListener(audioManager, OLD_RING_VOLUME, OLD_MUSIC_VOLUME);
            }
            return i;
        }

        final AudioManager audioManager;

        final int OLD_MUSIC_VOLUME;
        final int OLD_RING_VOLUME;

        private MyPhoneStateListener(AudioManager audioManager, int OLD_RING_VOLUME, int OLD_MUSIC_VOLUME) {
            this.audioManager = audioManager;
            this.OLD_RING_VOLUME = OLD_RING_VOLUME;
            this.OLD_MUSIC_VOLUME = OLD_MUSIC_VOLUME;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.v("zd", "监听电话状态");
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.v("zd", "有电话进来，恢复铃声");
                // 打开铃声音量
                MyUtils.unMuteAll(audioManager, OLD_RING_VOLUME, OLD_MUSIC_VOLUME);
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.v("zd", "电话挂断，静音铃声");
                // 关闭铃声音量
                MyUtils.muteAll(audioManager);
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                Log.v("zd", "电话空闲，静音铃声");
                // 关闭铃声音量
                MyUtils.muteAll(audioManager);
            }
        }
    }

}
