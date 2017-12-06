package com.example.hamer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.lang.ref.WeakReference;

public class SlideShowService extends Service{
		private static final String TAG = "MyLogs SlideShowService";

		public static final String ACTION_SLIDESHOW = "Slide Show";
		public static final String ACTION_NEXT_IMAGE = "Next Image";
		public static final String ACTION_PREVIOUS_IMAGE = "Previous Image";

		private String mAction;
		private Messenger mServiceMessenger;
		private HandlerThread mHandlerThread;

		public SlideShowService() {
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind: ");
				mAction = intent.getAction();
				return mServiceMessenger.getBinder();
		}

		@Override public void onCreate() {
				Log.d(TAG, "onCreate: ");
				super.onCreate();

				mHandlerThread = new HandlerThread("BackgroundThread");
				mHandlerThread.start();

				final Handler backgroundHandler = new Handler(mHandlerThread.getLooper()) {
						@Override public void handleMessage(Message clientRequestMessage) {
								switch (mAction){
										case ACTION_SLIDESHOW:
												startSlideShow(clientRequestMessage);
												break;
										case ACTION_NEXT_IMAGE:
												nextImage(clientRequestMessage);
												break;
										case ACTION_PREVIOUS_IMAGE:
												previousImage(clientRequestMessage);
												break;
								}
						}
				};
				mServiceMessenger = new Messenger(backgroundHandler);
		}

		@Override public int onStartCommand(Intent intent, int flags, int startId) {
				Log.d(TAG, "onStartCommand. Service = " + this.hashCode());
				return super.onStartCommand(intent, flags, startId);
		}

		@Override public boolean onUnbind(Intent intent) {
				Log.d(TAG, "onUnbind:");
				mHandlerThread.quit();
				mHandlerThread.interrupt();
				return super.onUnbind(intent);
		}

		@Override public void onDestroy() {
				Log.d(TAG, "Destroy Service");
				super.onDestroy();
		}

		//----------------------------------------------------------------------------------------------
		private void startSlideShow(Message clientMessage){
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");

				try {
						while (true) {
								Log.d(TAG, "handleMessage(). Long running task in Service = " + SlideShowService.this.hashCode());
								Thread.currentThread().sleep(4000);
								currentImageUri = getNextImage(currentImageUri);
								sendImageUriToActivity(currentImageUri, clientMessage);
						}
				} catch (InterruptedException ie) {
						Log.d(TAG, "handleMessage INTERRUPTED:");
				}
		}

		private void previousImage(Message clientMessage) {
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");
				Uri nextImageUri = getPreviousImage(currentImageUri);
				sendImageUriToActivity(nextImageUri, clientMessage);
				//TODO Need to unbind the service at the end
		}

		private void nextImage(Message clientMessage) {
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");
				Uri nextImageUri = getNextImage(currentImageUri);
				sendImageUriToActivity(nextImageUri, clientMessage);
				//TODO Need to unbind the service at the end
		}

		private void sendImageUriToActivity(Uri imageUri, Message message){
				Bundle replyData = new Bundle();
				replyData.putParcelable("uri", imageUri);
				Message serviceReplyMessage = Message.obtain();
				serviceReplyMessage.setData(replyData);
				try {
						message.replyTo.send(serviceReplyMessage);
				} catch (RemoteException re) {
						Log.d(TAG, "handleMessage: " + re.getMessage());
				}
		}


		private Uri getNextImage(Uri currentImageUri) {
				int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
				String baseUri = currentImageUri.toString()
						.substring(0, currentImageUri.toString().indexOf(String.valueOf(currentImageId)));
				if (currentImageId >= R.raw.cat5) {
						currentImageId = R.raw.cat1;
				} else {
						currentImageId++;
				}
				Uri nextImageUri = Uri.parse(baseUri.toString() + currentImageId);
				return nextImageUri;
		}

		private Uri getPreviousImage(Uri currentImageUri) {
				int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
				String baseUri = currentImageUri.toString()
						.substring(0, currentImageUri.toString().indexOf(String.valueOf(currentImageId)));
				if (currentImageId <= R.raw.cat1) {
						currentImageId = R.raw.cat5;
				} else {
						currentImageId--;
				}
				Uri nextImageUri = Uri.parse(baseUri.toString() + currentImageId);
				return nextImageUri;
		}
}