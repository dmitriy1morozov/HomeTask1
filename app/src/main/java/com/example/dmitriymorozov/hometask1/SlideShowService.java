package com.example.dmitriymorozov.hometask1;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SlideShowService extends Service {
    private static final String TAG = "MyLogs SlideShowService";

    private SlideShowThread mSlideShowThread;
    private Uri mBaseUri;
    private Uri mCurrentImageUri;

    public SlideShowService() {
    }

    @Override
    public void onCreate() {
        mBaseUri = Uri.parse("android.resource://" + getPackageName() + "/");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1) {
            mCurrentImageUri = intent.getParcelableExtra("currentImage");
            mSlideShowThread = new SlideShowThread();
            mSlideShowThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mSlideShowThread.interrupt();
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------------
    private class SlideShowThread extends Thread {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().sleep(2000);
                    int currentImageId = Integer.parseInt(mCurrentImageUri.getLastPathSegment());
                    if(currentImageId >= R.raw.cat5){
                        currentImageId = R.raw.cat1;
                    } else{
                        currentImageId++;
                    }
                    mCurrentImageUri = Uri.parse(mBaseUri.toString() + currentImageId);
                    //TODO send broadcast containing URI for new image
                    Log.d(TAG, "run: nextImage()");
                }
            } catch (InterruptedException ie) {
                Log.d(TAG, "SlideShowThread interrupted");
            }
        }
    }
}