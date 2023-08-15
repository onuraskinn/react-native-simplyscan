package com.simplydeliver.simplyscan;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;
import android.graphics.Bitmap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.util.Log;

import java.io.File;
import java.io.ByteArrayOutputStream;
import android.util.Base64;

public class Module extends ReactContextBaseJavaModule implements ActivityEventListener {

    File photoFile;
    private Context context;
    private Promise scanPromise;
    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
    private static final String EXTRA_CROPPED_FILE = "extra_cropped_file";

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            /*if (resultCode != RESULT_OK) {
                return;
            }*/
            if (requestCode == 100 && photoFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
                String photo = BitMapToString(bitmap);
                if (scanPromise != null) {
                    scanPromise.resolve(photo);
                }
            }
            scanPromise = null;
        }
    };

    public Module(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "SimplyScanView";
    }

    @ReactMethod
    public void startScan(final Promise promise) {
        Activity currentActivity = getCurrentActivity();

        scanPromise = promise;

        photoFile = new File(context.getExternalFilesDir("img"), "scan.jpg");

        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_FROM_ALBUM, false);
        intent.putExtra(EXTRA_CROPPED_FILE, photoFile);

        currentActivity.startActivityForResult(CropActivity.getJumpIntent(context, false, photoFile), 100);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b = baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
   }

}