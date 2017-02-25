package com.groomify.hollavirun.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Valkyrie1988 on 11/12/2016.
 */

public class BitmapUtils {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap cropBitmap(int targetW, int targetH, String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return cropBitmap(targetW, targetH, bitmap);
    }

    public static Bitmap cropBitmap(int targetW, int targetH, Bitmap originalBitmap){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        //BitmapFactory.decodeFile(imagePath, bmOptions);

        //int photoW = originalBitmap.getWidth();//bmOptions.outWidth;
        //int photoH = originalBitmap.getHeight();//bmOptions.outHeight;

        int squareSize = Math.min(targetH, targetW);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        //BitmapUtils.calculateInSampleSize(bmOptions, targetW, targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inSampleSize =  BitmapUtils.calculateInSampleSize(bmOptions, targetW, targetH);
        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        //originalBitmap.recycle();
        bitmap  = ThumbnailUtils.extractThumbnail(bitmap, squareSize, squareSize);

        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(int targetW, int targetH, String photoPath){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        options.inSampleSize =  BitmapUtils.calculateInSampleSize(options, targetW, targetH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    public static String convertToBase64(String imagePath)
    {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = "data:image/jpeg;base64,"+ Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public static String convertToBase64(Bitmap originalBitmap)
    {
        Bitmap bm = originalBitmap.copy(originalBitmap.getConfig(), true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = "data:image/jpeg;base64,"+Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodeFromBase64ToBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public static byte[] loadFileToJpegByte(int targetW, int targetH, String photoPath){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        options.inSampleSize =  BitmapUtils.calculateInSampleSize(options, targetW, targetH);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(photoPath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return byteArrayImage;
    }

    public static String loadFileToJpegBase64(int targetW, int targetH, String photoPath){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        options.inSampleSize =  BitmapUtils.calculateInSampleSize(options, targetW, targetH);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(photoPath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = "data:image/jpeg;base64,"+Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }
}
