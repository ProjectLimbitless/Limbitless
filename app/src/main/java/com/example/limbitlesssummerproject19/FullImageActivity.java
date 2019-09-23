package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

public class FullImageActivity extends AppCompatActivity {

    ImageView imageView;
    String singleImageURL = "";
    String backtrackingURL = "";
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        //  single image gets displayed into the user
        singleImageURL = getIntent().getStringExtra("image_url");

        //  saves the absolute path of the images
        backtrackingURL = getIntent().getStringExtra("folderName");

        //  displays the image to the user
        imageView = findViewById(R.id.my_image);

        Glide.with(this)
                .load(singleImageURL)
                .centerCrop()
                .transform(new FullImageActivity.ImageTransformation(imageView.getContext(),
                        90))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }

    //  Image transformation from horizontal to vertical (used when working with Glide)
    public class ImageTransformation extends BitmapTransformation {

        private Context context;
        private int mOrientation;

        public ImageTransformation(Context context, int orientation) {
            this.context = context;
            this.mOrientation = orientation;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int newOrientation = getOrientation(mOrientation);
            return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
        }

        //  Sets orientation of the images
        private int getOrientation(int orientation) {
            int newOrientation;
            switch (orientation) {
                case 90:
                    newOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                // other cases
                default:
                    newOrientation = ExifInterface.ORIENTATION_NORMAL;
                    break;
            }
            return newOrientation;
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    //  ConCreateOptionMenu and onOptionItemsSelected create a return button
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_items, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //  Retrun to the album activity using the previous URL (backtracking steps)
            case R.id.return_button:

                Intent intent = new Intent(this, AlbumActivity.class);

                intent.putExtra("fileName", backtrackingURL);

                this.startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

