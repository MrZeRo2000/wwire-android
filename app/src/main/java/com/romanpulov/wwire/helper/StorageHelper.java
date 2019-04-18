package com.romanpulov.wwire.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class StorageHelper {

    private static final String DATA_FILE_EXTENSION = "WW1";

    @NonNull
    static File getDataFileFolder(@NonNull Context context) {
        return context.getFilesDir();
    }

    @NonNull
    public static File getDataFile(@NonNull Context context, String fileName) {
        return new File(getDataFileFolder(context), fileName);
    }

    public static String[] getDataFileNameList(@NonNull Context context) {
        File dataFolder = getDataFileFolder(context);

        String[] files = null;
        if (dataFolder.exists()) {
            files = dataFolder.list(new FilenameFilter()  {

                @Override
                public boolean accept(File dir, String filename) {
                    return filename.toUpperCase(Locale.US).endsWith(DATA_FILE_EXTENSION);
                }
            });
        }

        return files;
    }

    private StorageHelper() {
        throw new AssertionError();
    }
}
