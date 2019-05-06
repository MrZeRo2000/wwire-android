package com.romanpulov.wwire.gles;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.romanpulov.wwire.model.WWireData;

import java.lang.reflect.Constructor;

public final class DrawerFactory {

    public static final Class<?>[] DRAWER_CLASSES = {ElementsDrawer.class, DiagramDrawer.class};

    private DrawerFactory() {
        throw new AssertionError();
    }

    @Nullable
    public static GLES20Primitives.ModelDrawer getModelDrawer(@NonNull WWireData data, @NonNull Class<?> drawerClass) {
        GLES20Primitives.ModelDrawer drawer = null;

        try {
            Constructor<?> drawerConstructor = drawerClass.getDeclaredConstructor(WWireData.class);
            drawerConstructor.setAccessible(true);
            drawer = (GLES20Primitives.ModelDrawer)drawerConstructor.newInstance(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawer;
    }
}
