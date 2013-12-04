package com.romanpulov.wwire;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;

import android.graphics.PointF;
import android.opengl.GLU;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class ModelRenderer implements GLSurfaceView.Renderer {
	GLES20Primitives mGLES20Primitives;
	GLES20Primitives.Axes mAxes;
	OGLPrimitives.Cube mCube;
	
	GLES20Primitives.ModelDrawer mModelDrawer;
	
	TouchHandlerFactory mTouchHandlerFactory;
	TouchHandler mTouchHandler;
	int mHandlerMode;	
	
	GLES20Primitives.GLES20Matrix mMatrix;
	/*
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private int[] mViewport = new int[4];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	*/
	public void setModelDrawer(GLES20Primitives.ModelDrawer modelDrawer) {
		mModelDrawer = modelDrawer;		
	}
	
	public GLES20Primitives.ModelDrawer getModelDrawer() {
		return mModelDrawer;
	}
	
	public void setHandlerMode(int handlerMode) {
		mHandlerMode = handlerMode;
	}
	
	private abstract class TouchHandler {
		protected boolean mActive = false;
		
		protected PointF mOldPos;
		protected PointF mNewPos;
		
		protected float[] mModelViewMatrix = new float[16];
		
		public boolean getActive() {
			return mActive;
		}
		
		public TouchHandler() {
			
		}
		
		public void activate() {
			Log.d("TouchHandler", "Activate");
			mActive = true;
			Matrix.multiplyMM(mModelViewMatrix, 0, mMatrix.view, 0, mMatrix.model, 0);
		}
		
		public void updatePos(PointF oldPos, PointF newPos) {
			Log.d("TouchHandler", "UpdatePos");
			mOldPos = oldPos;
			mNewPos = newPos;
			perform();
		}
		
		public void deactivate() {
			Log.d("TouchHandler", "Deactivate");
			mActive = false;
		}
		
		public abstract void perform();		
	}
	
	private class RevertHandler extends TouchHandler {
		@Override
		public void perform() {
			if (mActive) {
				Matrix.setIdentityM(mMatrix.model, 0);
				Matrix.setIdentityM(mMatrix.normal, 0);
			}
		}
	}
	
	private class PanHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {
				float[] oldCoords = new float[4];
				float[] newCoords = new float[4];			
				
				GLU.gluUnProject(mOldPos.x, -mOldPos.y, 0.0f, mModelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, oldCoords, 0);
				GLU.gluUnProject(mNewPos.x, -mNewPos.y, 0.0f,  mModelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, newCoords, 0);
				Matrix.translateM(mMatrix.model, 0, newCoords[0]-oldCoords[0], newCoords[1]-oldCoords[1], newCoords[2]-oldCoords[2]);				
			}
		}		
	}
	
	private class RotateHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {				
				Matrix.rotateM(mMatrix.model, 0, mNewPos.x - mOldPos.x, 0.0f, 0.0f, 1.0f);
				Matrix.rotateM(mMatrix.model, 0, - (mNewPos.y - mOldPos.y), 1.0f, -1.0f, 0.0f);				
				Matrix.rotateM(mMatrix.normal, 0, mNewPos.x - mOldPos.x, 0.0f, 0.0f, 1.0f);
				Matrix.rotateM(mMatrix.normal, 0, - (mNewPos.y - mOldPos.y), 1.0f, -1.0f, 0.0f);
			}
		}		
	}
	
	private class ScaleHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {
				float rate = 1.0f - (mNewPos.y - mOldPos.y)/mMatrix.viewport[3];
				float[] coords = new float[4];
				GLU.gluUnProject((float)mMatrix.viewport[2]/2.0f, (float)mMatrix.viewport[3]/2.0f, 0.0f, 
						mModelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, coords, 0);
				
				Matrix.translateM(mMatrix.model, 0, coords[0], coords[1], coords[2]);
				Matrix.scaleM(mMatrix.model, 0, rate, rate, rate);
				Matrix.translateM(mMatrix.model, 0, -coords[0], -coords[1], -coords[2]);
			}
		}		
	}
	
	private class TouchHandlerFactory {
		public static final int MODE_REVERT = 1;
		public static final int MODE_PAN = 2;
		public static final int MODE_ROTATE = 3;
		public static final int MODE_SCALE = 4;
		
		private RevertHandler mRevertHandler;
		private PanHandler mPanHandler;
		private RotateHandler mRotateHandler;
		private ScaleHandler mScaleHandler;
		
		public TouchHandler getHandler(int handlerMode) {
			switch (handlerMode) {
			case MODE_REVERT:
				if (null == mRevertHandler)
					mRevertHandler = new RevertHandler();
				return mRevertHandler;
			case MODE_PAN:
				if (null == mPanHandler)
					mPanHandler = new PanHandler();
				return mPanHandler;
			case MODE_ROTATE:
				if (null == mRotateHandler)
					mRotateHandler = new RotateHandler();
				return mRotateHandler;
			case MODE_SCALE:
				if (null == mScaleHandler)
					mScaleHandler = new ScaleHandler();
				return mScaleHandler;
			default:
				return null;					
			}
		}
	}
	
	public ModelRenderer() {
		mGLES20Primitives = new GLES20Primitives();
		mTouchHandlerFactory = new TouchHandlerFactory();
		mMatrix = mGLES20Primitives.new GLES20Matrix(); 
	}
	
	private void clear(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		Matrix.setLookAtM(mMatrix.view, 0, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		//Matrix.setIdentityM(mViewMatrix, 0);
		Matrix.setIdentityM(mMatrix.model, 0);		
		Matrix.setIdentityM(mMatrix.normal, 0);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mMatrix.viewport, 0);
		
		mAxes = mGLES20Primitives.new Axes();
		mAxes.initBuffers();
		mAxes.createProgram();		
		
		if (null != mModelDrawer)
			mModelDrawer.initElements(mMatrix);		
		
		if (height > width)
			Matrix.orthoM(mMatrix.projection, 0, -1.0f, 1.0f, (float) -height / width, (float) height / width, -1.0f, 50.0f);
		else
			Matrix.orthoM(mMatrix.projection, 0, (float) -width / height, (float) width / height, -1.0f, 1.0f, -1.0f, 50.0f);
		
		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 50.0f);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		clear(gl);		
				
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.view, 0, mMatrix.model, 0);        
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.projection, 0, mMatrix.mvp, 0);
       
		mAxes.draw(gl, mMatrix);
        
		if (null != mModelDrawer)
			mModelDrawer.drawElements(gl, mMatrix);
		else
			Log.d("Draw", "Drawer not assigned");		
        		
		Log.i("Draw", "OnDraw");
	}
	
	public final void performTouch(PointF oldPos, PointF newPos) {
		Log.i("Coords", String.valueOf(oldPos.x) + "/" + String.valueOf(oldPos.y) + "/" + String.valueOf(newPos.x) + "/" + String.valueOf(newPos.x));
		
		mTouchHandler = mTouchHandlerFactory.getHandler(mHandlerMode);
		if (null != mTouchHandler) {
			if (!mTouchHandler.getActive())
				mTouchHandler.activate();
			mTouchHandler.updatePos(oldPos, newPos);			
		}
	}
	
	public final void completeTouch() {
		if (null != mTouchHandler)
			mTouchHandler.deactivate();
	}
	
	
}
