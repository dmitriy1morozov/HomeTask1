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
import java.util.Locale;

public class
MainActivity extends AppCompatActivity implements OnImageReceived{
		private static final String TAG = "MyLogs MainActivity";

		public static final String KEY_CURRENT_IMAGE_URI = "mCurrentImageUri";
		public static final String KEY_IS_SLIDESHOW_RUNNING = "isSlideShowRunning";

		@BindView(R.id.image_main_slide) ImageView mSlideImage;
		@BindView(R.id.btn_main_previous) Button mPreviousButton;
		@BindView(R.id.btn_main_slideshow) Button mSlideshowButton;
		@BindView(R.id.btn_main_next) Button mNextButton;

		private boolean isSlideShowRunning;
		private byte mAction = -1;
		private Uri mCurrentImageUri;

		private Messenger mServiceMessenger;
		private Messenger mClientMessenger = new Messenger(new ActivityHandler(this));
		private ServiceConnection mServiceConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
						Log.d(TAG, "onServiceConnected: Binded with service");
						mServiceMessenger = new Messenger(service);
						if(mAction != -1){
								sendCommandToService();
						}
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
						String packageName = getPackageName();
						String defaultImage = String.format(Locale.US,"android.resource://%s/%d", packageName, R.raw.cat1);
						mCurrentImageUri = Uri.parse(defaultImage);
				}
				setContentView(R.layout.activity_main);
				ButterKnife.bind(this);
				Glide.with(this).load(mCurrentImageUri).into(mSlideImage);
		}

		@Override
		protected void onResume() {
				super.onResume();
				Log.d(TAG, "onResume: startMyService");
				if(isSlideShowRunning){
						mAction = SlideShowService.ACTION_SLIDESHOW_START;
						startMyService();
						mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
				}
		}

		@Override
		protected void onPause() {
				super.onPause();
				Log.d(TAG, "onPause: stopMyService");
				if (isSlideShowRunning) {
						stopMyService();
						mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_play));
				}
		}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
				outState.putBoolean(KEY_IS_SLIDESHOW_RUNNING, isSlideShowRunning);
				outState.putParcelable(KEY_CURRENT_IMAGE_URI, mCurrentImageUri);
		}

		@Override public void imageReceived(Uri imageUri) {
				mCurrentImageUri = imageUri;
				Glide.with(this).load(imageUri).into(mSlideImage);
		}
		//----------------------------------------------------------------------------------------------
		private void startMyService() {
				Intent slideShow = new Intent(this, SlideShowService.class);
				bindService(slideShow, mServiceConnection, Service.BIND_AUTO_CREATE);
				Log.d(TAG, "startMyService: bindService Requested");
		}

		private void stopMyService() {
				if(mServiceMessenger != null){
						unbindService(mServiceConnection);
				}
		}

		private void sendCommandToService(){
				Log.d(TAG, "sendCommandToService: mAction = " + mAction);
				Message message = Message.obtain();
				message.replyTo = mClientMessenger;
				Bundle bundle = new Bundle();
				bundle.putByte("action", mAction);
				bundle.putParcelable("uri", mCurrentImageUri);
				message.setData(bundle);
				try {
						mServiceMessenger.send(message);
				} catch (RemoteException re) {
						Log.d(TAG, "sendCommandToService: " + re.getMessage());
				}

				//Reset one-time actions
				if(mAction == SlideShowService.ACTION_NEXT_IMAGE || mAction == SlideShowService.ACTION_PREVIOUS_IMAGE){
						mAction = -1;
				}
		}
		//----------------------------------------------------------------------------------------------
		@OnClick(R.id.btn_main_previous)
		public void btnPreviousClick() {
				mAction = SlideShowService.ACTION_PREVIOUS_IMAGE;
				if(mServiceMessenger == null){
						startMyService();
				}else{
						sendCommandToService();
				}
		}

		@OnClick(R.id.btn_main_next)
		public void btnNextClick() {
				mAction = SlideShowService.ACTION_NEXT_IMAGE;
				if(mServiceMessenger == null){
						startMyService();
				}else{
						sendCommandToService();
				}
		}

		@OnClick(R.id.btn_main_slideshow)
		public void slideShow(View view) {
				isSlideShowRunning = !isSlideShowRunning;

				if (isSlideShowRunning) {
						mAction = SlideShowService.ACTION_SLIDESHOW_START;
						if(mServiceMessenger == null){
								startMyService();
						}else{
								sendCommandToService();
						}
						mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
				} else {
						mAction = -1;
						stopMyService();
						mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_play));
				}
		}


		//==============================================================================================
		private static class ActivityHandler extends Handler{
				private final WeakReference<OnImageReceived> mImageReceiverRef;

				public ActivityHandler(OnImageReceived imageReceiver) {
						this.mImageReceiverRef = new WeakReference<>(imageReceiver);
				}

				@Override public void handleMessage(Message msg) {
						Uri newImageUri = msg.getData().getParcelable("uri");
						OnImageReceived imageReceiver = mImageReceiverRef.get();
						if(imageReceiver != null){
								imageReceiver.imageReceived(newImageUri);
						}
				}
		}
}