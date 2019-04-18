package com.romanpulov.wwire.gles;

import javax.microedition.khronos.opengles.GL10;

import com.romanpulov.wwire.gles.GLES20Primitives.GLES20Matrix;
import com.romanpulov.wwire.model.WWireData;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

import java.util.Arrays;

public class ElementsDrawer implements GLES20Primitives.ModelDrawer {

    private float[] mSegmentsData;
    private float[] mSourcesData;

    public static String title = "Model";

    GLES20Primitives.Line mSegments;
    GLES20Primitives.Line mSources;
    boolean prepared = false;

    @Override
    public void drawElements(GL10 gl, GLES20Matrix matrix) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        if (!prepared) {
            initElements(matrix);
            if (null != mSegments)
                prepared = true;
        }
        if (prepared && (null != mSegments)) {
            mSegments.draw(gl, matrix);
            if (null != mSources) {
                mSources.draw(gl, matrix);
            }
        }
    }

    ElementsDrawer(@NonNull WWireData data) {
        if ((data.getSegments() != null) && (data.getSources() != null)) {
            mSegmentsData = Arrays.copyOf(data.getSegments(), data.getSegments().length);
            mSourcesData = Arrays.copyOf(data.getSources(), data.getSources().length);
        }
    }

    @Override
    public void invalidate() {
        prepared = false;
    }

    @Override
    public void initElements(GLES20Matrix matrix) {
        if (null != mSegmentsData) {
            float maxVal = 0.0f;
            for (float sd :mSegmentsData) {
                float f = Math.abs(sd);
                if (f > maxVal)
                    maxVal = f;
            }

            for (float sd : mSourcesData) {
                float f = Math.abs(sd);
                if (f > maxVal)
                    maxVal = f;
            }

            if (maxVal>0.0f) {
                for (int i=0; i<mSegmentsData.length; i++) {
                    mSegmentsData[i] = mSegmentsData[i]/maxVal;
                }

                for (int i=0; i<mSourcesData.length; i++) {
                    mSourcesData[i] = mSourcesData[i]/maxVal;
                }
            }

            mSegments = new GLES20Primitives.Line(
                mSegmentsData,
                new float[] {0.8f, 0.8f, 0.8f}
            );
            mSegments.initBuffers();
            mSegments.createProgram();

            if (mSourcesData.length>0) {
                mSources = new GLES20Primitives.Line(
                    mSourcesData,
                    new float[] {0.698f, 0.2f, 0.2f}
                );
                mSources.initBuffers();
                mSources.createProgram();
            }
        }
    }
}
