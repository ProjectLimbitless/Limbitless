package com.example.limbitlesssummerproject19.Album;

import android.content.Context;
import android.os.Environment;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * File: InternalStorage.java
 *
 *
 * Class to represent internal storage.
 *
 */
class InternalStorage {

    private ArrayList<Pair<String, String>> sessionFiles = new ArrayList<>();

    /** Obtains the directory name */
    private String directoryName = Environment.getExternalStorageDirectory() + File.separator +
            "ProstheticFolder";


    ArrayList<Pair<String, String>> getListFromFiles(Context context, File[] files) {
        try {

            /** Get session thumbnails ( image at first index of each session ) */
            for (File file : files) {
                File[] sessionImages = file.listFiles();
                if (sessionImages.length != 0) {
                    Pair<String, String> newPair = new Pair<>(sessionImages[0].getAbsolutePath(),
                            file.getName());
                    sessionFiles.add(newPair);
                } else {
                    file.delete();
                }
            }

        } catch (Exception e) {
            e.fillInStackTrace();
            Toast.makeText( context , "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }
        return sessionFiles;
    }


    /**
     * Function: getFilesFromInternalStorage()
     * Purpose: get files from internal storage...(:
     * Parameters: Context context = te context of the activity calling this function
     * Return: an array of files... from internal storage
     */
    File[] getFilesFromInternalStorage(Context context) {

        File countFiles = new File(directoryName);
        File[] files = countFiles.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();

            }
        });

        if (files != null && files.length > 1) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    long lastModifiedO1 = o1.lastModified();
                    long lastModifiedO2 = o2.lastModified();
                    return lastModifiedO2 < lastModifiedO1 ? -1 : 0;
                }
            });
        } else {

            Toast.makeText(context, "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }

        return files;
    }
}





