package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import android.media.ExifInterface;

import java.security.MessageDigest;

/**
 * File: ImageTransformation.java
 *
 * (Used in AlbumActivty!!!!!)
 *
 * This class transforms images from horizontal to vertical (used when working with Glide)
 *
 */
public class ImageTransformation extends BitmapTransformation {

    private Context context; /** Should be AlbumActivity Context*/
    private int mOrientation; /** orientation to be set*/


    /**
     * Function: Constructor
     * Purpose: creates a ImageTransformation object
     * Parameters: Context context = context of the activity creating this object
     *             int orientation = the orientation for the image transformation
     * Return: none
     */
    public ImageTransformation(Context context, int orientation) {
        this.context = context;
        this.mOrientation = orientation;
    }


    /**
     * Function: transform
     * Purpose: transforms bitmap>??
     * Parameters: BitmapPool pool = what is this
     *             Bitmap toTransform = what is this
     *             int outWidth = what is this
     *             int outHeight = what is this
     * Return: a new Bitmap
     */
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int newOrientation = getOrientation(mOrientation);
        return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
    }


    /**
     * Function: getOrientation()
     * Purpose: sets orientation of the images
     * Parameters: int orientation = new orientation
     * Return: newOrientation = the new orientation
     */
    private int getOrientation(int orientation) {
        int newOrientation;
        switch (orientation) {
            case 90:
                newOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            /** other cases */
            default:
                newOrientation = ExifInterface.ORIENTATION_NORMAL;
                break;
        }
        return newOrientation;
    }


    /**
     * Function: banished function xD
     * Purpose: N/A
     * Parameters: N/A
     * Return: none
     */
    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
