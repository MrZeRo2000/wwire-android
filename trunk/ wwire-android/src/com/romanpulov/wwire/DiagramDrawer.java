package com.romanpulov.wwire;

import javax.microedition.khronos.opengles.GL10;

import com.romanpulov.wwire.GLES20Primitives.GLES20Matrix;

import android.opengl.GLES20;

public class DiagramDrawer implements GLES20Primitives.ModelDrawer{
	
	public static String title = "Diagram";
	
	private DiagramData diagramData;
	
	private GLES20Primitives.Surface mSurface;
	private GLES20Primitives.Surface mWFSurface;
	boolean prepared = false;	

	@Override
	public void drawElements(GL10 gl, GLES20Matrix matrix) {
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
		mSurface.draw(gl, matrix);
		GLES20.glLineWidth(1.0f);
		mWFSurface.draw(gl, matrix);
	}

	@Override
	public void initElements(GLES20Matrix matrix) {
		// TODO Auto-generated method stub
		float pixWidth = (float) 2.0 / Math.min(matrix.viewport[3], matrix.viewport[2]);
		
		diagramData = new DiagramData(pixWidth);		 
		
		float[] vertex = diagramData.getVertex();
		float[] vertexWF = diagramData.getVertexWF();
		float[] normal = diagramData.getNormal();
		short[] indices = diagramData.getVertexIndices();
		short[] wfIndices = diagramData.getVertexWFIndices();
		
		GLES20Primitives primitives = new GLES20Primitives();
		
		mSurface = primitives.new Surface(
				vertex, 
				normal,				
				indices, 
				new float[] {0.0f, 1.0f, 0.0f},
				GLES20.GL_TRIANGLE_STRIP);
		
		mSurface.initBuffers();
		mSurface.createProgram();	
		
		mWFSurface = primitives.new Surface(
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
		// TODO Auto-generated method stub
		prepared = false;
	}
	

}
