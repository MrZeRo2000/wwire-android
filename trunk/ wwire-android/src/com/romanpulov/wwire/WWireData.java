package com.romanpulov.wwire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class WWireData {
	
	private static WWireData instance;
	
	public static void createInstance(Context context) {
		if (null == instance)
			instance = new WWireData(context);
	}
	
	public static WWireData getInstance() {
		return instance;
	}
	
	private float[] mSegments;
	private float[] mSources;
	private float[] mGaint;
	private float[] mVar;
	
	
	public float[] getSegments() {
		return mSegments;
	}
	
	public float[] getSources() {
		return mSources;
	}
	
	public float[] getGaint() {
		return mGaint;
	}
	
	public int getDP() {
		return (int) mVar[3];
	}
	
	public int getDT() {
		return (int) mVar[4];
	}
	
	
	public int getLP() {
		return (int) (360.0 / mVar[3]);
	}
	
	public int getLT() {
		return (int) (360.0 / mVar[4]);
	}
	
	static String SECTION_KEY = "@";
	static String SECTION_NAME_SEGMENT_LAYOUT = "segment.layout";
	static String SECTION_NAME_SOURCE_LAYOUT = "source.layout";
	static String SECTION_NAME_SOURCEV_LAYOUT = "sourcev.layout";
	static String SECTION_NAME_GAINT = "gain.t";	
	static String SECTION_NAME_VAR = "var";
	
	
	Context mContext;
	ArrayList<String> dataFileList;
	
	private WWireData(Context context) {		
		mContext = context;
		dataFileList = new ArrayList<String>();		
	}
	
	private void readSection(String sectionName, ArrayList<String> sectionValues) {
		boolean inSection = false;
		for (String s:dataFileList) {
			if (s.startsWith(SECTION_KEY)) {
				if (inSection)
					break;
				if (s.equals(SECTION_KEY + sectionName)) {
					inSection = true;
					continue;
				}
			}
			if (inSection)
				sectionValues.add(s);
		}
	}
	
	private float[] copyFromArrayList(ArrayList<String> str) {
		float[] retval = new float[str.size()];
		int i = 0;
		for (String s : str) {
			retval[i] = Float.valueOf(s);
			i++;
		}		
		return retval;		
	}
	
	private void loadFileList(String dataFileString) {
		File dataFile = new File(dataFileString);
		if (dataFile.exists()) {
			dataFileList.clear();
			try {
				BufferedReader inBuf = new BufferedReader(new FileReader(dataFile));
				while (true) {
					String s = inBuf.readLine();
			        if (s == null)
			        	break;
		        	dataFileList.add(s);
				}
				inBuf.close();
			} catch ( IOException ioe ) {}
		}		
	}
	
	public String getDataFileFolder() {
		StringBuilder dataFileFolderStringBuilder = new StringBuilder(Environment.getExternalStorageDirectory().toString());		
		dataFileFolderStringBuilder.append("/Android/data/").append(mContext.getPackageName()).append("/data/");
		return dataFileFolderStringBuilder.toString();
	}
	
	private float[] getSectionArray(String[] sectionNames) {
		ArrayList<String> stringsSection = new ArrayList<String>();
		for (String s : sectionNames) {
			readSection(s, stringsSection);
		}		
		float[] retval = copyFromArrayList(stringsSection);
		return retval;
	}
	
	public void loadFromFile(String fileName) {
		String dataFileString = getDataFileFolder() + fileName;
		
		loadFileList(dataFileString);			
		
		mSegments = getSectionArray(new String[] {SECTION_NAME_SEGMENT_LAYOUT});
		Log.d("LoadFromFile", "Segments: " + String.valueOf(mSegments.length/6));
		
		mSources = getSectionArray(new String[] {SECTION_NAME_SOURCE_LAYOUT, SECTION_NAME_SOURCEV_LAYOUT});
		Log.d("LoadFromFile", "Sources: " + String.valueOf(mSources.length/6));
		
		mGaint = getSectionArray(new String[] {SECTION_NAME_GAINT});
		Log.d("LoadFromFile", "Gaint: " + String.valueOf(mGaint.length));
		
		mVar = getSectionArray(new String[] {SECTION_NAME_VAR});
		Log.d("LoadFromFile", "Var: " + String.valueOf(mVar.length));
		
		Log.d("LoadFromFile", "DP: " + String.valueOf(getDP()));
		Log.d("LoadFromFile", "DT: " + String.valueOf(getDT()));
		Log.d("LoadFromFile", "LP: " + String.valueOf(getLP()));
		Log.d("LoadFromFile", "LT: " + String.valueOf(getLT()));
		
	}
	
}
