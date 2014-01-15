package com.romanpulov.wwire;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public final class GLES20Primitives {
	
	public interface ModelDrawer {		
		public void drawElements(GL10 gl, GLES20Matrix matrix);
		public void initElements(GLES20Matrix matrix);
		public void invalidate();
	}
	
	public class GLES20Matrix {
		// model and normal
		public float[] model = new float[16];
		public float[] normal = new float[16];
		// rotation support
		public float[] currentRotation = new float[16];
		public float[] accumulatedRotation = new float[16];
		// auxiliary matrix
		public float[] temp = new float[16];
		// view
		public float[] view = new float[16];
		public int[] viewport = new int[4];
		// projection
		public float[] projection = new float[16];
		//modelview 
		public float[] modelViewMatrix = new float[16];
		// resulting
		public float[] mvp = new float[16];
		
		public void initModel() {
			Matrix.setIdentityM(model, 0);
			Matrix.setIdentityM(normal, 0);
		}
		
		public void init() {
			initModel();
			Matrix.setIdentityM(accumulatedRotation, 0);
		}
		
		public void calcModelViewMatrix() {
			Matrix.multiplyMM(modelViewMatrix, 0, view, 0, model, 0);
		}
		
	}
	
	public class Line extends GLES20Shader {
		
		// shader locations
		private int mLineAVertexLocation;
		private int mLineAColorLocation;
		private int mLineMVPMatrixHandle;
		
		//shader data
		private float mLineVFA[];
		private float mLineCFA[];
		
		public Line(float[] lineVFA, float[] lineCFA) {
			super();
			mLineVFA = lineVFA;
			mLineCFA = lineCFA;
			mVertexShaderCode = GLES20ShaderCode.LINE_VERTEX_SHADER_CODE;
			mFragmentShaderCode = GLES20ShaderCode.LINE_FRAGMENT_SHADER_CODE;			
		}			

		@Override
		public void initBuffers() {
			// TODO Auto-generated method stub
			
			// vertices			
			mVFB = fromArrayToFloatBuffer(mLineVFA);
			// colors
			mCFB = fromArrayToFloatBuffer(mLineCFA);
		}		
		
		@Override
		public int createProgram() {
			int program = super.createProgram();
			mLineMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");        
			mLineAVertexLocation = GLES20.glGetAttribLocation(program, "aPosition");
			mLineAColorLocation = GLES20.glGetUniformLocation(program, "uColor");
			Log.d("Draw", getClass().toString() + " create program = " + String.valueOf(mProgram));
			return program;
		}
		
		@Override
		public void draw(GL10 gl, GLES20Matrix matrix) {
			GLES20.glUseProgram(mProgram);
			Log.d("Draw", getClass().toString() + " use program = " + String.valueOf(mProgram));
		    GLES20.glVertexAttribPointer(mLineAVertexLocation, 3, GLES20.GL_FLOAT, false, 0, mVFB);
		    GLES20.glEnableVertexAttribArray(mLineAVertexLocation);
		    
		    GLES20.glUniform3fv(mLineAColorLocation, 1, mCFB);		    
		    
		    GLES20.glLineWidth(2);		    
		    GLES20.glUniformMatrix4fv(mLineMVPMatrixHandle, 1, false, matrix.mvp, 0);
		    GLES20.glDrawArrays(GLES20.GL_LINES, 0, mVFB.capacity()/3);
		    //GLES20.glDrawElements(GLES20.GL_LINES, 6, GLES20.GL_UNSIGNED_SHORT, mISB);		    
		}
	}	
	
	public class Axes extends Line {
		
		public Axes() {
			super(
					 new float[] {
							 0.0f, 0.0f, 0.0f,
							 1.0f, 0.0f, 0.0f,
							 0.0f, 0.0f, 0.0f,
							 0.0f, 1.0f, 0.0f,
							 0.0f, 0.0f, 0.0f,
							 0.0f, 0.0f, 1.0f
					 }, 
					 new float[] {
							 0.5f, 0.5f, 0.5f							 
					 });
		}
	}
	
	public class Surface extends GLES20Shader {
		//shader locations
		private int mSurfaceMVPMatrixHandle;
		private int mSurfaceModelMatrixHandle;
		private int mSurfaceNormalMatrixHandle;
		private int mSurfaceAVertexLocation;
		private int mSurfaceANormalLocation;
		private int mSurfaceAColorLocation;
		
		//shader data
		private float[] mSurfaceVFA;
		private float[] mSurfaceNFA;
		private short[] mSurfaceISA;
		private float[] mSurfaceCFA;
		
		//surface mode
		private int mSurfaceMode;
		
		public Surface(float[] surfaceVFA, float[] surfaceNFA, short[] surfaceISA, float[] surfaceCFA, int surfaceMode) {
			super();
			mSurfaceVFA = surfaceVFA;
			mSurfaceNFA = surfaceNFA;
			mSurfaceISA = surfaceISA;
			mSurfaceCFA = surfaceCFA;
			mSurfaceMode = surfaceMode;
			mVertexShaderCode = GLES20ShaderCode.SURFACE_VERTEX_SHADER_CODE;
			mFragmentShaderCode = GLES20ShaderCode.SURFACE_FRAGMENT_SHADER_CODE;	
		}

		@Override
		public void initBuffers() {
			// TODO Auto-generated method stub
			// vertices			
			mVFB = fromArrayToFloatBuffer(mSurfaceVFA);
			//normals
			mNFB = fromArrayToFloatBuffer(mSurfaceNFA);
			//indices
			mISB = fromArrayToShortBuffer(mSurfaceISA);
			//colors
			mCFB = fromArrayToFloatBuffer(mSurfaceCFA);
		}
		
		@Override
		public int createProgram() {
			int program = super.createProgram();
			mSurfaceMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
			mSurfaceModelMatrixHandle = GLES20.glGetUniformLocation(program, "u_ModelMatrix");
			mSurfaceNormalMatrixHandle = GLES20.glGetUniformLocation(program, "u_NormalMatrix");
			mSurfaceAVertexLocation = GLES20.glGetAttribLocation(program, "aPosition");
			mSurfaceANormalLocation = GLES20.glGetAttribLocation(program, "aNormal");
			mSurfaceAColorLocation = GLES20.glGetUniformLocation(program, "uColor");
			Log.d("Draw", getClass().toString() + " create program = " + String.valueOf(mProgram));
			return program;
		}

		@Override
		public void draw(GL10 gl, GLES20Matrix matrix) {
			// TODO Auto-generated method stub
			GLES20.glUseProgram(mProgram);
			Log.d("Draw", getClass().toString() + " use program = " + String.valueOf(mProgram));
			//vertex
			GLES20.glVertexAttribPointer(mSurfaceAVertexLocation, 3, GLES20.GL_FLOAT, false, 0, mVFB);
		    GLES20.glEnableVertexAttribArray(mSurfaceAVertexLocation);
		    //normal
		    GLES20.glVertexAttribPointer(mSurfaceANormalLocation, 3, GLES20.GL_FLOAT, false, 0, mNFB);
		    GLES20.glEnableVertexAttribArray(mSurfaceANormalLocation);
		    //color
		    GLES20.glUniform3fv(mSurfaceAColorLocation, 1, mCFB);
		    //matrix
		    GLES20.glUniformMatrix4fv(mSurfaceMVPMatrixHandle, 1, false, matrix.mvp, 0);
		    GLES20.glUniformMatrix4fv(mSurfaceModelMatrixHandle, 1, false, matrix.model, 0);
		    GLES20.glUniformMatrix4fv(mSurfaceNormalMatrixHandle, 1, false, matrix.normal, 0);
		    //elements
		    GLES20.glDrawElements(mSurfaceMode, mSurfaceISA.length, GLES20.GL_UNSIGNED_SHORT, mISB);
		}
		
	}
	
	public class Plate extends Surface {
		public Plate() {
			super(
					new float[] {							
							0.5f, 0.0f, 0.0f,
							0.5f, 0.5f, 0.0f,
							0.0f, 0.0f, 0.0f,
							0.0f, 0.5f, 0,0f
							},
					new float[] {
							0.0f, 0.0f, 1.0f,
							0.0f, 0.0f, 1.0f,
							0.0f, 0.0f, 1.0f,
							0.0f, 0.0f, 1.0f
							},
					new short[] {0, 1, 2, 3},
					new float[] {0.0f, 1.0f, 0.0f},
					GLES20.GL_TRIANGLE_STRIP
					);
	
		}
	}
	
	public class BendedPlate extends Surface {
		public BendedPlate() {
			super(
					new float[] {
							0.0f, 0.0f, 0.0f,
							0.5f, 0.0f, 0.0f,
							0.5f, 0.5f, 0.0f,
							0.0f, 0.5f, 0.0f,
							0.5f, 0.5f, -0.5f,
							0.0f, 0.5f, -0.5f
							},
					new float[] {
							0.0f, 0.0f, 1.0f,
							0.0f, 0.0f, 1.0f,
							0.0f, 1.0f, 1.0f,
							0.0f, 1.0f, 1.0f,
							0.0f, 1.0f, 0.0f,
							0.0f, 1.0f, 0.0f
							},
					new short[] {0, 1, 2, 3, 0, 2, 3, 2, 4, 3, 4, 5},
					new float[] {0.0f, 1.0f, 0.0f},
					GLES20.GL_TRIANGLE_STRIP
					);
	
		}
	}

}
