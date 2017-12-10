package com.example.hamer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.transition.Slide;
import android.util.Log;

public class SlideShowService extends Service{
		private static final String TAG = "MyLogs SlideShowService";

		public static final byte ACTION_SLIDESHOW_START = 1;
		public static final byte ACTION_PREVIOUS_IMAGE = 2;
		public static final byte ACTION_NEXT_IMAGE = 3;

		private Messenger mServiceMessenger;
		private Messenger mClientMessenger;
		private HandlerThread mHandlerThread;
		private Handler mBackgroundHandler;
		private boolean isSlideShowRunning = false;

		public SlideShowService() {
		}

		@Override public void onCreate() {
				Log.d(TAG, "onCreate: ");
				super.onCreate();

				mHandlerThread = new HandlerThread("BackgroundThread");
				mHandlerThread.start();

				mBackgroundHandler = new Handler(mHandlerThread.getLooper()) {
						@Override public void handleMessage(Message clientRequestMessage) {
								if(mClientMessenger == null){
										mClientMessenger = clientRequestMessage.replyTo;
								}
								Uri currentImageUri = clientRequestMessage.getData().getParcelable("uri");
								byte action = clientRequestMessage.getData().getByte("action");
								Log.d(TAG, "handleMessage: action = " + action);
								switch(action){
										case ACTION_SLIDESHOW_START:
												slideShow(currentImageUri);
												break;
										case ACTION_NEXT_IMAGE:
												nextImageAction(currentImageUri);
												break;
										case ACTION_PREVIOUS_IMAGE:
												previousImageAction(currentImageUri);
												break;
								}
						}
				};
				mServiceMessenger = new Messenger(mBackgroundHandler);
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind: ");
				return mServiceMessenger.getBinder();
		}

		@Override public boolean onUnbind(Intent intent) {
				Log.d(TAG, "onUnbind:");
				isSlideShowRunning = false;
				mBackgroundHandler.removeCallbacksAndMessages(null);
				mHandlerThread.quit();
				return super.onUnbind(intent);
		}

		//----------------------------------------------------------------------------------------------
		private void slideShow(Uri currentImageUri){
				Log.d(TAG, "handleMessage(). Long running task in Service = " + SlideShowService.this.hashCode());
				isSlideShowRunning = true;
				sendMessageToActivity(currentImageUri);

				currentImageUri = getNextImage(currentImageUri);
				sendMessageToService(currentImageUri, ACTION_SLIDESHOW_START, 2000);
		}

		private void previousImageAction(Uri currentImageUri){
				mBackgroundHandler.removeCallbacksAndMessages(null);
				Uri previousImageUri = getPreviousImage(currentImageUri);
				if(isSlideShowRunning){
						sendMessageToService(previousImageUri, ACTION_SLIDESHOW_START, 0);
				}else{
						sendMessageToActivity(previousImageUri);
				}
		}

		private void nextImageAction(Uri currentImageUri) {
				mBackgroundHandler.removeCallbacksAndMessages(null);
				Uri nextImageUri = getNextImage(currentImageUri);
				if(isSlideShowRunning){
						sendMessageToService(nextImageUri, ACTION_SLIDESHOW_START, 0);
				}else{
						sendMessageToActivity(nextImageUri);
				}
		}

		private void sendMessageToActivity(Uri imageUri){
				Bundle clientData = new Bundle();
				clientData.putParcelable("uri", imageUri);
				Message clientMessage = Message.obtain();
				clientMessage.setData(clientData);
				try {
						if(mClientMessenger != null){
								mClientMessenger.send(clientMessage);
						}
				} catch (RemoteException re) {
						Log.d(TAG, "handleMessage: " + re.getMessage());
				}
		}

		private void sendMessageToService(Uri imageUri, byte action, int delay){
				Message message = Message.obtain(mBackgroundHandler);
				Bundle data = new Bundle();
				data.putParcelable("uri", imageUri);
				data.putByte("action", action);
				message.setData(data);
				mBackgroundHandler.sendMessageDelayed(message, delay);
		}

		/**
		 * Potentially long running task
		 */
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

		/**
		 * Potentially long running task
		 */
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