package com.romanpulov.wwire.gles;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;

import android.graphics.PointF;
import android.opengl.GLU;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

public class ModelRenderer implements GLSurfaceView.Renderer {
    // primitive objects
    private GLES20Primitives.Axes mAxes;
    //drawer
    private GLES20Primitives.ModelDrawer mModelDrawer;

    private final TouchHandlerFactory mTouchHandlerFactory;

    private int mHandlerMode;
    private float mDensity;

    private final GLES20Primitives.GLES20Matrix mMatrix;

    public void setModelDrawer(GLES20Primitives.ModelDrawer modelDrawer) {
        mModelDrawer = modelDrawer;
    }

    public GLES20Primitives.ModelDrawer getModelDrawer() {
        return mModelDrawer;
    }

    public void setHandlerMode(int handlerMode) {
        mHandlerMode = handlerMode;
        if (mHandlerMode == TouchHandlerFactory.MODE_REVERT) {
            performRevert();
        }
    }

    public void setDensity(float density) {
        mDensity = density;
    }

    public void saveHandlerState(Bundle state) {
        mTouchHandlerFactory.saveState(state);
    }

    public void loadHandlerState(Bundle state) {
        mTouchHandlerFactory.loadState(state);
    }

    private abstract class TouchHandler {

        private boolean mIsDirty = false;

        void setDirty() {
            mIsDirty = true;
        }

        boolean getDirty() {
            return mIsDirty;
        }

        private PointF mOldPos;
        private PointF mNewPos;

        PointF getOldPos() {
            return mOldPos;
        }

        PointF getNewPos() {
            return mNewPos;
        }

        TouchHandler() {

        }

        void updatePos(PointF oldPos, PointF newPos) {
            mOldPos = oldPos;
            mNewPos = newPos;
        }

        void init() {
            mIsDirty = false;
        }

        protected abstract void perform();

        protected abstract void apply();

        // state routines
        protected abstract void saveState(Bundle state);
        protected abstract void loadState(Bundle state);
    }

    private class PanHandler extends TouchHandler {

        private float mOffsetX;
        private float mOffsetY;

        @Override
        public void init() {
            super.init();
            mOffsetX = mOffsetY = 0.0f;
        }

        @Override
        public void perform() {
            mOffsetX += getNewPos().x - getOldPos().x;
            mOffsetY += - (getNewPos().y - getOldPos().y);
        }

        @Override
        public void apply() {
            // calc modelview
            mMatrix.calcModelViewMatrix();

            // determine offset
            float[] oldCoords = new float[4];
            float[] newCoords = new float[4];
            GLU.gluUnProject(0.0f, 0.0f, 0.0f, mMatrix.modelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, oldCoords, 0);
            GLU.gluUnProject(mOffsetX, mOffsetY, 0.0f, mMatrix.modelViewMatrix, 0, mMatrix.projection, 0, mMatrix.viewport, 0, newCoords, 0);

            // translate
            Matrix.translateM(mMatrix.model, 0, newCoords[0] - oldCoords[0], newCoords[1] - oldCoords[1], newCoords[2] - oldCoords[2]);
            //Log.d(getClass().toString(), "apply, offsetX = " + String.valueOf(mOffsetX) + ", offsetY = " + String.valueOf(mOffsetY));
        }

        // state routines
        @Override
        public void saveState(Bundle state) {
            state.putFloat(this.getClass().toString() + "_offsetX", mOffsetX);
            state.putFloat(this.getClass().toString() + "_offsetY", mOffsetY);
        }

        @Override
        public void loadState(Bundle state) {
            mOffsetX = state.getFloat(this.getClass().toString() + "_offsetX", 0.0f);
            mOffsetY = state.getFloat(this.getClass().toString() + "_offsetY", 0.0f);
        }
    }

    private class RotateHandler extends TouchHandler {
        // rotation support
        private final float[] accumulatedRotation = new float[16];

        private float mRotateX;
        private float mRotateY;

        @Override
        public void init() {
            //Log.d(getClass().toString(), "init accumulated rotation");
            super.init();
            mRotateX = mRotateY = 0.0f;
            Matrix.setIdentityM(accumulatedRotation, 0);
        }

        @Override
        public void perform() {
            // calc rotation offset
            mRotateX = (getNewPos().x - getOldPos().x) / mDensity / 2.0f;
            mRotateY = - (getNewPos().y - getOldPos().y) / mDensity / 2.0f;
        }

        @Override
        public void apply() {
            //Log.d(getClass().toString(), "apply rotation");
            // rotate current rotation
            final float[] currentRotation = new float[16];
            Matrix.setIdentityM(currentRotation, 0);
            Matrix.rotateM(currentRotation, 0, mRotateX, 0.0f, 0.0f, 1.0f);
            Matrix.rotateM(currentRotation, 0, mRotateY, 1.0f, -1.0f, 0.0f);

            final float[] temp = new float[16];
            // multiply the current rotation by the accumulated rotation, and then set the accumulated
            // rotation to the result.
            Matrix.multiplyMM(temp, 0, currentRotation, 0, accumulatedRotation, 0);
            System.arraycopy(temp, 0, accumulatedRotation, 0, 16);

            // rotate the model taking the overall rotation into account.
            Matrix.multiplyMM(temp, 0, mMatrix.model, 0, accumulatedRotation, 0);
            System.arraycopy(temp, 0, mMatrix.model, 0, 16);

            // rotate the normal taking the overall rotation into account.
            Matrix.multiplyMM(temp, 0, mMatrix.normal, 0, accumulatedRotation, 0);
            System.arraycopy(temp, 0, mMatrix.normal, 0, 16);

            //reset rotate
            mRotateX = mRotateY = 0.0f;
        }

