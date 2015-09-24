package com.romanpulov.wwire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

public class WWireData {
	
	private static WWireData instance;
	
	public static void createInstance(Context context) {
		if (null == instance)
			instance = new WWireData(context);
	}
	
	public static WWireData getInstance() {
		return instance;
	}
	
	//folders
	private String mPackageFileFolder;
	private String mDataFileFolder;
	
	public String getPackageFileFolder() {
		return mPackageFileFolder;
	}
	
	public String getDataFileFolder() {
		return mDataFileFolder;
	}	
	
	//model
	private float[] mSegments;
	private float[] mSources;
	private float[] mGaint;
	private float[] mVar;
	
	private void initFileFolders() {
		StringBuilder dataFileFolderStringBuilder = new StringBuilder(Environment.getExternalStorageDirectory().toString());
		dataFileFolderStringBuilder.append("/").append(mContext.getPackageName());
		mPackageFileFolder = dataFileFolderStringBuilder.toString();
		dataFileFolderStringBuilder.append("/data/");
		mDataFileFolder = dataFileFolderStringBuilder.toString();
	}

	public float[] getSegments() {
		return mSegments;
	}
	
	public float[] getSources() {
		return mSources;
	}
	
	public float[] getGaint() {
		return mGaint;
	}
	
	public boolean gaintAvailable() {
		return mGaint != null;
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
		dataFileList = new ArrayList<>();
		initFileFolders();
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
		float[] res = new float[str.size()];
		int i = 0;
		for (String s : str) {
			res[i++] = Float.valueOf(s);
		}
		return res;
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
			} catch ( IOException ioe ) {
                ioe.printStackTrace();
            }
		}		
	}	
	
	private float[] getSectionArray(String[] sectionNames) {
		ArrayList<String> stringsSection = new ArrayList<>();
		for (String s : sectionNames) {
			readSection(s, stringsSection);
		}		
		return copyFromArrayList(stringsSection);
	}
	
	public void loadFromFile(String fileName) {
		String dataFileString = getDataFileFolder() + fileName;
		loadFileList(dataFileString);
		
		mSegments = getSectionArray(new String[] {SECTION_NAME_SEGMENT_LAYOUT});
		//Log.d("LoadFromFile", "Segments: " + String.valueOf(mSegments.length/6));
		mSources = getSectionArray(new String[] {SECTION_NAME_SOURCE_LAYOUT, SECTION_NAME_SOURCEV_LAYOUT});
		//Log.d("LoadFromFile", "Sources: " + String.valueOf(mSources.length/6));
		mGaint = getSectionArray(new String[] {SECTION_NAME_GAINT});
		//Log.d("LoadFromFile", "Gaint: " + String.valueOf(mGaint.length));
		mVar = getSectionArray(new String[] {SECTION_NAME_VAR});
		//Log.d("LoadFromFile", "Var: " + String.valueOf(mVar.length));
		//Log.d("LoadFromFile", "DP: " + String.valueOf(getDP()));
		//Log.d("LoadFromFile", "DT: " + String.valueOf(getDT()));
		//Log.d("LoadFromFile", "LP: " + String.valueOf(getLP()));
		//Log.d("LoadFromFile", "LT: " + String.valueOf(getLT()));
	}
}
