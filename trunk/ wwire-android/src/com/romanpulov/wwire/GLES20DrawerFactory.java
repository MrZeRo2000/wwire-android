package com.romanpulov.wwire;

public final class GLES20DrawerFactory {
	
	public static Class<?>[] DrawerListClass = {ElementsDrawer.class, DiagramDrawer.class};
	public static String[] DrawerListTitle = {ElementsDrawer.title, DiagramDrawer.title};
	
	private static GLES20DrawerFactory mInstance;	
	private DiagramDrawer mDiagramDrawer;
	private ElementsDrawer mElementsDrawer;
	
	public static GLES20DrawerFactory getInstance() {
		if (null == mInstance) {
			mInstance = new GLES20DrawerFactory();
		}
		return mInstance;
	}
	
	private GLES20DrawerFactory() {
		
	}
	
	public GLES20Primitives.ModelDrawer getModelDrawer(Class<?> drawerClass) {
		if (drawerClass.equals(DiagramDrawer.class)) {
			if (null == mDiagramDrawer) {
				mDiagramDrawer = new DiagramDrawer(); 
			}
			return mDiagramDrawer;
		} else if (drawerClass.equals(ElementsDrawer.class)) {
			if (null == mElementsDrawer) {
				mElementsDrawer = new ElementsDrawer(); 
			}
			return mElementsDrawer;
		} else
			return null;
	}
}
