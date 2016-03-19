package com.bluelinelabs.conductor.internal;

import android.text.TextUtils;

public class ClassUtils {

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> classForName(String className) {
        return classForName(className, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> classForName(String className, boolean allowEmptyName) {
        if (allowEmptyName && TextUtils.isEmpty(className)) {
            return null;
        }

        try {
            return (Class<? extends T>)Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while finding class for name " + className + ". " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            Class<? extends T> cls = classForName(className);
            return cls != null ? cls.newInstance() : null;
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while creating a new instance of " + className + ". " + e.getMessage());
        }
    }

}
