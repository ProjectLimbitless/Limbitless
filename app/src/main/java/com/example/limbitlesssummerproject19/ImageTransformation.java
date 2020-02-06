package com.example.limbitlesssummerproject19;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

//  Image transformation from horizontal to vertical (used when working with Glide)
public class ImageTransformation extends BitmapTransformation {

    private int mOrientation;

    ImageTransformation(int orientation) {

        this.mOrientation = orientation;
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int newOrientation = getOrientation(mOrientation);
        return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
    }

    //  Sets orientation
    private int getOrientation(int orientation) {

        if (orientation == 90) {
            return ExifInterface.ORIENTATION_ROTATE_90;
            // other cases
        } else {
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
