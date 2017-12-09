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
		private HandlerThread mHandlerThread;
		private Handler mBackgroundHandler;
		private boolean isSlideShowRunning = false;

		public SlideShowService() {
		}

		@Override public IBinder onBind(Intent intent) {
				Log.d(TAG, "onBind: ");
				return mServiceMessenger.getBinder();
		}

		@Override public void onCreate() {
				Log.d(TAG, "onCreate: ");
				super.onCreate();

				mHandlerThread = new HandlerThread("BackgroundThread");
				mHandlerThread.start();

				mBackgroundHandler = new Handler(mHandlerThread.getLooper()) {
						@Override public void handleMessage(Message clientRequestMessage) {
								byte action = clientRequestMessage.getData().getByte("action");
								Log.d(TAG, "handleMessage: action = " + action);
								switch(action){
										case ACTION_SLIDESHOW_START:
												slideShow(clientRequestMessage);
												break;
										case ACTION_NEXT_IMAGE:
												nextImageAction(clientRequestMessage);
												break;
										case ACTION_PREVIOUS_IMAGE:
												previousImageAction(clientRequestMessage);
												break;
								}
						}
				};
				mServiceMessenger = new Messenger(mBackgroundHandler);
		}



		@Override public int onStartCommand(Intent intent, int flags, int startId) {
				Log.d(TAG, "onStartCommand. Service = " + this.hashCode());
				return super.onStartCommand(intent, flags, startId);
		}

		@Override public boolean onUnbind(Intent intent) {
				Log.d(TAG, "onUnbind:");
				isSlideShowRunning = false;
				mHandlerThread.quit();
				mHandlerThread.interrupt();
				return super.onUnbind(intent);
		}

		@Override public void onDestroy() {
				Log.d(TAG, "Destroy Service");
				super.onDestroy();
		}

		//----------------------------------------------------------------------------------------------
		private void slideShow(Message clientMessage){
				isSlideShowRunning = true;
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");

				try {
						Log.d(TAG, "handleMessage(). Long running task in Service = " + SlideShowService.this.hashCode());
						Thread.currentThread().sleep(4000);
						currentImageUri = getNextImage(currentImageUri);
						sendMessageToActivity(currentImageUri, clientMessage);
						//Process next image on the Service Handler
						sendMessageToService(currentImageUri, clientMessage, ACTION_SLIDESHOW_START);
				} catch (InterruptedException ie) {
						Log.d(TAG, "handleMessage INTERRUPTED:");
				}
		}

		private void previousImageAction(Message clientMessage) {
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");

				if(isSlideShowRunning){
						mHandlerThread.interrupt();
						Uri previousImageUri = getPreviousImage(currentImageUri);
						sendMessageToActivity(previousImageUri, clientMessage);
						mHandlerThread.start();
						sendMessageToService(previousImageUri, clientMessage, ACTION_SLIDESHOW_START);
				}else {
						Uri previousImageUri = getPreviousImage(currentImageUri);
						sendMessageToActivity(previousImageUri, clientMessage);
				}
		}

		private void nextImageAction(Message clientMessage) {
				Uri currentImageUri = clientMessage.getData().getParcelable("uri");

				if(isSlideShowRunning){
						Log.d(TAG, "nextImageAction: Interrupting the Thread");
						mHandlerThread.interrupt();
						Uri nextImageUri = getNextImage(currentImageUri);
						sendMessageToActivity(nextImageUri, clientMessage);
						mHandlerThread.start();
						sendMessageToService(nextImageUri, clientMessage, ACTION_SLIDESHOW_START);
				}else {
						Uri previousImageUri = getPreviousImage(currentImageUri);
						sendMessageToActivity(previousImageUri, clientMessage);
				}
		}

		private void sendMessageToActivity(Uri imageUri, Message message){
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

		private void sendMessageToService(Uri imageUri, Message message, byte action){
				Bundle data = new Bundle();
				data.putParcelable("uri", imageUri);
				data.putByte("action", action);
				message.setData(data);
				mBackgroundHandler.handleMessage(message);
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