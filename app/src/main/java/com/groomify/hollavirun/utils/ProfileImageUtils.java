package com.groomify.hollavirun.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.groomify.hollavirun.view.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;

/**
 * Created by Valkyrie1988 on 11/12/2016.
 */

public class ProfileImageUtils {


    public static Bitmap processOptimizedRoundBitmap(int targetH, int targetW, String imagePath){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        int squareSize = Math.min(targetH, targetW);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        bitmap  = ThumbnailUtils.extractThumbnail(bitmap, squareSize, squareSize);
        bitmap = ProfilePictureView.getRoundedBitmap(bitmap);

        return bitmap;
    }
    public static Bitmap processOptimizedRoundBitmap(int targetW, int targetH, Bitmap originalBitmap){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        //BitmapFactory.decodeFile(imagePath, bmOptions);

        int photoW = originalBitmap.getWidth();//bmOptions.outWidth;
        int photoH = originalBitmap.getHeight();//bmOptions.outHeight;

        int squareSize = Math.min(photoH, photoW);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        bitmap  = ThumbnailUtils.extractThumbnail(bitmap, squareSize, squareSize);
        bitmap = ProfilePictureView.getRoundedBitmap(bitmap);

        return bitmap;
    }

}
