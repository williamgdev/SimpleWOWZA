package com.randmcnally.bb.wowza.util;

import android.os.Environment;
import android.util.Log;

import com.wowza.gocoder.sdk.api.logging.WZLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static File getOutputMediaFile(File savedFile, String folderName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "GoCoderSDK");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "getOutputMediaFile: failed to create the directory in which to store the MP4 in " + Environment.getExternalStorageDirectory(
                ) + File.separator + "sdcard/Movies/");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        savedFile = new File(mediaStorageDir.getPath() + File.separator + folderName + "_" + timeStamp + ".mp3");
        return savedFile;
    }

}
