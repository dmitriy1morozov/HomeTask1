package com.example.dmitriymorozov.hometask1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

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
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCurrentImageUri = intent.getParcelableExtra("uri");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy Service");
        super.onDestroy();
        if(mSlideShowThread != null){
            mSlideShowThread.interrupt();
        }
    }

    //----------------------------------------------------------------------------------------------
    private Uri getNextImage(Uri currentImageUri) {
        int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
        if (currentImageId >= R.raw.cat5) {
            currentImageId = R.raw.cat1;
        } else {
            currentImageId++;
        }
        Uri nextImageUri = Uri.parse(mBaseUri.toString() + currentImageId);
        return nextImageUri;
    }
    private Uri getPreviousImage(Uri currentImageUri) {
        int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
        if (currentImageId <= R.raw.cat1) {
            currentImageId = R.raw.cat5;
        } else {
            currentImageId--;
        }
        Uri previousImageUri = Uri.parse(mBaseUri.toString() + currentImageId);
        return previousImageUri;
    }


    //----------------------------------------------------------------------------------------------
    private class SlideShowThread extends Thread {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.currentThread().sleep(2000);
                    Log.d(TAG, "Thread " + this.hashCode() + " isRunning");
                    mCurrentImageUri = getNextImage(mCurrentImageUri);
                    Intent broadcast = new Intent(MainActivity.BROADCAST_ACTION);
                    broadcast.putExtra("uri", mCurrentImageUri);
                    sendBroadcast(broadcast);
                }
            } catch (InterruptedException ie) {
                Log.d(TAG, "SlideShowThread interrupted");
            }
        }
    }
    //----------------------------------------------------------------------------------------------
		public class LocalBinder extends Binder{
				public void nextImage(Uri currentImageUri){
				    mCurrentImageUri = SlideShowService.this.getNextImage(currentImageUri);
            Intent broadcast = new Intent(MainActivity.BROADCAST_ACTION);
            broadcast.putExtra("uri", mCurrentImageUri);
            sendBroadcast(broadcast);
				    if(mSlideShowThread != null && mSlideShowThread.isAlive()){
                Log.d(TAG, "getNextImage: restart Thread");
                mSlideShowThread.interrupt();
                mSlideShowThread = new SlideShowThread();
						    mSlideShowThread.start();
            }
				}

        public void previousImage(Uri currentImageUri) {
				    mCurrentImageUri = SlideShowService.this.getPreviousImage(currentImageUri);
            Intent broadcast = new Intent(MainActivity.BROADCAST_ACTION);
            broadcast.putExtra("uri", mCurrentImageUri);
            sendBroadcast(broadcast);
            if(mSlideShowThread != null && mSlideShowThread.isAlive()){
                Log.d(TAG, "getPreviousImage: restart Thread");
                mSlideShowThread.interrupt();
                mSlideShowThread = new SlideShowThread();
                mSlideShowThread.start();
            }
        }

        public void stopSlideShow() {
            if(mSlideShowThread != null) {
                mSlideShowThread.interrupt();
                Log.d(TAG, "stopSlideShow: ");
            }
        }

        public void startSlideShow(Uri currentImageUri) {
				    mCurrentImageUri = currentImageUri;
            mSlideShowThread = new SlideShowThread();
            mSlideShowThread.start();
        }
    }
}