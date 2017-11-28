package com.example.dmitriymorozov.hometask1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class BackgroundIntentService extends IntentService {

    private static final String TAG = "MyLogs IntentService";

    public static final String ACTION_IMAGE_PREVIOUS = "com.example.dmitriymorozov.hometask1.action.PREVIOUS";
    public static final String ACTION_IMAGE_NEXT = "com.example.dmitriymorozov.hometask1.action.NEXT";
    public static final String ACTION_SLIDESHOW_START = "com.example.dmitriymorozov.hometask1.action.START";
    public static final String ACTION_SLIDESHOW_STOP = "com.example.dmitriymorozov.hometask1.action.STOP";

    // TODO: Rename parameters
    public static final String EXTRA_CURRENT_IMAGE = "com.example.dmitriymorozov.hometask1.extra.CURRENT_IMAGE";

    public BackgroundIntentService() {
        super("BackgroundIntentService");
    }

    public static void startSlideShow(Context context, String currentImageUri) {
        Intent intent = new Intent(context, BackgroundIntentService.class);
        intent.setAction(ACTION_SLIDESHOW_START);
        intent.putExtra(EXTRA_CURRENT_IMAGE, currentImageUri);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String currentImageUri = intent.getStringExtra(EXTRA_CURRENT_IMAGE);

            switch(action){
                case ACTION_IMAGE_PREVIOUS:
                    previousImage(currentImageUri);
                    break;
                case ACTION_IMAGE_NEXT:
                    nextImage(currentImageUri);
                    break;
                case ACTION_SLIDESHOW_START:
                    startSlideShow(currentImageUri);
                    break;
                case ACTION_SLIDESHOW_STOP:
                    stopSlideShow();
                    break;
            }
        }
    }

    private void previousImage(String currentImageUri) {
        Uri uri = Uri.parse(currentImageUri);
        int currentImageId = Integer.parseInt(uri.getLastPathSegment());
        Log.d(TAG, "previousImage. Serevice hash: " + this.hashCode());
    }

    private void nextImage(String currentImageUri) {
    }


    private void startSlideShow(String currentImageUri) {
        Log.d(TAG, "startSlideShow: ");
    }


    private void stopSlideShow() {
        Log.d(TAG, "stopSlideShow: ");
    }
}
