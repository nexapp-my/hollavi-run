package com.groomify.hollavirun.adapter;

/**
 * Created by Valkyrie1988 on 2/26/2017.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.groomify.hollavirun.FullScreenImageActivity;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class GridViewImageAdapter extends BaseAdapter {

    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;

    boolean pauseOnScroll = false; // or true
    boolean pauseOnFling = true; // or false

    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;

    public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
                                int imageWidth) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        ImageLoadUtils.initImageLoader(_activity);

        imageLoader = ImageLoader.getInstance();
        displayImageOptions = ImageLoadUtils.getDisplayRoundedImageOptions();

        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

    }

    @Override
    public int getCount() {
        return this._filePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }

        // get screen dimensions
        Log.v("GridViewImageAdapter", "Displaying: "+position);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));

        if(!isRemoteImageFile(_filePaths.get(position))){
            final String uri = Uri.fromFile(new File(_filePaths.get(position))).toString();
            final String decoded = Uri.decode(uri);

            imageLoader.displayImage(decoded, imageView, displayImageOptions);
            /*Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
                    imageWidth);
            imageView.setImageBitmap(image);*/
        }else{
            imageLoader.displayImage(_filePaths.get(position), imageView, displayImageOptions);
        }

        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));

        return imageView;
    }

    class OnImageClickListener implements OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent intent = new Intent(_activity, FullScreenImageActivity.class);
            if(isRemoteImageFile(_filePaths.get(_postion))){
                intent.putExtra("IMAGE_URL", _filePaths.get(_postion));
            }else{
                intent.putExtra("IMAGE_FILE_PATH", _filePaths.get(_postion));
            }
            _activity.startActivity(intent);
        }

    }

    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isRemoteImageFile(String path){
        return path.startsWith("http:") || path.startsWith("https:");
    }

}
