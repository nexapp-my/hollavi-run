package com.groomify.hollavirun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.groomify.hollavirun.camera.CameraSourcePreview;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by Valkyrie1988 on 10/24/2016.
 */

public class QRActivity extends AppCompatActivity {

    public static final String EXTRA_QR_RESULT = "EXTRA_QR_RESULT";
    private static final String TAG = "QRActivity";
    private static final int PERMISSIONS_REQUEST = 100;

    private BarcodeDetector mBarcodeDetector;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private TextView cancelTextView;
    private ImageView qrHelpImageView;

    private String helpText = "Having trouble finding QR codes? Here is a picture to show you what it looks like.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mPreview = (CameraSourcePreview) findViewById(R.id.cameraSourcePreview);

        if (isPermissionGranted()) {
            setupBarcodeDetector();
            setupCameraSource();
        } else {
            requestPermission();
        }

        if(cancelTextView == null){
            cancelTextView = (TextView) findViewById(R.id.cancel_text_view);
        }

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRActivity.this.onBackPressed();
            }
        });

        if(qrHelpImageView == null){
            qrHelpImageView = (ImageView) findViewById(R.id.qr_help_image_view);
        }
        helpText = getResources().getString(R.string.qr_help_text);


        qrHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO launch help activity
                Toast.makeText(QRActivity.this, helpText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted())
            startCameraSource();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    recreate();
                }
            } else {
                Toast.makeText(this, "This application needs Camera permission to read QR codes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupBarcodeDetector() {
        mBarcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    String data = barcodes.valueAt(0).displayValue;
                    Log.d(TAG, "Barcode detected: " + data);
                    playBeep();
                    returnData(data);
                }
            }
        });

        if (!mBarcodeDetector.isOperational())
            Log.w(TAG, "Detector dependencies are not yet available.");
    }

    private void setupCameraSource() {
        mCameraSource = new CameraSource.Builder(getApplicationContext(), mBarcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        Log.d(TAG, "Camera Source started");
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void playBeep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    private void returnData(String data) {
        Log.i(TAG, "Return data: "+data);
        if (data != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_QR_RESULT, data);
            setResult(RESULT_OK, resultIntent);
            Log.i(TAG, "Putting OK: "+data);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
}
