package com.romanpulov.wwire.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

public class StorageHelper {

    @NonNull
    public static File getDataFileFolder(@NonNull Context context) {
        return context.getFilesDir();
    }

    @NonNull
    public static File getDataFile(@NonNull Context context, String fileName) {
        return new File(getDataFileFolder(context), fileName);
    }
}
