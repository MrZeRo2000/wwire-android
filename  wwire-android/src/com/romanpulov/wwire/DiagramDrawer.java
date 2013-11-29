package com.romanpulov.wwire;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

public class DiagramDrawer implements GLES20Primitives.ModelDrawer{
	
	public static String title = "Diagram";
	
	private DiagramData diagramData;
	
	private GLES20Primitives.Surface mSurface;
	private GLES20Primitives.Surface mWFSurface;
	boolean prepared = false;

	@Override
	public void drawElements(GL10 gl, float[] mvpMatrix) {
		// TODO Auto-generated method stub
		if (!prepared) {
			initElements();
			prepared = true;			
		}
		mSurface.draw(gl, mvpMatrix);
		GLES20.glLineWidth(1.0f);
		mWFSurface.draw(gl, mvpMatrix);
	}

	@Override
	public void initElements() {
		// TODO Auto-generated method stub

		//depth test on
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        //set culling properties
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);
        
        //culling on
        GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		diagramData = new DiagramData();
		float[] vertex = diagramData.getVertex();
		float[] vertexWF = diagramData.getVertexWF();
		float[] normal = diagramData.getNormal();
		short[] indices = diagramData.getVertexIndices();
		
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
				//indices,
				new short[] {1, 13, 25, 37, 49, 61, 11, 23, 35, 47, 59, 71, 1, 2, 14, 26},
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
