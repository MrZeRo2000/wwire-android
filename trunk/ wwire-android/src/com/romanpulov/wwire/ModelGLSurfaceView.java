package com.romanpulov.wwire;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ModelGLSurfaceView extends GLSurfaceView {
	
	private ModelRenderer mModelRenderer;
	private float mDensity;
	private GestureHandler mGestureHandler;
	
	public ModelRenderer getModelRenderer() {
		return mModelRenderer;
	}
	
	public void setModelRenderer(ModelRenderer modelRenderer) {
		mModelRenderer = modelRenderer;		
		setRenderer(mModelRenderer);
	}
	
	public void setDensity(float density) {
		mDensity = density;
	}
	
	private class GestureHandler {
		static final int NONE = 0;
		static final int DRAG = 1;		
		
		PointF mCurrentTouchPoint = new PointF();
		PointF mLastTouchPoint = new PointF();

		int mGesture = NONE;
		
		private void getPointFromEvent(PointF point, MotionEvent event) {
			point.x = event.getX();
			point.y = event.getY();
		}
		
		public void startGesture(int gesture, MotionEvent event) {
			getPointFromEvent(mLastTouchPoint, event);
			getPointFromEvent(mCurrentTouchPoint, event);
			mGesture = gesture;
		}
		
		public boolean handleGesture(MotionEvent event) {
			if (mGesture == DRAG) {
				return handleDragGesture(event);
			} else
				return false;
		}
		
		private boolean handleDragGesture(MotionEvent event) 
		{
			if (mGesture == DRAG) {
				mLastTouchPoint.x = mCurrentTouchPoint.x;
				mLastTouchPoint.y = mCurrentTouchPoint.y;
				
				getPointFromEvent(mCurrentTouchPoint, event);	
				
				mModelRenderer.performTouch(mLastTouchPoint, mCurrentTouchPoint);				
				
				return true;
			} else
				return false;
		}

		public void completeGesture(MotionEvent event) {
			getPointFromEvent(mLastTouchPoint, event);	
			mModelRenderer.completeTouch();
			mGesture = NONE;
		}
	}
	
	
	// create from XML
	public ModelGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		//setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setModelRenderer(new ModelRenderer());
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}	
	
	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		Log.i("Touch", "Touch");
		boolean retval = true;
		if (null == mGestureHandler)
			mGestureHandler = new GestureHandler();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				Log.i("Touch", "ACTION_DOWN");
				mGestureHandler.startGesture(GestureHandler.DRAG, event);
				break;
			
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				Log.i("Touch", "ACTION_UP");
				mGestureHandler.completeGesture(event);				
				break;	
				
			case MotionEvent.ACTION_MOVE:
				Log.i("Touch", "ACTION_MOVE");
				if (mGestureHandler.handleGesture(event)) 
				{	
					this.requestRender();
				} 
				break;				
		}
		
		return retval;
	}	

}
