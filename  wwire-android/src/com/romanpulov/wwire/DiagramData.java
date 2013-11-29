package com.romanpulov.wwire;

import android.util.Log;

public class DiagramData {
	private int mLP;
	private int mLP2;
	private int mLT;
	private int mLT2;
	private int mLT4;
	private int mDT;
	private int mDP;
	
	float[] mGaint;
	
	private float[] mVertex;
	private float[] mVertexWF;
	private float[] mNormal;
	
	private int[] mNP1;
	private int[] mNP2;
	private int[] mNU;
	private int[] mNNU;
	private int[] mND;
	private int[] mNND; 

	
	private short[] mVertexIndices;
	private int mVertexIndicesPos;
	
	public float[] getVertex() {
		return mVertex;
	}
	
	public float[] getVertexWF() {
		return mVertexWF;
	}
	
	public float[] getNormal() {
		return mNormal;
	}
	
	public short[] getVertexIndices() {
		return mVertexIndices;
	}
	
	public DiagramData() {
		super();
		initData();
	}
	
	private void calcVertex() {
		
		float[] point = new float[3];
		
		for (int p = 0; p < mLP; p++)
		{
			double phi = p * mDP * Math.PI / 180.0; 
			for (int t = 0; t < mLT; t++) {
				 double thet = t * mDT * Math.PI / 180.0;
				 int idx = p * mLT + t;
				 sphereToCube(/*mGaint[idx]*/0.99f, phi, thet, point);
				 mVertex[idx*3] = point[0];
				 mVertex[idx*3 + 1] = point[1];
				 mVertex[idx*3 + 2] = point[2];				 
				 sphereToCube(/*mGaint[idx]*/1.0f, phi, thet, point);
				 mVertexWF[idx*3] = point[0];
				 mVertexWF[idx*3 + 1] = point[1];
				 mVertexWF[idx*3 + 2] = point[2];
			}
		}			
	}
	
	private void calcVertexIndices() {
		mVertexIndicesPos = 0;
		for (int i = 0; i<mLP2; i++) {
			f1f(i);
			if (i<(mLP2 - 1))
				f1b(i);
			else
				f2b(i);
		}
		for (int i = 0; i<mLP2; i++) {
			f3f(i);
			if (i<(mLP2 - 1))
				f3b(i);
			else
				f4b();
		}
		f1n();
		f2n();
		f3n();
	}
	
	private void appendVertex(int v) {
		mVertexIndices[mVertexIndicesPos++] = (short)v;
	}
	
	private void f4n(int[] n, int[] nn, float[] res) {
		float[] v = new float[3];
		v[0] = 0;
		v[1] = 0;
		v[2] = 0;
		for (int i = 0; i < n.length; i ++) {
			int idx = 3 * n[i];
			for (int j = 0; j < 3; j ++)
				v[j] = v[j] + mVertex[idx + j] - mVertex[nn[0] * 3 + j];
		}
		
		for (int i = 0; i < nn.length; i ++) {
			int idx = 3 * nn[i];
			for (int j = 0; j < 3; j ++)
				res[idx + j] = - v[j];
		}
	}
	
	private void f1f(int k) {
		int offs = k * 2 * mLT;
		
		int id0 = offs + 1;
		int id1 = offs;                        
		int id2 = offs + 1 + mLT;               
				                                  
		//normals                          
		mNP1[id1] = id0;                    
		mNP2[id1] = id2;                    
				                                  
		//vertex                           
		appendVertex(id1);		
		
				                                  
		for (int i = 1; i<mLT2; i ++) {
			id0 = offs + i - 1;              
			id1 = offs + i;                  
			id2 = offs + i + mLT;             
			                                  
			//normals                        
			mNP1[id1] = id2;                  
			mNP2[id1] = id0;
			mNP1[id2] = id1;
			mNP2[id2] = id1 + 1;
			                                  
			//vertex                         
			appendVertex(id1);
			appendVertex(id2);
		}	
		appendVertex(id2 + 1);
	}
	
