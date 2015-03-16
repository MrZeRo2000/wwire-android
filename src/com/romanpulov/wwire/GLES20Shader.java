package com.romanpulov.wwire;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

public abstract class GLES20Shader {
	protected int mProgram;          //program		
	protected FloatBuffer mVFB;      //vertex buffer
	protected FloatBuffer mNFB;      //normals buffer
	protected ShortBuffer mISB;      //indices buffer
	protected FloatBuffer mCFB;      //color buffer
	
	protected String mVertexShaderCode;
	protected String mFragmentShaderCode;
	
	public GLES20Shader() {
		
	}
	
	public int getProgram() {
		return mProgram;
	}
	
	public int createProgram() {
		mProgram = GLES20.glCreateProgram();
		
		int vertexShader = 0;
		int fragmentShader = 0;
		if (null != mVertexShaderCode) {
			 vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
			 GLES20.glAttachShader(mProgram, vertexShader);
		}	 
		if (null != mFragmentShaderCode) {
			fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
			GLES20.glAttachShader(mProgram, fragmentShader);
		}				
		
		GLES20.glLinkProgram(mProgram);
		return mProgram;
	}
	
	public static FloatBuffer fromArrayToFloatBuffer(float[] a) {
		ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(a);
		fb.position(0);
		return fb;
	}
	
	public static ShortBuffer fromArrayToShortBuffer(short[] a) {
		ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 2);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		sb.put(a);
		sb.position(0);
		return sb;
	}
	
	public abstract void initBuffers();
	
	public abstract void draw(GL10 gl, GLES20Primitives.GLES20Matrix matrix);

	protected int loadShader(int type, String source)  {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		return shader;
	}	
	
}
