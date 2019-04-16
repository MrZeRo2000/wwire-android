package com.romanpulov.wwire.gles;

import javax.microedition.khronos.opengles.GL10;

import com.romanpulov.wwire.gles.GLES20Primitives;
import com.romanpulov.wwire.gles.GLES20Primitives.GLES20Matrix;
import com.romanpulov.wwire.model.WWireData;

import android.opengl.GLES20;

public class ElementsDrawer implements GLES20Primitives.ModelDrawer {

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

    @Override
    public void invalidate() {
        prepared = false;
    }

    @Override
    public void initElements(GLES20Matrix matrix) {
        float[] segmentsData = WWireData.getInstance().getSegments();
        float[] sourcesData = WWireData.getInstance().getSources();
        if (null != segmentsData) {
            float maxVal = 0.0f;
            for (float sd :segmentsData) {
                float f = Math.abs(sd);
                if (f > maxVal)
                    maxVal = f;
            }

            for (float sd : sourcesData) {
                float f = Math.abs(sd);
                if (f > maxVal)
                    maxVal = f;
            }

            if (maxVal>0.0f) {
                for (int i=0; i<segmentsData.length; i++) {
                    segmentsData[i] = segmentsData[i]/maxVal;
                }

                for (int i=0; i<sourcesData.length; i++) {
                    sourcesData[i] = sourcesData[i]/maxVal;
                }
            }

            mSegments = new GLES20Primitives.Line(
                segmentsData,
                new float[] {0.8f, 0.8f, 0.8f}
            );
            mSegments.initBuffers();
            mSegments.createProgram();

            if (sourcesData.length>0) {
                mSources = new GLES20Primitives.Line(
                    sourcesData,
                    new float[] {0.698f, 0.2f, 0.2f}
                );
                mSources.initBuffers();
                mSources.createProgram();
            }
        }
    }
}