	private void f1b(int k) {
		int offs = k * 2 * mLT  + mLT;		
		int id1 = offs + mLT2;
		int id2;
		
		//vertex
		appendVertex(id1);		
		
		for (int i = - 1 + mLT2; i>0; i--) {
			id1 = offs + i + mLT;
			id2 = offs + i;
			
			//vertex
			appendVertex(id1);
			appendVertex(id2);
		}
		appendVertex(id1 - 1);
	}
	
	private void f2b(int k) {
		int offs = k * 2 * mLT  + mLT;
		int id1 = offs + mLT2;
		int id2;
		
		//vertex
		appendVertex(id1);
		
		for (int i = -1 + mLT2; i>0; i--) {
			id1 = mLT - i;
			id2 = offs + i;
			
			//vertex
			appendVertex(id1);
			appendVertex(id2);
		}
		appendVertex(id1 + 1);
	}
	
	private void f3f(int k) {
		int offs = k * 2 * mLT + mLT2;		
		int id1 = offs + mLT2;
		int id2;
		
		//vertex
		appendVertex(id1);
		
		for (int i = mLT2 - 1; i > 0; i--) {
			id1 = offs + i;
			id2 = offs + i + mLT;
			
			//vertex
			appendVertex(id1);
			appendVertex(id2);
		}
		appendVertex(id1 - 1);
	}
	
	private void f3b(int k) {
		int offs = k * 2 * mLT + mLT + mLT2;
		int id1 = offs - mLT;
		int id2;
		
		//vertex
		appendVertex(id1);
		
		for (int i = 1; i < mLT2; i++) {
			id1 = offs + mLT + i;
			id2 = offs + i;

			//vertex
			appendVertex(id1);
			appendVertex(id2);
		}
		appendVertex(id1 + 1);
	}
	
	private void f4b() {
		int id1 = mLT * mLT2 - mLT - mLT2;
		int id2;
				
		//vertex
		appendVertex(id1);
		
		for (int i = mLT2 - 1; i > 0; i--) {
			id1 = i;
			id2 = mLT * mLT2 - i;
			
			//vertex
			appendVertex(id1);
			appendVertex(id2);
		}
		appendVertex(0);
		appendVertex(0);
	}
	
	private void f1n() {
		for (int k = 0; k < mLP; k ++ )
		{
			int offs = k * mLT;
			for (int i = 1; i < mLT2; i ++) {
			    int idx = offs + i;
			    mNP1[idx] = idx + 1;
			    if (k == mLP - 1) 
			      mNP2[idx] = mLT - i;
			    else
			      mNP2[idx] = idx + mLT;   
			}
		}
		
	}
	
	private void f2n() {
		for (int k = 0; k < mLP; k ++ ) {
			int offs = k * mLT + mLT2;
			for (int i = 1; i < mLT2; i ++ ) {
				int idx = offs + i;
				mNP1[idx] = idx - 1;
				if (k == mLP - 1) 
					mNP2[idx] = mLT2 - i;
				else
					mNP2[idx] = idx + mLT;
			}
		}
	}
	
	private void f3n() {
		for (int i = 0; i < mLP; i ++ ) {
			mNNU[i] = i * mLT;
			mNND[i] = i * mLT + mLT2;
		}
		
		for (int i = 0; i < 2 * mLP; i ++) {
			if (i < mLP) {
			    mNU[i] = mLT * i + 1;
			    mND[i] = mLT * i + mLT2 - 1;
			}  else {
			    mNU[i] = mLT * (i - mLP) + mLT - 1;
			    mND[i] = mLT * (i - mLP) + mLT2 + 1;
			}
		}
	}
	
