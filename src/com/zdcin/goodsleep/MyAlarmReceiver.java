package com.zdcin.goodsleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 需要注册，监听自定义的闹钟事件
 * 
 * @author Leo
 * 
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    
    
    public void onReceive(Context context, Intent intent) {
        ActionType action = ActionType.get(intent.getAction());
        if (action == null) {
            return;
        }
        
        action.onReceive(context, intent);
    }
    
}