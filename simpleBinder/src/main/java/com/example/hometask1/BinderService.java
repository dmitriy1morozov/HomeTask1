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

		@Override public void onCreate() {
				Log.d(TAG, "onCreate: " + this.hashCode());
				super.onCreate();
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind: " + this.hashCode());
				return new LocalBinder();
		}

		@Override public boolean onUnbind(Intent intent) {
				Log.d(TAG, "onUnbind: " + this.hashCode());
				return super.onUnbind(intent);
		}

		@Override public int onStartCommand(Intent intent, int flags, int startId) {
				Log.d(TAG, "onStartCommand: " + this.hashCode());
				return super.onStartCommand(intent, flags, startId);
		}

		@Override public void onDestroy() {
				Log.d(TAG, "Destroy Service " + this.hashCode());
				if (mMyThread != null) {
						mMyThread.interrupt();
				}
				super.onDestroy();
				this.stopSelf();
		}

		//----------------------------------------------------------------------------------------------
		private class MyThread extends Thread {
				@Override public void run() {
						try {
								//while (!Thread.currentThread().isInterrupted()) {
										Log.d(TAG, "Thread " + this.hashCode() + " isRunning in Service " + BinderService.this.hashCode());
										Thread.currentThread().sleep(4000);
										Log.d(TAG, "Thread " + this.hashCode() + " finished tasks in Service " + BinderService.this.hashCode());
								//}
						} catch (InterruptedException ie) {
								Log.d(TAG, "Thread " + this.hashCode() + " interrupted in Service " + BinderService.this.hashCode());
						}
				}
		}

		//----------------------------------------------------------------------------------------------
		public class LocalBinder extends Binder {
				public void stopThread() {
						Log.d(TAG, "stopThread: ");
						if (mMyThread != null) {
								mMyThread.interrupt();
						}
				}

				public void startThread() {
						Log.d(TAG, "startThread: ");
						mMyThread = new MyThread();
						mMyThread.start();
				}
		}
}
