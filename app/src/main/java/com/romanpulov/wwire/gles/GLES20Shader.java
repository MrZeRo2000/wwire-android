package com.romanpulov.wwire.gles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

abstract class GLES20Shader {
	int mProgram;          //program
	FloatBuffer mVFB;      //vertex buffer
	FloatBuffer mNFB;      //normals buffer
	ShortBuffer mISB;      //indices buffer
	FloatBuffer mCFB;      //color buffer
	
	String mVertexShaderCode;
	String mFragmentShaderCode;
	
	GLES20Shader() {
		
	}
	
	int createProgram() {
		mProgram = GLES20.glCreateProgram();
		
		if (null != mVertexShaderCode) {
			 int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
			 GLES20.glAttachShader(mProgram, vertexShader);
		}	 
		if (null != mFragmentShaderCode) {
			int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
			GLES20.glAttachShader(mProgram, fragmentShader);
		}				
		
		GLES20.glLinkProgram(mProgram);
		return mProgram;
	}
	
	static FloatBuffer fromArrayToFloatBuffer(float[] a) {
		ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(a);
		fb.position(0);
		return fb;
	}
	
	static ShortBuffer fromArrayToShortBuffer(short[] a) {
		ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 2);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		sb.put(a);
		sb.position(0);
		return sb;
	}
	
	public abstract void initBuffers();
	
	public abstract void draw(GLES20Primitives.GLES20Matrix matrix);

	private int loadShader(int type, String source)  {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		return shader;
	}	
}
