package com.romanpulov.wwire;

import javax.microedition.khronos.opengles.GL10;

import com.romanpulov.wwire.GLES20Primitives.GLES20Matrix;

import android.opengl.GLES20;

public class ElementsDrawer extends BaseDrawer implements GLES20Primitives.ModelDrawer {
	
	public static String title = "Model";
	
	GLES20Primitives.Line mSegments;
	GLES20Primitives.Line mSources;
	boolean prepared = false;
	
	public ElementsDrawer(float density) {
		super(density);
	}

	@Override
	public void drawElements(GL10 gl, GLES20Matrix matrix) {
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		if (!prepared) {
			initElements(matrix);
			if (null != mSegments)
				prepared = true;
		}
		if (prepared) {	
			mSegments.draw(gl, matrix);
			if (null != mSources) {
				mSources.draw(gl, matrix);
			}
		}
	}
	
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		prepared = false;
	}

	@Override
	public void initElements(GLES20Matrix matrix) {
		// TODO Auto-generated method stub	
		
		
		float[] segmentsData = WWireData.getInstance().getSegments();
		float[] sourcesData = WWireData.getInstance().getSources();
		if (null != segmentsData) {
			float maxVal = 0.0f;
			for (int i=0; i<segmentsData.length; i++) {
				float f = Math.abs(segmentsData[i]);
				if (f>maxVal)
					maxVal = f; 
			}			

			for (int i=0; i<sourcesData.length; i++) {
				float f = Math.abs(sourcesData[i]);
				if (f>maxVal)
					maxVal = f;
			}
				
			if (maxVal>0.0f) {
				for (int i=0; i<segmentsData.length; i++) {
					segmentsData[i] = (float)segmentsData[i]/maxVal;
				}
				
				for (int i=0; i<sourcesData.length; i++) {
					sourcesData[i] = (float)sourcesData[i]/maxVal;
				}
			}
			
			GLES20Primitives primitives = new GLES20Primitives();
			mSegments = primitives.new Line(
					segmentsData, 
					new float[] {0.8f, 0.8f, 0.8f}
			);
			mSegments.initBuffers();
			mSegments.createProgram();
			
			if (sourcesData.length>0) {
				mSources = primitives.new Line(
						sourcesData, 
						new float[] {0.698f, 0.2f, 0.2f}
				);
				mSources.initBuffers();
				mSources.createProgram();
			}
		}
	}	

}