        // state routines
        @Override
        public void saveState(Bundle state) {
            state.putFloatArray(this.getClass().toString() + "_accumulatedRotation", accumulatedRotation);
        }

        @Override
        public void loadState(Bundle state) {
            float[] tempAccumulatedRotation = state.getFloatArray(this.getClass().toString() + "_accumulatedRotation");
            if (null != tempAccumulatedRotation) {
                //Log.d(getClass().toString(), "load accumulated rotation");
                System.arraycopy(tempAccumulatedRotation, 0, accumulatedRotation, 0, 16);
            }
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
            //Matrix.translateM(mMatrix.model, 0, coords[0], coords[1], coords[2]);
            Matrix.scaleM(mMatrix.model, 0, rate, rate, rate);
            //Matrix.translateM(mMatrix.model, 0, -coords[0], -coords[1], -coords[2]);
        }

        // state routines
        @Override
        public void saveState(Bundle state) {
            state.putFloat(this.getClass().toString() + "_scaleFactor", mScaleFactor);
        }

        @Override
        public void loadState(Bundle state) {
            mScaleFactor = state.getFloat(this.getClass().toString() + "_scaleFactor", 0.0f);
        }
    }

    private class TouchHandlerFactory {
        static final int MODE_REVERT = 1;
        static final int MODE_PAN = 2;
        static final int MODE_ROTATE = 3;
        static final int MODE_SCALE = 4;

        private static final int MAX_MODE = 4;

        private final Map<Integer, TouchHandler> mHandlers = new HashMap<>();

        TouchHandler getHandler(int handlerMode, boolean createIfNotExists) {
            if (mHandlers.containsKey(handlerMode)) {
                return mHandlers.get(handlerMode);
            } else {
                if (createIfNotExists) {
                    final TouchHandler newHandler;

                    switch (handlerMode) {
                    case MODE_PAN:
                        newHandler = new PanHandler();
                        break;
                    case MODE_ROTATE:
                        newHandler = new RotateHandler();
                        break;
                    case MODE_SCALE:
                        newHandler = new ScaleHandler();
                        break;
                    default:
                        return null;
                    }

                    mHandlers.put(handlerMode, newHandler);
                    newHandler.init();

                    return newHandler;

                } else
                    return null;
            }
        }

        void applyTransform() {
            // init matrix
            mMatrix.initModel();

            // calc modelview
            mMatrix.calcModelViewMatrix();

            // apply transformation for all dirty handlers
            for (TouchHandler value : mHandlers.values()){
                if (value.getDirty())
                    value.apply();
            }
        }

        void initHandlers() {
            for (TouchHandler value : mHandlers.values()) {
                value.init();
            }
        }

        // state routines
        void saveState(Bundle state) {
            for (int i = 1; i <= MAX_MODE; i ++) {
                TouchHandler touchHandler = getHandler(i, false);
                if ((null != touchHandler) && (touchHandler.getDirty())) {
                    state.putBoolean(this.getClass().toString() + i, true);
                    touchHandler.saveState(state);
                }
            }
        }

        void loadState(Bundle state) {
            for (int i = 1; i <= MAX_MODE; i ++) {
                final boolean handlerExists = state.getBoolean(this.getClass().toString() + i, false);
                if (handlerExists) {
                    TouchHandler touchHandler = getHandler(i, true);
                    touchHandler.loadState(state);
                    touchHandler.setDirty();
                }
            }
        }
    }

    public ModelRenderer() {
        mTouchHandlerFactory = new TouchHandlerFactory();
        mMatrix = new GLES20Primitives.GLES20Matrix();
    }

    private void clear() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // clear color
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        // set lookat
        Matrix.setLookAtM(mMatrix.view, 0, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // init matrix
        mMatrix.initModel();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // get viewport parameters
        GLES20.glViewport(0, 0, width, height);
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mMatrix.viewport, 0);

        // create axes
        mAxes = new GLES20Primitives.Axes();
        mAxes.initBuffers();
        mAxes.createProgram();

        // assign default drawer
        if (null != mModelDrawer)
            mModelDrawer.initElements(mMatrix);

        // set up projection depending on orientation
        if (height > width)
            Matrix.orthoM(mMatrix.projection, 0, -1.0f, 1.0f, (float) -height / width, (float) height / width, -1.0f, 50.0f);
        else
            Matrix.orthoM(mMatrix.projection, 0, (float) -width / height, (float) width / height, -1.0f, 1.0f, -1.0f, 50.0f);

        // init matrix
        mMatrix.initModel();
        mTouchHandlerFactory.applyTransform();
        //Log.d("OnSurfaceChanged", "ApplyTransform");

        // another option
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 50.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear buffer
        clear();

        // calc ModelViewProjection
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.view, 0, mMatrix.model, 0);        
        Matrix.multiplyMM(mMatrix.mvp, 0, mMatrix.projection, 0, mMatrix.mvp, 0);
       
        // draw axes
        mAxes.draw(mMatrix);
        
        // draw scene elements
        if (null != mModelDrawer)
            mModelDrawer.drawElements(mMatrix);
    }

    public void performRevert() {
        // init matrix
        mMatrix.initModel();
        // reset handlers
        mTouchHandlerFactory.initHandlers();
    }

    public final void performTouch(PointF oldPos, PointF newPos) {
        //Log.i("Coords", String.valueOf(oldPos.x) + "/" + String.valueOf(oldPos.y) + "/" + String.valueOf(newPos.x) + "/" + String.valueOf(newPos.y));

        if (TouchHandlerFactory.MODE_REVERT == mHandlerMode) {
            performRevert();
        } else {
            final TouchHandler mTouchHandler = mTouchHandlerFactory.getHandler(mHandlerMode, true);
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
