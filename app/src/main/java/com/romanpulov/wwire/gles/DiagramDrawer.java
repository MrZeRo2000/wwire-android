package com.romanpulov.wwire.gles;

import com.romanpulov.wwire.gles.GLES20Primitives;
import com.romanpulov.wwire.gles.GLES20Primitives.GLES20Matrix;
import com.romanpulov.wwire.model.DiagramData;
import com.romanpulov.wwire.model.WWireData;

import android.opengl.GLES20;

public class DiagramDrawer implements GLES20Primitives.ModelDrawer{
    private final WWireData mData;

    private GLES20Primitives.Surface mSurface;
    private GLES20Primitives.Surface mWFSurface;
    private boolean prepared = false;

    DiagramDrawer(WWireData data) {
        mData = data;
    }

    @Override
    public void drawElements(GLES20Matrix matrix) {
        //depth test on
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        //set culling properties
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);
        
        //culling on
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        if (!prepared) {
            initElements(matrix);
            prepared = true;
        }
        mSurface.draw(matrix);
        GLES20.glLineWidth(1.0f);
        mWFSurface.draw(matrix);
    }

    @Override
    public void initElements(GLES20Matrix matrix) {
        float pixWidth = (float) 2.0 / Math.min(matrix.viewport[3], matrix.viewport[2]);

        DiagramData diagramData = new DiagramData(mData, pixWidth);

        float[] vertex = diagramData.getVertex();
        float[] vertexWF = diagramData.getVertexWF();
        float[] normal = diagramData.getNormal();
        short[] indices = diagramData.getVertexIndices();
        short[] wfIndices = diagramData.getVertexWFIndices();

        mSurface = new GLES20Primitives.Surface(
            vertex,
            normal,
            indices,
            new float[] {0.0f, 1.0f, 0.0f},
            GLES20.GL_TRIANGLE_STRIP);

        mSurface.initBuffers();
        mSurface.createProgram();

        mWFSurface = new GLES20Primitives.Surface(
            vertexWF,
            normal,
            wfIndices,
            new float[] {0.0f, 0.5f, 0.0f},
            GLES20.GL_LINE_STRIP);

        mWFSurface.initBuffers();
        mWFSurface.createProgram();
    }

    @Override
    public void invalidate() {
        prepared = false;
    }
}
