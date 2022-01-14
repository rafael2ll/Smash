package com.smash.up.Itens;
import android.util.*;

import android.util.Log;

public class LogHelper {
    public final static void v(String tag, String messages) {
       	Log.v(tag, messages);
    }
    public final static void d(String tag, String messages) {
        Log.d(tag, messages);
    }
    public final static void i(String tag, String messages) {
        Log.i(tag, messages);
    }
    public final static void w(String tag, String messages) {
       Log.w(tag, messages);
    }
    public final static void w(String tag, Throwable t, String messages) {
        Log.w(tag,messages,t);
    }
    public final static void e(String tag, Object... messages) {
        log(tag, Log.ERROR, null, messages);
    }
    public final static void e(String tag, Throwable t, Object... messages) {
        log(tag, Log.ERROR, t, messages);
    }
    public final static void log(String tag, int level, Throwable t, Object... messages) {
        if (messages != null && Log.isLoggable(tag, level)) {
            String message = null;
            if (messages.length == 1) {
                message = messages[0] == null ? null : messages[0].toString();
            } else {
                StringBuilder sb = new StringBuilder();
                for (Object m: messages) {
                    sb.append(m);
                }
                message = sb.toString();
            }
            Log.d(tag, message, t);
        }
    }
}
