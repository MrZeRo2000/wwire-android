package com.romanpulov.wwire.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;

public class WWireData {
	
	private static WWireData instance;
	
	@NonNull
	public static WWireData getInstance() {
	    if (instance == null) {
	        instance = new WWireData();
        }
		return instance;
	}
	
	//model
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
	
	private static String SECTION_KEY = "@";
    private static String SECTION_NAME_SEGMENT_LAYOUT = "segment.layout";
    private static String SECTION_NAME_SOURCE_LAYOUT = "source.layout";
    private static String SECTION_NAME_SOURCEV_LAYOUT = "sourcev.layout";
    private static String SECTION_NAME_GAINT = "gain.t";
    private static String SECTION_NAME_VAR = "var";

	private WWireData() {

	}
	
	private static void readSection(@NonNull List<String> data, String sectionName, ArrayList<String> sectionValues) {
		boolean inSection = false;
		for (String s : data) {
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

	@NonNull
	private static float[] copyFromArrayList(@NonNull List<String> str) {
		float[] res = new float[str.size()];
		int i = 0;
		for (String s : str) {
			res[i++] = Float.valueOf(s);
		}
		return res;
	}

	@NonNull
	private static List<String> loadFileList(@NonNull File file) {
        List<String> data = new ArrayList<>();

		if (file.exists()) {
			try {
				BufferedReader inBuf = new BufferedReader(new FileReader(file));
				while (true) {
					String s = inBuf.readLine();
			        if (s == null)
			        	break;
                    data.add(s);
				}
				inBuf.close();
			} catch ( IOException ioe ) {
                ioe.printStackTrace();
            }
		}

		return data;
	}	

	@NonNull
	private static float[] getSectionArray(@NonNull List<String> data, @NonNull String[] sectionNames) {
		ArrayList<String> stringsSection = new ArrayList<>();
		for (String s : sectionNames) {
			readSection(data, s, stringsSection);
		}		
		return copyFromArrayList(stringsSection);
	}
	
	public void loadFromFile(File file) {
		List<String> data = loadFileList(file);
		
		mSegments = getSectionArray(data, new String[] {SECTION_NAME_SEGMENT_LAYOUT});
		//Log.d("LoadFromFile", "Segments: " + String.valueOf(mSegments.length/6));
		mSources = getSectionArray(data, new String[] {SECTION_NAME_SOURCE_LAYOUT, SECTION_NAME_SOURCEV_LAYOUT});
		//Log.d("LoadFromFile", "Sources: " + String.valueOf(mSources.length/6));
		mGaint = getSectionArray(data, new String[] {SECTION_NAME_GAINT});
		//Log.d("LoadFromFile", "Gaint: " + String.valueOf(mGaint.length));
		mVar = getSectionArray(data, new String[] {SECTION_NAME_VAR});
		//Log.d("LoadFromFile", "Var: " + String.valueOf(mVar.length));
		//Log.d("LoadFromFile", "DP: " + String.valueOf(getDP()));
		//Log.d("LoadFromFile", "DT: " + String.valueOf(getDT()));
		//Log.d("LoadFromFile", "LP: " + String.valueOf(getLP()));
		//Log.d("LoadFromFile", "LT: " + String.valueOf(getLT()));
	}
}
