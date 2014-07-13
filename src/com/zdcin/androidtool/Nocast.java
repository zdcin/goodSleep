package com.zdcin.androidtool;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * 把需要强转的方法封装下，避免强转
 * 
 * @author leo
 * 
 */
public class Nocast {

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final Activity AC, final int id) {
        return (T) AC.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final Activity AC, Class<T> tType, final int id) {
        return (T) AC.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> T getSystemService(final Context ctx, final String name) {
        return (T) ctx.getSystemService(name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getSystemService(final Context ctx, Class<T> tType, final String name) {
        return (T) ctx.getSystemService(name);
    }
}