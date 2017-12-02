package com.example.dmitriymorozov.hometask1;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.net.Uri;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.lang.ref.WeakReference;

public class SlideShowService extends Service implements OnNextImageListener {
<<<<<<< HEAD
		private static final String TAG = "MyLogs SlideShowService";

		private SlideShowThread mSlideShowThread;
		private Uri mCurrentImageUri;

		public SlideShowService() {
		}

		@Override public IBinder onBind(Intent intent) {
				return new LocalBinder();
		}

		@Override public void onCreate() {
				super.onCreate();
		}

		@Override public int onStartCommand(Intent intent, int flags, int startId) {
				mCurrentImageUri = intent.getParcelableExtra("uri");
				return super.onStartCommand(intent, flags, startId);
		}

		@Override public void onDestroy() {
				Log.d(TAG, "Destroy Service");
				super.onDestroy();
				if (mSlideShowThread != null) {
						mSlideShowThread.interrupt();
						mSlideShowThread = null;
				}
		}

		@Override public void onNextImageReceived(Uri nextImage) {
				mCurrentImageUri = nextImage;
				Intent localBroadcastIntent = new Intent(MainActivity.INTENT_FILTER_RECEIVED_IMAGE);
				localBroadcastIntent.putExtra("uri", mCurrentImageUri);
				LocalBroadcastManager.getInstance(this).sendBroadcast(localBroadcastIntent);
		}

		//----------------------------------------------------------------------------------------------
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

		private void startSlideShowThread() {
				mSlideShowThread = new SlideShowThread(mCurrentImageUri, this);
				mSlideShowThread.start();
		}

		//----------------------------------------------------------------------------------------------
		public class LocalBinder extends Binder {
				public void nextImage(Uri currentImageUri) {
						mCurrentImageUri = SlideShowService.this.getNextImage(currentImageUri);

						if (mSlideShowThread != null && mSlideShowThread.isAlive()) {
								stopSlideShow();
								startSlideShowThread();
						}
						onNextImageReceived(mCurrentImageUri);
				}

				public void previousImage(Uri currentImageUri) {
						mCurrentImageUri = SlideShowService.this.getPreviousImage(currentImageUri);

						if (mSlideShowThread != null && mSlideShowThread.isAlive()) {
								stopSlideShow();
								startSlideShowThread();
						}
						onNextImageReceived(mCurrentImageUri);
				}

				public void stopSlideShow() {
						if (mSlideShowThread != null) {
								mSlideShowThread.interrupt();
								mSlideShowThread = null;
								Log.d(TAG, "stopSlideShow: ");
						}
				}

				public void startSlideShow(Uri currentImageUri) {
						mCurrentImageUri = currentImageUri;
						startSlideShowThread();
				}
		}
=======
    private static final String TAG = "MyLogs SlideShowService";

    private SlideShowThread mSlideShowThread;
    private Uri mCurrentImageUri;

    public SlideShowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        if (mSlideShowThread != null) {
            mSlideShowThread.interrupt();
            mSlideShowThread = null;
        }
    }

    @Override
    public void onNextImageReceived(Uri nextImage) {
        mCurrentImageUri = nextImage;
        Intent broadcast = new Intent(MainActivity.BROADCAST_ACTION);
        broadcast.putExtra("uri", mCurrentImageUri);
        sendBroadcast(broadcast);
    }

    //----------------------------------------------------------------------------------------------
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

    private Uri getPreviousImage(Uri currentImageUri) {
        int currentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
        String baseUri = currentImageUri.toString().substring(0, currentImageUri.toString().indexOf(String.valueOf(currentImageId)));
        if (currentImageId <= R.raw.cat1) {
            currentImageId = R.raw.cat5;
        } else {
            currentImageId--;
        }
        Uri nextImageUri = Uri.parse(baseUri.toString() + currentImageId);
        return nextImageUri;
    }

    private void startSlideShowThread() {
        mSlideShowThread = new SlideShowThread(mCurrentImageUri, this);
        mSlideShowThread.start();
    }

    //----------------------------------------------------------------------------------------------
    public class LocalBinder extends Binder {
        public void nextImage(Uri currentImageUri) {
            mCurrentImageUri = SlideShowService.this.getNextImage(currentImageUri);

            if (mSlideShowThread != null && mSlideShowThread.isAlive()) {
                stopSlideShow();
                startSlideShowThread();
            }
            
        }

        public void previousImage(Uri currentImageUri) {
            mCurrentImageUri = SlideShowService.this.getPreviousImage(currentImageUri);

            if (mSlideShowThread != null && mSlideShowThread.isAlive()) {
                stopSlideShow();
                startSlideShowThread();
            }

            Intent broadcast = new Intent(MainActivity.BROADCAST_ACTION);
            broadcast.putExtra("uri", mCurrentImageUri);
            sendBroadcast(broadcast);
        }

        public void stopSlideShow() {
            if (mSlideShowThread != null) {
                mSlideShowThread.interrupt();
                mSlideShowThread = null;
                Log.d(TAG, "stopSlideShow: ");
            }
        }

        public void startSlideShow(Uri currentImageUri) {
            mCurrentImageUri = currentImageUri;
            startSlideShowThread();
        }
    }
>>>>>>> 8a8dd850eef256cd7f796db9157d92061a4033a0
}