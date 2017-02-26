package com.groomify.hollavirun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.groomify.hollavirun.utils.AppUtils;
import com.groomify.hollavirun.utils.BitmapUtils;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.ProfileImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ImageLoadUtils.initImageLoader(this);

        imageView = (ImageView) findViewById(R.id.full_screen_image_view);
        progressBar = (ProgressBar) findViewById(R.id.image_loading_progress);
        facebookShareBtn = findViewById(R.id.facebook_share_button);

        imageView.setBackgroundColor(Color.parseColor("#000000"));

        imageFilePath = getIntent().getStringExtra("IMAGE_FILE_PATH");
        imageUrl = getIntent().getStringExtra("IMAGE_URL");


        if(imageFilePath != null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "Full screen image viewer with image file path: "+imageFilePath);
        }else if(imageUrl != null){
            Log.i(TAG, "Full screen image viewer with url: "+imageFilePath);
            loadImageFromUrl();
        }

        progressBar.setVisibility(View.GONE);

        mAttacher = new PhotoViewAttacher(imageView);

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

                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photoBuilder.build())
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("Groomify").build())
                            .build();

                    shareDialog.show(content);

                }else{
                    Toast.makeText(FullScreenImageActivity.this, "Your device does not support facebook share.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadImageFromUrl(){
        ImageLoader.getInstance().loadImage(imageUrl,ImageLoadUtils.getDisplayImageOptions() ,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                bitmapForShare = BitmapUtils.cropBitmap(800, 600, loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        View decorView  = getWindow().getDecorView();
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult, requestCode: "+requestCode+", resultCode: "+resultCode+", data:"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
