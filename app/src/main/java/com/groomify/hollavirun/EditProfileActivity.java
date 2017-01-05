package com.groomify.hollavirun;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.groomify.hollavirun.utils.ProfileImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();
    ImageView pictureView = null;
    View saveButton;
    View cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if(pictureView == null)
            pictureView = (ImageView) findViewById(R.id.profile_picture_image_view);


        if(saveButton == null)
            saveButton = findViewById(R.id.save_text_view);

        if(cancelButton == null)
            cancelButton = findViewById(R.id.cancel_text_view);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        loadProfileImageFromStorage();
        populateCountryListing();
    }

    private void save(){

        this.onBackPressed();
    }

    private void cancel(){
        this.onBackPressed();
    }

    private  void populateCountryListing(){
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        Spinner citizenship = (Spinner)findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);
    }

    private void loadProfileImageFromStorage()
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        try {
            File f = new File(directory,"profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Bitmap optimizedProfilePic = ProfileImageUtils.processOptimizedRoundBitmap(72, 72, b);
            pictureView.setImageBitmap(optimizedProfilePic);
            Log.i(TAG, "Action bar profile picture loaded");
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "Unable to find profile picture.", e);
        }

    }
}
