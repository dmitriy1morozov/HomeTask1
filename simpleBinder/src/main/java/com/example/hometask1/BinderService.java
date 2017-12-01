package com.example.hometask1;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BinderService extends Service {
    private static final String TAG = "MyLogs BinderService";

    private MyThread mMyThread;

    public BinderService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: " + this.hashCode());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + this.hashCode());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + this.hashCode());
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: " + this.hashCode());
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy Service " + this.hashCode());
        if (mMyThread != null && mMyThread.isAlive()) {
            mMyThread.interrupt();
        }
        super.onDestroy();
    }


    //----------------------------------------------------------------------------------------------
    public class LocalBinder extends Binder {
        public void stopThread() {
            Log.d(TAG, "stopThread: ");
            if (mMyThread != null && mMyThread.isAlive()) {
                mMyThread.interrupt();
            }else{
                Log.d(TAG, "stopThread: cannot stop. No thread found.");
            }
        }

        public void startThread() {
            Log.d(TAG, "startThread: ");
            if(mMyThread != null && mMyThread.isAlive()){
                //TODO stop old thread and start a new one or prevent new threads and leave a single one running depending on App logic
                //mMyThread.interrupt();
                return;
            }
            mMyThread = new MyThread(new OnNextImageListener() {
                @Override
                public void nextImage(Uri nextImage) {
                    Log.d(TAG, "nextImage received. Uri = " + nextImage.toString());
                }
            });
            mMyThread.start();
        }
    }
}
