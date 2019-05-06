package com.romanpulov.wwire.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

public class AssetsHelper {

    public static void listAssets(@NonNull Context context, @NonNull String path) {
        AssetManager assetManager = context.getResources().getAssets();

        if (assetManager != null) {
            File destFolderFile = StorageHelper.getDataFileFolder(context);
            try {
                String[] assetList = assetManager.list(path);
                if (assetList != null) {
                    for (String s : assetList) {
                        File destFile = new File(destFolderFile, s);
                        if (!destFile.exists()) {
                            InputStream inStream = null;
                            FileOutputStream outStream = null;

                            try {
                                inStream = assetManager.open(path.concat(File.separator).concat(s));
                                outStream = new FileOutputStream(destFile);

                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = inStream.read(buf)) > 0) {
                                    outStream.write(buf, 0, len);
                                }
                            } finally {
                                if (inStream != null) {
                                    inStream.close();
                                }
                                if (outStream != null) {
                                    outStream.flush();
                                    outStream.close();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private AssetsHelper() {
        throw new AssertionError();
    }
}
