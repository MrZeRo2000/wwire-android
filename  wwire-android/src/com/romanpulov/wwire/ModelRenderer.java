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
	
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private int[] mViewport = new int[4];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	
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
			Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
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
	
	class RevertHandler extends TouchHandler {
		@Override
		public void perform() {
			if (mActive)
				Matrix.setIdentityM(mModelMatrix, 0);
		}
	}
	
	class PanHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {
				float[] oldCoords = new float[4];
				float[] newCoords = new float[4];			
				
				GLU.gluUnProject(mOldPos.x, -mOldPos.y, 0.0f, mModelViewMatrix, 0, mProjectionMatrix, 0, mViewport, 0, oldCoords, 0);
				GLU.gluUnProject(mNewPos.x, -mNewPos.y, 0.0f,  mModelViewMatrix, 0, mProjectionMatrix, 0, mViewport, 0, newCoords, 0);
				Matrix.translateM(mModelMatrix, 0, newCoords[0]-oldCoords[0], newCoords[1]-oldCoords[1], newCoords[2]-oldCoords[2]);				
			}
		}		
	}
	
	class RotateHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {				
				Matrix.rotateM(mModelMatrix, 0, mNewPos.x - mOldPos.x, 0.0f, 0.0f, 1.0f);
				Matrix.rotateM(mModelMatrix, 0, - (mNewPos.y - mOldPos.y), 1.0f, -1.0f, 0.0f);				
			}
		}		
	}
	
	class ScaleHandler extends TouchHandler {
		
		@Override
		public void perform() {			
			if (mActive) {
				float rate = 1.0f - (mNewPos.y - mOldPos.y)/mViewport[3];
				float[] coords = new float[4];
				GLU.gluUnProject((float)mViewport[2]/2.0f, (float)mViewport[3]/2.0f, 0.0f, mModelViewMatrix, 0, mProjectionMatrix, 0, mViewport, 0, coords, 0);
				
				Matrix.translateM(mModelMatrix, 0, coords[0], coords[1], coords[2]);
				Matrix.scaleM(mModelMatrix, 0, rate, rate, rate);
				Matrix.translateM(mModelMatrix, 0, -coords[0], -coords[1], -coords[2]);
			}
		}		
	}
	
	class TouchHandlerFactory {
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
	}
	
	private void clear(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
	
		
		Matrix.setLookAtM(mViewMatrix, 0, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);		
		Matrix.setIdentityM(mModelMatrix, 0);		
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mViewport, 0);
		
		mAxes = mGLES20Primitives.new Axes();
		mAxes.initBuffers();
		mAxes.createProgram();		
		
		if (null != mModelDrawer)
			mModelDrawer.initElements();		
		
		//final float ratio = (float) width / height;
		
		if (height > width)
			Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, (float) -height / width, (float) height / width, -1.0f, 50.0f);
		else
			Matrix.orthoM(mProjectionMatrix, 0, (float) -width / height, (float) width / height, -1.0f, 1.0f, -1.0f, 50.0f);
		
		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 50.0f);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		clear(gl);		
				
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);        
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        
       
		mAxes.draw(gl, mMVPMatrix);
        
        
		if (null != mModelDrawer)
			mModelDrawer.drawElements(gl, mMVPMatrix);
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
