package com.groomify.hollavirun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class CouponDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button scanQRButton;

    private final static String TAG = CouponDetailsActivity.class.getSimpleName();

    public static final int QR_REQUEST = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scanQRButton = (Button) findViewById(R.id.scan_qr_button);

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            requestQRCodeScan(v);
            }
        });
    }

    public void requestQRCodeScan(View v) {
        Intent qrScanIntent = new Intent(this, QRActivity.class);
        startActivityForResult(qrScanIntent, QR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode + ", data:" + data);

        if (requestCode == QR_REQUEST) {
            String result = "";
            if (resultCode == Activity.RESULT_OK) {
                String qrCodeData = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                if(getResources().getString(R.string.coupon_validation_code).equals(qrCodeData)){
                    result = "QR Code validation success. Coupon redeemed.";
                }else{
                    result = "Invalid redemption code.";
                }
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            } else {
                result = "Unable to verify QR Code";
            }
        }
    }


}
