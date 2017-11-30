package com.example.dmitriymorozov.hometask1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SlideShowService extends Service {
		private static final String TAG = "MyLogs SlideShowService";
		private SlideShowThread mSlideShowThread;

		public SlideShowService() {
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind()");
				//return null;
				//Using LocalBinder
				return new LocalBinder();
		}

		@Override public int onStartCommand(Intent intent, int flags, int startId) {
				if(startId > 1){
						Log.d(TAG, "onStartCommand btnNextClick. startId = " + startId);
				}
				if (startId == 1) {
						mSlideShowThread = new SlideShowThread();
						mSlideShowThread.start();
				}
				return super.onStartCommand(intent, flags, startId);
		}

		@Override public void onDestroy() {
				mSlideShowThread.interrupt();
				super.onDestroy();
		}

		//----------------------------------------------------------------------------------------------
		private class SlideShowThread extends Thread {
				@Override public void run() {
						try {
								while (!Thread.currentThread().isInterrupted()) {
										Thread.currentThread().sleep(3000);
										//TODO nextImage();
										Log.d(TAG, "run: nextImage()");
								}
						} catch (InterruptedException ie) {
								Log.d(TAG, "SlideShowThread interrupted");
						}
				}
		}

		//----------------------------------------------------------------------------------------------
		public class LocalBinder extends Binder{

		}

}