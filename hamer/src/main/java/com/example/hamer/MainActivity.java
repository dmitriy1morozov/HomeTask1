package com.example.hamer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import java.lang.ref.WeakReference;

public class
MainActivity extends AppCompatActivity{
		private static class ActivityHandler extends Handler{
				private final WeakReference<MainActivity> mActivity;

				public ActivityHandler(MainActivity activity) {
						this.mActivity = new WeakReference<>(activity);
				}

				@Override public void handleMessage(Message msg) {
						Uri newImageUri = msg.getData().getParcelable("uri");
						boolean shouldStopService = msg.getData().getBoolean("shouldStopService");
						Log.d(TAG, "handleMessage: newImageUri = " + newImageUri);
						MainActivity activity = mActivity.get();
						if(activity != null){
								activity.mCurrentImageUri = newImageUri;
								Glide.with(activity).load(newImageUri).into(activity.mSlideImage);
								if(shouldStopService){
										activity.stopMyService();
								}
						}
				}
		}

		//----------------------------------------------------------------------------------------------
		private static final String TAG = "MyLogs MainActivity";

		public static final String KEY_CURRENT_IMAGE_URI = "mCurrentImageUri";
		public static final String KEY_IS_SLIDESHOW_RUNNING = "isSlideShowRunning";

		@BindView(R.id.image_main_slide) ImageView mSlideImage;
		@BindView(R.id.btn_main_previous) Button mPreviousButton;
		@BindView(R.id.btn_main_slideshow) Button mSlideshowButton;
		@BindView(R.id.btn_main_next) Button mNextButton;

		boolean isSlideShowRunning;
		private Uri mCurrentImageUri;

		private Messenger mServiceMessenger;
		private Messenger mClientMessenger = new Messenger(new ActivityHandler(this));
		private ServiceConnection mServiceConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
						Log.d(TAG, "onServiceConnected: Binded with service");
						mServiceMessenger = new Messenger(service);
						sendInitialData();
				}
				@Override
				public void onServiceDisconnected(ComponentName name) {
						mServiceMessenger = null;
				}
		};

		//----------------------------------------------------------------------------------------------
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				if (savedInstanceState != null) {
						isSlideShowRunning = savedInstanceState.getBoolean(KEY_IS_SLIDESHOW_RUNNING);
						mCurrentImageUri = savedInstanceState.getParcelable(KEY_CURRENT_IMAGE_URI);
				} else {
						isSlideShowRunning = false;
						mCurrentImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cat1);
				}
				setContentView(R.layout.activity_main);
				ButterKnife.bind(this);
				Glide.with(this).load(mCurrentImageUri).into(mSlideImage);
		}

		@Override
		protected void onResume() {
				super.onResume();
				if(isSlideShowRunning){
						Log.d(TAG, "onResume: startMyService");
						startMyService(SlideShowService.ACTION_SLIDESHOW);
				}
		}

		@Override
		protected void onPause() {
				super.onPause();
				if (isSlideShowRunning) {
						Log.d(TAG, "onPause: stopMyService");
						stopMyService();
				}
		}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
				outState.putBoolean(KEY_IS_SLIDESHOW_RUNNING, isSlideShowRunning);
				outState.putParcelable(KEY_CURRENT_IMAGE_URI, mCurrentImageUri);
		}
		//----------------------------------------------------------------------------------------------
		private void startMyService(String action) {
				Intent slideShow = new Intent(this, SlideShowService.class);
				slideShow.setAction(action);
				//TODO не понял зачем в примере следующие две строчки
				//slideShow.setPackage(getApplicationContext().getPackageName());
				//slideShow.setClass(this, SlideShowService.class);
				bindService(slideShow, mServiceConnection, Service.BIND_AUTO_CREATE);
				Log.d(TAG, "startMyService: bindService Requested");

				if(action == SlideShowService.ACTION_SLIDESHOW){
						mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
				}
		}

		private void sendInitialData(){
				Message message = Message.obtain();
				message.replyTo = mClientMessenger;
				Bundle bundle = new Bundle();
				bundle.putParcelable("uri", mCurrentImageUri);
				message.setData(bundle);
				try {
						mServiceMessenger.send(message);
				} catch (RemoteException re) {
						Log.d(TAG, "sendInitialData: " + re.getMessage());
				}
		}

		private void stopMyService() {
				unbindService(mServiceConnection);
				mSlideshowButton.setBackground(
						this.getResources().getDrawable(android.R.drawable.ic_media_play));
		}
		//----------------------------------------------------------------------------------------------

		@OnClick(R.id.btn_main_previous)
		public void btnPreviousClick() {
				if(isSlideShowRunning){
						//TODO Interrupt the SlideShow, previous Image processing, restart SlideShow.
						//stopMyService();
						//startMyService(SlideShowService.ACTION_PREVIOUS_IMAGE);
						//startMyService(SlideShowService.ACTION_SLIDESHOW);
				} else{
						startMyService(SlideShowService.ACTION_PREVIOUS_IMAGE);
				}
		}

		@OnClick(R.id.btn_main_next)
		public void btnNextClick() {
				if(isSlideShowRunning){
						//TODO Interrupt the SlideShow, next Image processing, restart SlideShow.
						//stopMyService();
						//startMyService(SlideShowService.ACTION_NEXT_IMAGE);
						//startMyService(SlideShowService.ACTION_SLIDESHOW);
				} else{
						startMyService(SlideShowService.ACTION_NEXT_IMAGE);
				}
		}

		@OnClick(R.id.btn_main_slideshow)
		public void slideShow(View view) {
				isSlideShowRunning = !isSlideShowRunning;
				if (isSlideShowRunning) {
						startMyService(SlideShowService.ACTION_SLIDESHOW);
				} else {
						stopMyService();
				}
		}
}