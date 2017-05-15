package com.randmcnally.bb.poc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class FileUtil {

    private static final String TAG = "FileUtil ->";

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

    public static String getDeviceUID(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Config", MODE_PRIVATE);
        String uniqueID = settings.getString("UUID", "");
        if (uniqueID.isEmpty()) {
            uniqueID = createUID(context);
        }
        return uniqueID;
    }

    public static String createUID(Context context) {
        String uniqueID = UUID.randomUUID().toString();
        SharedPreferences setting = context.getSharedPreferences("Config", MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("UUID", uniqueID);
        editor.apply();
        return uniqueID;
    }
}
