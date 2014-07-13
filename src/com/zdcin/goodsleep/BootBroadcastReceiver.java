package com.zdcin.goodsleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("zd", "BootBroadcastReceiver.onReceive, action=" + intent.getAction());
        if (intent.getAction().equals(action_boot)) {
            Log.v("zd", "GOOD SLEEP BOOT_COMPLETED, goodSleep 自启动成功。");
            Intent ootStartIntent = new Intent(context, MainActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
    }

}