	private void calcNormals() {
		for (int i = 0; i < mLP * mLT; i++) {
			Log.d("normals", String.valueOf(i) + " np1 = " + String.valueOf(mNP1[i]) + " np2 = " + String.valueOf(mNP2[i]));
			
			mNormal[i * 3 + 0] = 
					(mVertex[mNP1[i] * 3 + 1] - mVertex[i * 3 + 1]) * (mVertex[mNP2[i] * 3 + 2] - mVertex[i * 3 + 2]) -
					(mVertex[mNP2[i] * 3 + 1] - mVertex[i * 3 + 1]) * (mVertex[mNP1[i] * 3 + 2] - mVertex[i * 3 + 2]);
			mNormal[i * 3 + 1] = 
					(mVertex[mNP1[i] * 3 + 2] - mVertex[i * 3 + 2]) * (mVertex[mNP2[i] * 3 + 0] - mVertex[i * 3 + 0]) -
					(mVertex[mNP2[i] * 3 + 2] - mVertex[i * 3 + 2]) * (mVertex[mNP1[i] * 3 + 0] - mVertex[i * 3 + 0]);
			mNormal[i * 3 + 2] = 
					(mVertex[mNP1[i] * 3 + 0] - mVertex[i * 3 + 0]) * (mVertex[mNP2[i] * 3 + 1] - mVertex[i * 3 + 1]) -
					(mVertex[mNP2[i] * 3 + 0] - mVertex[i * 3 + 0]) * (mVertex[mNP1[i] * 3 + 1] - mVertex[i * 3 + 1]);
					
		}
		f4n(mNU, mNNU, mNormal);
		f4n(mND, mNND, mNormal);
		normalizeNormal(mNormal);
	}
	
	private void initData() {
		
		//reading data from WWireData
		WWireData wd = WWireData.getInstance();
		mLP = (int) (wd.getLP() / 2);
		mLP2 = (int) mLP / 2;
		mLT = wd.getLT();
		mLT2 = (int) mLT / 2;
		mLT4 = (int) mLT2 / 2;
		mDT = wd.getDT();
		mDP = wd.getDP();		
		mGaint = wd.getGaint(); 
		
		//calc vertex
		mVertex = new float[mLP * mLT * 3];
		mVertexWF = new float[mLP * mLT * 3];
		calcVertex();	
		//normalizeVertex(mVertex);
		
		//calc indices and normals
		int indCount = mLP * 2 * ((mLT2 - 1) * 2 + 1) + 1 + mLT;
		mVertexIndices = new short[indCount];
		mNP1 = new int[mLP * mLT];
		mNP2 = new int[mLP * mLT];
		mNU = new int[mLT];
		mNNU = new int[mLP];
		mND = new int[mLT];
		mNND = new int[mLP];
				
		calcVertexIndices();
		
		//calc normals
		mNormal = new float[mLP * mLT * 3];
		calcNormals();
		
	}
	
	public static void sphereToCube(float r, double phi, double thet, float[] point) {
		double ct = Math.cos(thet);
		double cp = Math.cos(phi);
		double sp = Math.sin(phi);
		double st = Math.sin(thet);
		
		point[0] = (float) (r*st*cp);
	    point[1] = (float) (r*st*sp);
	    point[2] = (float) (r*ct);
	}
	
	private void normalizeNormal(float[] v) {		
		for (int i = 0; i < v.length / 3; i ++) {
			int idx = i * 3;
			float len = (float) Math.sqrt(v[idx] * v[idx] + v[idx + 1] * v[idx + 1] + v[idx + 2] * v[idx + 2]);
			if (len > 0.0f) {
				for (int j = 0; j < 3; j ++)
					v[idx + j] = v[idx + j] / len;
			}
		}
	}
	
	private void normalizeVertex(float[] v) {
		float max = 0.0f;
		for (int i = 0; i < v.length / 3; i ++) {
			int idx = i * 3;
			float len = (float) Math.sqrt(v[idx] * v[idx] + v[idx + 1] * v[idx + 1] + v[idx + 2] * v[idx + 2]);
			if (len > max)
				max = len;
		}
		if (max > 0.0f)
			for (int i = 0; i < v.length; i ++)
				v[i] = v[i] / max;
	}
	
	
}
