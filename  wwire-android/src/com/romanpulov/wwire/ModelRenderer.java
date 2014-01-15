package com.romanpulov.wwire;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.opengl.GLU;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class ModelRenderer implements GLSurfaceView.Renderer {
	// primitive objects
	GLES20Primitives mGLES20Primitives;
	GLES20Primitives.Axes mAxes;
	OGLPrimitives.Cube mCube;
	//drawer
	GLES20Primitives.ModelDrawer mModelDrawer;
	
	TouchHandlerFactory mTouchHandlerFactory;
	
	int mHandlerMode;
	float mDensity;
	
	GLES20Primitives.GLES20Matrix mMatrix;
	
	public void setModelDrawer(GLES20Primitives.ModelDrawer modelDrawer) {
		mModelDrawer = modelDrawer;		
	}
	
	public GLES20Primitives.ModelDrawer getModelDrawer() {
		return mModelDrawer;
	}
	
	public void setHandlerMode(int handlerMode) {
		mHandlerMode = handlerMode;
	}
	
	public void setDensity(float density) {
		mDensity = density;
	}
	
	private abstract class TouchHandler {
		
		private boolean mIsDirty = false;
		
		protected void setDirty() {
			mIsDirty = true;
		}
		
		public boolean getDirty() {
			return mIsDirty;
		}
		
		private PointF mOldPos;
		private PointF mNewPos;		
		
		public PointF getOldPos() {
			return mOldPos;
		}
		
		public PointF getNewPos() {
			return mNewPos;
		}
		
		public TouchHandler() {
			
		}
		
		public void updatePos(PointF oldPos, PointF newPos) {
			Log.d("TouchHandler", "UpdatePos");
			mOldPos = oldPos;
			mNewPos = newPos;			
		}
		
		public abstract void perform();
		
		public void init() {
			mIsDirty = false;
		}
		
		public abstract void apply();
		
	}
	
	private class PanHandler extends TouchHandler {		

		private float offsetX;
		private float offsetY;
		
		@Override
		public void init() {
			super.init();
			offsetX = offsetY = 0.0f;
		}
		
		@Override
		public void perform() {			
			offsetX += getNewPos().x - getOldPos().x;
			offsetY += - (getNewPos().y - getOldPos().y);			
		}
		
		@Override
		public void apply() {						
			// calc modelview
			mMatrix.calcModelViewMatrix();
			
			// determine offset
			float[] oldCoords = new float[4];
			float[] newCoords = new float[4];
			GLU.gluUnProject(0.0f, 0.0f, 0.0f, mMatrix.modelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, oldCoords, 0);
			GLU.gluUnProject(offsetX, offsetY, 0.0f, mMatrix.modelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, newCoords, 0);
			
			// translate
			Matrix.translateM(mMatrix.model, 0, newCoords[0] - oldCoords[0], newCoords[1] - oldCoords[1], newCoords[2] - oldCoords[2]);		

		}
		
	}
	
	private class RotateHandler extends TouchHandler {
		
		private float mRotateX;
		private float mRotateY;
		
		@Override
		public void init() {
			super.init();
			mRotateX = mRotateY = 0.0f;
		}
		
		@Override
		public void perform() {			
			// calc rotation offset
			mRotateX = (getNewPos().x - getOldPos().x) / mDensity / 2.0f;;
			mRotateY = - (getNewPos().y - getOldPos().y) / mDensity / 2.0f;
		}
		
		@Override
		public void apply() {
			// rotate current rotation				
			Matrix.setIdentityM(mMatrix.currentRotation, 0);
			Matrix.rotateM(mMatrix.currentRotation, 0, mRotateX, 0.0f, 0.0f, 1.0f);
			Matrix.rotateM(mMatrix.currentRotation, 0, mRotateY, 1.0f, -1.0f, 0.0f);
			
			// multiply the current rotation by the accumulated rotation, and then set the accumulated
			// rotation to the result.
			Matrix.multiplyMM(mMatrix.temp, 0, mMatrix.currentRotation, 0, mMatrix.accumulatedRotation, 0);
			System.arraycopy(mMatrix.temp, 0, mMatrix.accumulatedRotation, 0, 16);
			
			// rotate the model taking the overall rotation into account.				
			Matrix.multiplyMM(mMatrix.temp, 0, mMatrix.model, 0, mMatrix.accumulatedRotation, 0);
			System.arraycopy(mMatrix.temp, 0, mMatrix.model, 0, 16);
			
			// rotate the normal taking the overall rotation into account.
			Matrix.multiplyMM(mMatrix.temp, 0, mMatrix.normal, 0, mMatrix.accumulatedRotation, 0);
			System.arraycopy(mMatrix.temp, 0, mMatrix.normal, 0, 16);
			
			//reset rotate
			mRotateX = mRotateY = 0.0f;
		}
	}
	
	private class ScaleHandler extends TouchHandler {
		
		private float mScaleFactor;
		
		@Override
		public void init() {
			super.init();
			mScaleFactor = 0.0f;
		}
		
		@Override
		public void perform() {			
			mScaleFactor += (getNewPos().y - getOldPos().y);				
		}	
		
		@Override
		public void apply() {
			// apply scale		
			float rate = 1.0f - mScaleFactor / mMatrix.viewport[3];
			float[] coords = new float[4];
			GLU.gluUnProject((float)mMatrix.viewport[2]/2.0f, (float)mMatrix.viewport[3]/2.0f, 0.0f, 
					mMatrix.modelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, coords, 0);				
			Matrix.translateM(mMatrix.model, 0, coords[0], coords[1], coords[2]);
			Matrix.scaleM(mMatrix.model, 0, rate, rate, rate);
			Matrix.translateM(mMatrix.model, 0, -coords[0], -coords[1], -coords[2]);
		}
	}
	
	private class TouchHandlerFactory {
		public static final int MODE_REVERT = 1;
		public static final int MODE_PAN = 2;
		public static final int MODE_ROTATE = 3;
		public static final int MODE_SCALE = 4;
		
		@SuppressLint("UseSparseArrays")
		private Map<Integer, TouchHandler> mHandlers = new HashMap<Integer, TouchHandler>();
		
		public TouchHandler getHandler(int handlerMode) {
			final String tag = "getHandler"; 
			
			if (mHandlers.containsKey(handlerMode)) {
				Log.d(tag, "handler exists");
				return mHandlers.get(handlerMode);
			} else {
				Log.d(tag, "new handler");
				switch (handlerMode) {
				case MODE_PAN:
					mHandlers.put(handlerMode, new PanHandler());
					return mHandlers.get(handlerMode);
				case MODE_ROTATE:					
					mHandlers.put(handlerMode, new RotateHandler());
					return mHandlers.get(handlerMode);
				case MODE_SCALE:
					mHandlers.put(handlerMode, new ScaleHandler());
					return mHandlers.get(handlerMode);
				default:
					return null;					
				}
			}
		}
		
		public void applyTransform() {
			// init matrix
			mMatrix.initModel();
			
			// calc modelview
			mMatrix.calcModelViewMatrix();
			
			// apply transformation for all dirty handlers
			for (Map.Entry<Integer, TouchHandler> entry : mHandlers.entrySet()) {
				if (entry.getValue().getDirty())
					entry.getValue().apply();
			}
		}
		
		public void initHandlers() {
			for (Map.Entry<Integer, TouchHandler> entry : mHandlers.entrySet()) {
				entry.getValue().init();
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
		// clear color
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		// set lookat
		Matrix.setLookAtM(mMatrix.view, 0, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		// init matrix
		mMatrix.init();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// get viewport parameters
		GLES20.glViewport(0, 0, width, height);
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mMatrix.viewport, 0);
		
		// create axes
		mAxes = mGLES20Primitives.new Axes();
		mAxes.initBuffers();
		mAxes.createProgram();		
		
		// assign default drawer
		if (null != mModelDrawer)
			mModelDrawer.initElements(mMatrix);		
		
		// set up projection depending on orientatino
		if (height > width)
			Matrix.orthoM(mMatrix.projection, 0, -1.0f, 1.0f, (float) -height / width, (float) height / width, -1.0f, 50.0f);
		else
			Matrix.orthoM(mMatrix.projection, 0, (float) -width / height, (float) width / height, -1.0f, 1.0f, -1.0f, 50.0f);
		
		// another option
		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 50.0f);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// clear buffer
		clear(gl);		
		
		// calc ModelViewProjection
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.view, 0, mMatrix.model, 0);        
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.projection, 0, mMatrix.mvp, 0);
       
        // draw axes
		mAxes.draw(gl, mMatrix);
        
		// draw scene elements
		if (null != mModelDrawer)
			mModelDrawer.drawElements(gl, mMatrix);
		else
			Log.d("Draw", "Drawer not assigned");		
        		
		Log.i("Draw", "OnDraw");
	}
	
	public final void performTouch(PointF oldPos, PointF newPos) {
		Log.i("Coords", String.valueOf(oldPos.x) + "/" + String.valueOf(oldPos.y) + "/" + String.valueOf(newPos.x) + "/" + String.valueOf(newPos.y));
		
		if (TouchHandlerFactory.MODE_REVERT == mHandlerMode) {
			mMatrix.init();
			mTouchHandlerFactory.initHandlers();
		} else {		
			TouchHandler mTouchHandler = mTouchHandlerFactory.getHandler(mHandlerMode);
			if (null != mTouchHandler) {
				// save position in handler
				mTouchHandler.updatePos(oldPos, newPos);
				// perform actions
				mTouchHandler.perform();
				// update handler as dirty for subsequent update
				mTouchHandler.setDirty();
				// apply transformations for all dirty handlers
				mTouchHandlerFactory.applyTransform();
			}
		}
	}
	
	public final void completeTouch() {
		// nothing to do to complete touch event, maybe later
	}
	
}
