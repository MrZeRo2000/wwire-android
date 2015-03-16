package com.romanpulov.wwire;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AssetsHelper {
	public static String TAG = "AssetsHelper";
	
	public static void listAssets(Context context, String path) {		
		AssetManager assetManager = context.getResources().getAssets();
		Log.d(TAG, "Before");	
		
		//get package file folder
		String packageFolder = WWireData.getInstance().getPackageFileFolder();		
		File packageFolderFile = new File(packageFolder);		
		
		//get data file folder
		String destFolder = WWireData.getInstance().getDataFileFolder();		
		File destFolderFile = new File(destFolder);
		
		Log.d(TAG, destFolder);
		try {
			for (String s : assetManager.list(path)) {
				Log.d(TAG, s);
				File destFile = new File(destFolder, s);				
				Log.d(TAG, destFile.getPath());
				if (!destFile.exists()) {
					Log.d(TAG, "not exists");
					
					//create package file folder if not exists
					if (!packageFolderFile.exists()) {
						if (!packageFolderFile.mkdir()) {
							Log.d(TAG, "Failed to create " + packageFolder);
							return;
						}
					}
					
					//create data file folder if not exists
					if (!destFolderFile.exists()) {
						if (!destFolderFile.mkdir()) {
							Log.d(TAG, "Failed to create " + destFolder);
							return;
						}
					}
					
					InputStream inStream = assetManager.open(path.concat("/").concat(s));
					Log.d(TAG, "Created InputStream");
					
					FileOutputStream outStream = new FileOutputStream(destFile);
					Log.d(TAG, "Created OutputStream");
					
					try {
						byte[] buf = new byte[1024];
						int len;
						 while ((len = inStream.read(buf)) > 0) {
							 outStream.write(buf, 0, len);
						 }
						 Log.d(TAG, "File is written");						 						 
						 
						
					} finally {
						inStream.close();
						outStream.flush();
						outStream.close();
					}
					
				}
				
			}
			Log.d(TAG, "After");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
