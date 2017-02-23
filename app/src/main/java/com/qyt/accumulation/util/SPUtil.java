package com.qyt.accumulation.util;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.qyt.accumulation.App;
import com.qyt.accumulation.entity.User;

/**
 * SharedPreferences 工具类
 * Created by Relish on 2016/12/3.
 */

public class SPUtil {
    private static final String TAG = "SPUtil";

    private static SharedPreferences settings;

    static {
        settings = App.CONTEXT.getSharedPreferences("android_preference", 0);
    }

    public static User getUser() {
        User user = new User();
        user.setName(checkNull("name", user.getName()));
        user.setPassword(checkNull("password", user.getPassword()));
        user.setPhoto(checkNull("photo", user.getPhoto()));
        user.setMobile(checkNull("mobile", user.getMobile()));
        return user;
    }


    public static void saveUser(User user) {
        if (user == null) {
            AppLog.e(TAG, "saveUser", "coming param is null!!!");
            return;
        }
        save("name", user.getName());
        save("photo", user.getPhoto());
        save("password", user.getPassword());
        save("mobile", user.getMobile());
    }


    private static void save(String key, Object value) {
        if (value instanceof String) {
            //noinspection ConstantConditions
            putString(key, checkNull(key, (String) value));
        } else if (value instanceof Integer) {
            //noinspection ConstantConditions
            putInt(key, checkNull(key, (Integer) value));
        } else if (value instanceof Long) {
            //noinspection ConstantConditions
            putLong(key, checkNull(key, (Long) value));
        } else if (value instanceof Float) {
            //noinspection ConstantConditions
            putFloat(key, checkNull(key, (Float) value));
        } else if (value instanceof Boolean) {
            //noinspection ConstantConditions
            putBoolean(key, checkNull(key, (Boolean) value));
        } else {
            if (value != null) {
                AppLog.e(TAG, "save", "Unknown type: " + value.getClass().toString());
            }
        }
    }

    private static <T> T checkNull(String key, T value) {
        if (value instanceof String) {
            //noinspection unchecked
            return TextUtils.isEmpty((String) value) ||
                    ((String) value).equalsIgnoreCase("null") ?
                    (T) getString(key, "") : value;
        } else if (value instanceof Integer || value instanceof Boolean ||
                value instanceof Long || value instanceof Float) {
            //noinspection unchecked
            return value;
        } else {
            if (value != null)
                AppLog.e(TAG, "checkNull", "Unknown type: " + value.getClass());
            return null;
        }
    }

    public static boolean putString(String key, String value) {
        if (key != null && value != null) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            return editor.commit();
        } else {
            return false;
        }
    }

    public static String getString(String key) {
        return getString(key, (String) null);
    }

    public static String getString(String key, String defaultValue) {
        return settings.getString(key, defaultValue);
    }

    public static boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static int getInt(String key, int defaultValue) {
        return settings.getInt(key, defaultValue);
    }

    public static boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(String key) {
        return getLong(key, -1L);
    }

    public static long getLong(String key, long defaultValue) {
        return settings.getLong(key, defaultValue);
    }

    public static boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static float getFloat(String key) {
        return getFloat(key, -1.0F);
    }

    public static float getFloat(String key, float defaultValue) {
        return settings.getFloat(key, defaultValue);
    }

    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear().apply();
    }


}
