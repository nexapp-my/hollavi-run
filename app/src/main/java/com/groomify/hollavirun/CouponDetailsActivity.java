package com.groomify.hollavirun;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groomify.hollavirun.constants.AppConstant;
import com.groomify.hollavirun.entities.Coupon;
import com.groomify.hollavirun.utils.ImageLoadUtils;
import com.groomify.hollavirun.utils.SharedPreferencesHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CouponDetailsActivity extends AppCompatActivity {

    Coupon coupon;

    Toolbar toolbar;
    Button scanQRButton;

    private ImageView couponImageHeader;
    private TextView couponName;
    private TextView expirationTime;


    private final static String TAG = CouponDetailsActivity.class.getSimpleName();

    public static final int QR_REQUEST = 111;

    private Long raceId;

    private boolean redeemed = false;
    private boolean expired = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        Bundle extras = getIntent().getExtras().getBundle("EXTRA_COUPON");
        if (extras != null) {
            coupon = extras.getParcelable("COUPON");
        }
        Log.i(TAG, "Mission from main screen: "+coupon.toString());

        raceId = SharedPreferencesHelper.getSelectedRaceId(this);
        redeemed = SharedPreferencesHelper.isCouponRedeemed(this, raceId, coupon.getId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scanQRButton = (Button) findViewById(R.id.scan_qr_button);

        couponImageHeader = (ImageView) findViewById(R.id.coupon_header_image_view);
        couponName = (TextView) findViewById(R.id.coupon_detail_title_text_view);
        expirationTime = (TextView) findViewById(R.id.coupon_details_expiration_text_view);

        ImageLoader.getInstance().displayImage(coupon.getCoverPhotoUrl(), couponImageHeader, ImageLoadUtils.getDisplayImageOptions());

        couponName.setText(coupon.getName());

        long millisDiff = coupon.getExpirationTime().getTime() - Calendar.getInstance().getTime().getTime();

        if(millisDiff > 0){
            long hours = TimeUnit.MICROSECONDS.toHours(millisDiff);
            expirationTime.setText("Expires in "+hours+" hours");
        }else{
            expirationTime.setText("Expired");
            expired = true;
        }

        toggleButton();

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            requestQRCodeScan(v);
            }
        });


    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(this, QRActivity.class);
        qrScanIntent.setClassName("com.groomify.run", "com.groomify.hollavirun.QRActivity");
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode + ", data:" + data);

        if (requestCode == QR_REQUEST) {
            String result = "";
            if (resultCode == Activity.RESULT_OK) {
                String qrCodeData = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                int resId = getResources().getIdentifier("coupon_validation_code_"+coupon.getId(), "string", getPackageName());
                if(getResources().getString(resId).equals(qrCodeData)){
                    result = "QR Code validation success. Coupon redeemed.";
                    SharedPreferencesHelper.setCouponRedeemed(this, raceId, coupon.getId(), true);
                    redeemed = true;
                    toggleButton();
                }else{
                    result = "Invalid redemption code.";
                }
            } else {
                result = "Unable to verify QR Code";
            }
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleButton(){
        if(redeemed){
            scanQRButton.setBackgroundColor(ContextCompat.getColor(this,R.color.redeemedGreen));
            scanQRButton.setEnabled(false);
            scanQRButton.setText("Redeemed");
        }else if(expired){
            scanQRButton.setEnabled(false);
            scanQRButton.setText("Expired");
        }
    }

}
