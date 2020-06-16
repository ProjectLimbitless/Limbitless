package com.example.limbitlesssummerproject19.Album;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyUtil {
    // utility method which is exposed to the outside
    public static void copyDirectory(File sourceDir, File destDir)
            throws IOException {
        // creates the destination directory if it does not exist
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // throws exception if the source does not exist
        if (!sourceDir.exists()) {
            throw new IllegalArgumentException("sourceDir does not exist");
        }

        // throws exception if the arguments are not directories
        if (sourceDir.isFile() || destDir.isFile()) {
            throw new IllegalArgumentException(
                    "Either sourceDir or destDir is not a directory");
        }

        copyDirectoryImpl(sourceDir, destDir);
    }

    // implementation of copy directory method
    private static void copyDirectoryImpl(File sourceDir, File destDir)
            throws IOException {
        File[] items = sourceDir.listFiles();
        if (items != null && items.length > 0) {
            for (File anItem : items) {
                if (anItem.isDirectory()) {
                    // create the directory in the destination
                    File newDir = new File(destDir, anItem.getName());
                    System.out.println("CREATED DIR: "
                            + newDir.getAbsolutePath());
                    newDir.mkdir();

                    // copy the directory (recursive call)
                    copyDirectory(anItem, newDir);
                } else {
                    // copy the file
                    File destFile = new File(destDir, anItem.getName());
                    copySingleFile(anItem, destFile);
                }
            }
        }
    }

    // copy a file
    private static void copySingleFile(File sourceFile, File destFile)
            throws IOException {
        System.out.println("COPY FILE: " + sourceFile.getAbsolutePath()
                + " TO: " + destFile.getAbsolutePath());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }
}
