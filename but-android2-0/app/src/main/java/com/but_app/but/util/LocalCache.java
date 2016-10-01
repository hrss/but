package com.but_app.but.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.but_app.but.But;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by iGor Montella on 07/10/2014.
 */
public class LocalCache {

    private final ConcurrentMap<String, Object> lock = new ConcurrentHashMap<String, Object>();
    private final ConcurrentMap<String, Object> cache = new ConcurrentHashMap<String, Object>();
    private final SharedPreferences settings;

    private static final LocalCache INSTANCE = new LocalCache();

    public static LocalCache get() {
        return INSTANCE;
    }

    private LocalCache() {
        this.settings = But.get().getSharedPreferences(KeyUtils.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @SuppressWarnings("unchecked")
    public <T> T peekField(String key, Type aClass, T defaultValue) {
        T object = (T) cache.get(key);
        lock.putIfAbsent(key, new Object());

        synchronized (lock.get(key)) {
            if (object == null) {
                String json = settings.getString(key, null);

                if (json != null) {
                    object = new Gson().fromJson(json, aClass);
                } else {
                    object = defaultValue;
                    updateField(key, object);
                }
            }
        }

        return object;
    }

    public boolean contains(String key) {
        return settings.contains(key) && settings.getAll().get(key) != null;
    }

    public void remove(String key) {
        cache.remove(key);
        settings.edit().remove(key).commit();
    }

    public <T> T pullField(String key, Type aClass, T defaultValue) {
        T t = peekField(key, aClass, defaultValue);
        cache.remove(key);
        settings.edit().remove(key).commit();
        return t;
    }

    public <T> void updateField(String key, T object) {
        if (object == null) {
            cache.remove(key);
        } else {
            cache.put(key, object);
        }

        settings.edit().putString(key, (object == null) ? null : new Gson().toJson(object)).commit();
    }

    public boolean clearAll() {
        cache.clear();
        return settings.edit().clear().commit();
    }

}
