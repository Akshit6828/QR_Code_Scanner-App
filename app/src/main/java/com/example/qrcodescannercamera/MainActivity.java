package com.example.qrcodescannercamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    SurfaceView surfaceView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    surfaceView=findViewById(R.id.surfaceView);
    textView=findViewById(R.id.textView);


    barcodeDetector= new BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();

    cameraSource=new CameraSource.Builder(this,barcodeDetector)
            .setRequestedPreviewSize(580,580)
            .build();

    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

        // It is called at the time the surface is created.
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {


            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, // Context
                        new String[]{Manifest.permission.CAMERA}, // Array of permission to be asked
                        0 // A particular unique code for request
                );
            }

            try {
                cameraSource.start(holder);
            }
            catch (Exception e){
                Log.d("TAG1",e.toString());
            }
        }

        // Called at least one time after the surface is created.
        // Called when there is a structural change(Size or Format) to the surface view.
        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            //Nothing to code...


        }
        /* This is called immediately before a surface is being destroyed.
        After retrieving from the call, you should no longer try to
        access the surface */


        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            cameraSource.stop();
        }
    });



    // Barcode processing....

    barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {

            final SparseArray<Barcode> qr_code= detections.getDetectedItems();

            if(qr_code.size()!=0){

                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator vibrator= (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(10000);
                        textView.setText(qr_code.valueAt(0).displayValue);
                    }
                });

            }
            else{
                Toast.makeText(MainActivity.this, "Please try Again!", Toast.LENGTH_SHORT).show();
            }


        }
    });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=textView.getText().toString();

                if(url.contains("www")||url.contains(".in")||url.contains(".com")||url.contains(".live"))
                {
                    Intent i= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Nothing scanned !!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
