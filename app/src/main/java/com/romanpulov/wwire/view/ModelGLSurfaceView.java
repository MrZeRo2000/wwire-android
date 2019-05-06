package com.romanpulov.wwire.view;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.romanpulov.wwire.gles.ModelRenderer;

public class ModelGLSurfaceView extends GLSurfaceView {
	
	private ModelRenderer mModelRenderer;
	private GestureHandler mGestureHandler;
	
	public ModelRenderer getModelRenderer() {
		return mModelRenderer;
	}
	
	private void setModelRenderer(ModelRenderer modelRenderer) {
		mModelRenderer = modelRenderer;
        //added to suppress OpenGL onfig error message
        //super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
		setRenderer(mModelRenderer);		
	}
	
	public void setDensity(float density) {
		mModelRenderer.setDensity(density);		
	}
	
	private class GestureHandler {
		static final int NONE = 0;
		static final int DRAG = 1;		
		
		final PointF mCurrentTouchPoint = new PointF();
		final PointF mLastTouchPoint = new PointF();

		int mGesture = NONE;
		
		private void getPointFromEvent(PointF point, MotionEvent event) {
			point.x = event.getX();
			point.y = event.getY();
		}
		
		void startGesture(int gesture, MotionEvent event) {
			getPointFromEvent(mLastTouchPoint, event);
			getPointFromEvent(mCurrentTouchPoint, event);
			mGesture = gesture;
		}
		
		boolean handleGesture(MotionEvent event) {
			if (mGesture == DRAG) {
				return handleDragGesture(event);
			} else
				return false;
		}
		
		private boolean handleDragGesture(MotionEvent event) 
		{
			if (mGesture == DRAG) {				
				getPointFromEvent(mCurrentTouchPoint, event);	
				
				mModelRenderer.performTouch(mLastTouchPoint, mCurrentTouchPoint);
				mLastTouchPoint.x = mCurrentTouchPoint.x;
				mLastTouchPoint.y = mCurrentTouchPoint.y;
				
				return true;
			} else
				return false;
		}

		void completeGesture(MotionEvent event) {
			getPointFromEvent(mLastTouchPoint, event);	
			mModelRenderer.completeTouch();
			mGesture = NONE;
		}
	}
	
	// create from XML
	public ModelGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setModelRenderer(new ModelRenderer());
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);		
	}	
	
	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (null == mGestureHandler)
			mGestureHandler = new GestureHandler();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			    performClick();
				mGestureHandler.startGesture(GestureHandler.DRAG, event);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mGestureHandler.completeGesture(event);
				break;	
			case MotionEvent.ACTION_MOVE:
				if (mGestureHandler.handleGesture(event))
                    this.requestRender();
				break;
		}
		
		return true;
	}
}
