package com.zdcin.goodsleep;

import java.util.Date;

import com.zdcin.androidtool.Nocast;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

/**
 * 添加自启动功能
 * @author leo
 *
 */
public class MainActivity extends Activity {

    /** 给内部类使用的activity this引用 */
    private final MainActivity thisa = this;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1. 启动时从文件中读取设置，根据设置现实界面各个组件的值
        {
            Log.v("zd", "on MainActivity create.");
            SharedPreferences sharedPreferences = this.getSharedPreferences(Keys.app_config_file.name(), 0);
            AppConfig config = new AppConfig(sharedPreferences);
            TimePicker start = Nocast.findViewById(this, R.id.timePicker1);
            TimePicker end = Nocast.findViewById(this, R.id.timePicker2);

            start.setIs24HourView(true);
            start.setCurrentHour(config.startHour);
            start.setCurrentMinute(config.startMinute);
            end.setIs24HourView(true);
            end.setCurrentHour(config.endHour);
            end.setCurrentMinute(config.endMinute);
            Nocast.findViewById(this, Switch.class, R.id.switch1).setChecked(config.isOn);
        }

        // 2. 注册保存设置事件
        Nocast.findViewById(this, Button.class, R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("zd", "onClick");
                Switch isOn = Nocast.findViewById(thisa, Switch.class, R.id.switch1);
                TimePicker start = Nocast.findViewById(thisa, R.id.timePicker1);
                TimePicker end = Nocast.findViewById(thisa, R.id.timePicker2);
                //检查起止时间是否一样，一样的话让end比begin早一分钟，这样就可以几乎全天生效
                if (end.getCurrentHour() == start.getCurrentHour() 
                            && end.getCurrentMinute() == start.getCurrentMinute()) {
                   if (end.getCurrentHour() == 0 && end.getCurrentMinute() == 0) {
                       end.setCurrentHour(23);
                       end.setCurrentMinute(59);
                   } else if (end.getCurrentMinute() == 0) {
                       end.setCurrentHour(end.getCurrentMinute() - 1);
                       end.setCurrentMinute(59);
                   } else {
                        end.setCurrentMinute(end.getCurrentMinute() - 1);
                   }
                }
                
                //获取生效之前的音量信息
                final AudioManager audioManager = Nocast.getSystemService(thisa, Context.AUDIO_SERVICE);
                int oldMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int oldRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

                AppConfig config = new AppConfig(isOn.isChecked(), start.getCurrentHour(), start.getCurrentMinute(),
                        end.getCurrentHour(), end.getCurrentMinute(), oldMusicVolume, oldRingVolume);
                // 1 保存设置
                thisa.save(config);
                // 2. 关闭窗口
                // thisa.finish();
            }
        });
        Nocast.findViewById(this, Button.class, R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("zd", "onQuit");
                // 2. 关闭窗口
                 thisa.finish();
            }
        });
    }

    private void save(AppConfig config) {
        //不应该出现 activety控件相关的东西，使用纯后台逻辑
        Log.v("zd", "save");
        // 1. 变量初始化
        final AlarmManager alarmManager = Nocast.getSystemService(this, Context.ALARM_SERVICE);

        //2. save to file
        {
            SharedPreferences sp = this.getSharedPreferences(Keys.app_config_file.name(), 0);
            //2.1 新的配置生效之前，先要恢复之前的音量设置
            AppConfig oldConfig = new AppConfig(sp);
            if (oldConfig.isOn) {
                Log.v("zd", "新的配置生效之前，先要恢复之前的音量设置, oldConfig.ring=" + oldConfig.oldRingVolume);
                final AudioManager audioManager = Nocast.getSystemService(thisa, Context.AUDIO_SERVICE);
                MyUtils.unMuteAll(audioManager, oldConfig.oldRingVolume, oldConfig.oldMusicVolume);
                config.oldMusicVolume = oldConfig.oldMusicVolume;
                config.oldRingVolume = oldConfig.oldRingVolume;
            }
            //2.2 关闭之前的定时任务, 不管有没有
            {
//                this.getPendingIntentAndCancleBefore(alarmManager, ActionType.START_GOOD_SLEEP_INTENT_ACTION.action,
//                        oldConfig.oldMusicVolume, oldConfig.oldRingVolume);
//                this.getPendingIntentAndCancleBefore(alarmManager, ActionType.END_GOOD_SLEEP_INTENT_ACTION.action,
//                        oldConfig.oldMusicVolume, oldConfig.oldRingVolume);
                this.getPendingIntentAndCancleBefore(alarmManager, ActionType.START_GOOD_SLEEP_INTENT_ACTION.action);
                this.getPendingIntentAndCancleBefore(alarmManager, ActionType.END_GOOD_SLEEP_INTENT_ACTION.action);
             // 2. 取消监听电话响铃事件
                TelephonyManager tm = Nocast.getSystemService(this, Context.TELEPHONY_SERVICE);
                ActionType.MyPhoneStateListener.cancel(tm);
            }
            config.fillSharedPreferences(sp);
        }
        // 3. 检查是否关闭service
        if (!config.isOn) {
            return;
        }
        
        // 4. 注册自定义的闹钟事件
        {
            PendingIntent beginPi = this.getPendingIntentAndCancleBefore(alarmManager,
                    ActionType.START_GOOD_SLEEP_INTENT_ACTION.action);

            long firstRunTime = MyUtils.getFirstRunTime(config.startHour, config.startMinute);
            alarmManager.setRepeating(AlarmManager.RTC, firstRunTime, AlarmManager.INTERVAL_DAY, beginPi);
            Log.v("zd", "注册闹钟开始事件，闹钟启动时间：" + new Date(firstRunTime));
            // 如果当前时间介于闹铃开始和结束之间，那么现在就要让闹铃生效
            if (isNeedRunNow(config)) {
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, beginPi);
                Log.v("zd", "现在就要启动");
            }
        }
        {
            PendingIntent endPi = this.getPendingIntentAndCancleBefore(alarmManager,
                    ActionType.END_GOOD_SLEEP_INTENT_ACTION.action);

            long firstRunTime = MyUtils.getFirstRunTime(config.endHour, config.endMinute);
            alarmManager.setRepeating(AlarmManager.RTC, firstRunTime, AlarmManager.INTERVAL_DAY, endPi);
            Log.v("zd", "注册闹钟停止事件，闹钟停止时间：" + new Date(firstRunTime));
        }
    }

    /**
     * 如果当前时间介于闹铃开始和结束之间，那么现在就要让闹铃生效
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings("deprecation")
    private boolean isNeedRunNow(AppConfig conf) {
       
        Date now = new Date();
        int nowi = now.getHours() * 60 + now.getMinutes();
        int starti = conf.startHour * 60 + conf.startMinute;
        int endi = conf.endHour * 60 + conf.endMinute;
        // 20 - 22, 23 - 8
        if (starti < endi && nowi >= starti && nowi < endi) {
            return true;
        } else if (starti > endi && (nowi >= starti || nowi < endi)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * 关闭老的action，创建新的，但是不启动
     * @param alarmManager
     * @param actionStr
     * @return
     */
    private PendingIntent getPendingIntentAndCancleBefore(AlarmManager alarmManager, String actionStr) {
        Intent intent = new Intent(actionStr);
//        intent.putExtra(Keys.ring_volum.name(), oldMusicVolume);
//        intent.putExtra(Keys.music_volum.name(), oldRingVolume);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 结束之前运行的定时任务，如果有的话
        alarmManager.cancel(pi);
        return pi;
    }
    
}
