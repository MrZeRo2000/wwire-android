package com.romanpulov.wwire.gles;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.romanpulov.wwire.model.WWireData;

public final class DrawerFactory {

    public static Class<?>[] DrawerListClass = {ElementsDrawer.class, DiagramDrawer.class};
    public static String[] DrawerListTitle = {ElementsDrawer.title, DiagramDrawer.title};

    private static DrawerFactory mInstance;
    private DiagramDrawer mDiagramDrawer;
    private ElementsDrawer mElementsDrawer;

    public static DrawerFactory getInstance() {
        if (null == mInstance) {
            mInstance = new DrawerFactory();
        }
        return mInstance;
    }

    private DrawerFactory() {

    }

    public void invalidateModelDrawers() {
        mElementsDrawer = null;
        mDiagramDrawer = null;
    }

    @Nullable
    public GLES20Primitives.ModelDrawer getModelDrawer(@NonNull WWireData data, @NonNull Class<?> drawerClass) {
        if (drawerClass.equals(DiagramDrawer.class)) {
            if (null == mDiagramDrawer) {
                mDiagramDrawer = new DiagramDrawer(data);
            }
            return mDiagramDrawer;
        } else if (drawerClass.equals(ElementsDrawer.class)) {
            if (null == mElementsDrawer) {
                mElementsDrawer = new ElementsDrawer(data);
            }
            return mElementsDrawer;
        } else
            return null;
    }
}
