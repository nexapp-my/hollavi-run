package com.groomify.hollavirun;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.groomify.hollavirun.rest.models.response.RaceGalleryResponse;
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImageActivity extends AppCompatActivity {

    private final static String TAG = FullScreenImageActivity.class.getSimpleName();

    private ImageView imageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private View facebookShareBtn;

    private String imageFilePath;
    private String imageUrl;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private Bitmap bitmapForShare;
    ImageLoader imageLoader;

    private boolean removeable;
    private Bitmap decodedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(Color.TRANSPARENT);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);


        ImageLoadUtils.initImageLoader(this);
        imageLoader = ImageLoader.getInstance();

        imageView = (ImageView) findViewById(R.id.full_screen_image_view);
        progressBar = (ProgressBar) findViewById(R.id.image_loading_progress);
        facebookShareBtn = findViewById(R.id.facebook_share_button);

        imageView.setBackgroundColor(Color.parseColor("#000000"));

        imageFilePath = getIntent().getStringExtra("IMAGE_FILE_PATH");
        imageUrl = getIntent().getStringExtra("IMAGE_URL");
        //boolean hdMode = getIntent().getBooleanExtra("HD_MODE", false);
        /*boolean hdMode = false;

        if(imageFilePath != null){
            if(hdMode){
                progressBar.setVisibility(View.VISIBLE);
                new GroomifyGetRunnerGalleryTask().execute();
            }else{
                Log.i(TAG, "Display in normal mode.");
                final String uri = Uri.fromFile(new File(imageFilePath)).toString();
                final String decoded = Uri.decode(uri);

                imageLoader.displayImage(decoded, imageView, ImageLoadUtils.getDisplayImageOptions(), );
                mAttacher = new PhotoViewAttacher(imageView);
            }

            Log.i(TAG, "Full screen image viewer with image file path: "+imageFilePath);
        }else if(imageUrl != null){
            Log.i(TAG, "Full screen image viewer with url: "+imageUrl);
            loadImageFromUrl();
        }*/

        if(imageFilePath!= null) {
            final String uri = Uri.fromFile(new File(imageFilePath)).toString();
            final String decoded = Uri.decode(uri);
            imageUrl = decoded;
        }

        loadImageFromUrl();
        removeable = getIntent().getBooleanExtra("REMOVABLE_CAMERA_FILE", false);

        if(!removeable){
            getSupportActionBar().hide();
        }
        progressBar.setVisibility(View.INVISIBLE);




        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(FullScreenImageActivity.this, "Your photo has been shared to facebook.", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel() {
                //Toast.makeText(MissionDetailsActivity.this, "Post cancel :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Failed to share content to facebook. Errors: "+error.getMessage(),error.getCause() );
                Toast.makeText(FullScreenImageActivity.this, "Failed to share facebook post. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        facebookShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(SharePhotoContent.class)){

                    SharePhoto.Builder photoBuilder = new SharePhoto.Builder();

                    if(imageFilePath != null){
                        photoBuilder.setBitmap(BitmapUtils.loadBitmapFromFile(800, 600, imageFilePath));
                    }else{
                        photoBuilder.setBitmap(bitmapForShare);
                    }

                    String hashtag =  FullScreenImageActivity.this.getResources().getString(R.string.facebook_hashtag);
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photoBuilder.build())
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag(hashtag).build())
                            .build();

                    shareDialog.show(content);

                }else{
                    Toast.makeText(FullScreenImageActivity.this, "Your device does not support facebook share.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadImageFromUrl(){

        imageLoader.getInstance().loadImage(imageUrl,ImageLoadUtils.getDisplayImageOptions() ,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Log.i(TAG,"On loading started.");
                progressBar.setVisibility(View.VISIBLE);
                facebookShareBtn.setEnabled(false);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.i(TAG,"On loading failed.");
                progressBar.setVisibility(View.INVISIBLE);
                facebookShareBtn.setEnabled(true);
                Toast.makeText(FullScreenImageActivity.this, "Unable to load image.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.i(TAG,"On loading completed.");
                progressBar.setVisibility(View.INVISIBLE);
                facebookShareBtn.setEnabled(true);
                imageView.setImageBitmap(loadedImage);
                try {
                    Log.i(TAG, "URL: "+imageUri+" loaded complete. Bitmap: "+loadedImage);
                    Log.i(TAG, "URL: "+imageUri+" loaded complete. Config: "+loadedImage.getConfig().toString());
                    bitmapForShare = BitmapUtils.cropBitmap(800, 600, loadedImage);
                }catch (Exception e){
                    Log.e(TAG, "Unable to crop GIF.", e);
                    try{
                        bitmapForShare = loadedImage.copy(Bitmap.Config.RGB_565, false);
                    }catch (Exception ex){
                        Log.e(TAG, "Failed to copy the GIF as well.", ex);
                    }
                }
                mAttacher = new PhotoViewAttacher(imageView);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.INVISIBLE);
                facebookShareBtn.setEnabled(true);
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*View decorView  = getWindow().getDecorView();
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void removeFile(){
        Log.i(TAG, "Deleting file: "+imageFilePath);
        if(imageFilePath != null){
            File imageFile = new File(imageFilePath);
            if(imageFile.exists()){
                boolean deleted = imageFile.delete();
                Log.i(TAG, "File deleted? "+deleted);
                if(deleted){
                    Intent intent = new Intent();
                    intent.putExtra(RunGalleryActivity.REQUIRE_RELOAD_LOCAL_FILE, true);
                    intent.putExtra(RunGalleryActivity.DELETED_FILE, imageFile);
                    setResult(Activity.RESULT_OK, intent);
                    super.onBackPressed();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_screen_image_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_delete:
                promptConfirmation();
                break;
        }

        return true;
    }

    private void promptConfirmation(){
        new AlertDialog.Builder(this)
                .setMessage("Delete this photo?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFile();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class GroomifyGetRunnerGalleryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "Display in HD mode.");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            decodedBitmap = BitmapFactory.decodeFile(imageFilePath, options);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(decodedBitmap);
            mAttacher = new PhotoViewAttacher(imageView);
        }
    }
}
