package com.example.dmitriymorozov.hometask1;

import android.net.Uri;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SlideShowThread extends Thread {
    private static final String TAG = "MyLogs SlideShowThread";
    private WeakReference<OnNextImageListener> mNextImageListener;
    private Uri mCurrentImageUri;

    public SlideShowThread(Uri currentImageUri, OnNextImageListener listener) {
        mCurrentImageUri = currentImageUri;
        mNextImageListener = new WeakReference<>(listener);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.currentThread().sleep(2000);
                Log.d(TAG, "Thread " + this.hashCode() + " isRunning");
                mCurrentImageUri = getNextImage(mCurrentImageUri);
                if(mNextImageListener.get() != null){
                    mNextImageListener.get().onNextImageReceived(mCurrentImageUri);
                } else{
                    //Stop the thread tasks if any and terminate the thread appropriately.
                    // We just lost our reference and no further actions required.
                }
            }
        } catch (InterruptedException ie) {
            Log.d(TAG, "SlideShowThread interrupted");
        }
    }


    private Uri getNextImage(Uri currentImageUri) {
        int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
        String baseUri = currentImageUri.toString().substring(0, currentImageUri.toString().indexOf(String.valueOf(currentImageId)));
        if (currentImageId >= R.raw.cat5) {
            currentImageId = R.raw.cat1;
        } else {
            currentImageId++;
        }
        Uri nextImageUri = Uri.parse(baseUri.toString() + currentImageId);
        return nextImageUri;
    }
}
