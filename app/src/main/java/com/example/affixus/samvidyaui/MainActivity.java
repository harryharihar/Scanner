package com.example.affixus.samvidyaui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {
    private ImageView scannedImage;
    private int REQUEST_CODE = 99;
    LinearLayout ll_main;
    Button btn_camera,btn_show_saved_images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_show_saved_images = (Button) findViewById(R.id.btn_show_saved_images);
        scannedImage = (ImageView) findViewById(R.id.scannedImage);
        ll_main = (LinearLayout) findViewById(R.id.ll_main);

        btn_camera.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        btn_show_saved_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_main.setVisibility(View.GONE);
                RecyclerListFragment recyclerListFragment = new RecyclerListFragment();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.container,recyclerListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                /*Intent intent = new Intent(MainActivity.this, CapturedImagesFragment.class);
                startActivity(intent);*/
            }
        });
    }

    private class ScanButtonClickListener implements View.OnClickListener {
        private int preference;
        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        @Override
        public void onClick(View view) {
            startScan(preference);
        }
    }

    private void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                scannedImage.setVisibility(View.VISIBLE);
                ll_main.setVisibility(View.GONE);
                scannedImage.setImageBitmap(bitmap);
                btn_show_saved_images.setVisibility(View.VISIBLE);
                //Intent i=new Intent(Intent.ACTION_VIEW);

                //i.setDataAndType(Uri.fromFile(output), "image/jpeg");
               // startActivity(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);


                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
