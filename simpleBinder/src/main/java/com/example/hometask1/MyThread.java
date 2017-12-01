package com.example.hometask1;

import android.net.Uri;
import android.util.Log;

public class MyThread extends Thread {
    private static final String TAG = "MyLogs MyThread";

    private OnNextImageListener mNextImageListener;

    public MyThread(OnNextImageListener listener) {
        mNextImageListener = listener;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.d(TAG, "Thread " + this.hashCode() + " isRunning");
                Thread.currentThread().sleep(4000);
                Log.d(TAG, "Thread " + this.hashCode() + " finished tasks");
                mNextImageListener.nextImage(Uri.parse("Next Image"));
            }
        } catch (InterruptedException ie) {
            Log.d(TAG, "Thread " + this.hashCode() + " interrupted");
        }
    }
}
