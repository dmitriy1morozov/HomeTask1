package com.example.dmitriymorozov.hometask1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
		private static final String TAG = "MyLogs MainActivity";

		public static final String KEY_CURRENT_IMAGE_ID = "mCurrentImageId";
		public static final String KEY_IS_SLIDESHOW_RUNNING = "isSlideShowRunning";
		public static final String BROADCAST_ACTION = "com.example.dmitriymorozov.hometask1.SLIDE_UPDATE";

		@BindView(R.id.image_main_slide) ImageView mSlideImage;
		@BindView(R.id.btn_main_previous) Button mPreviousButton;
		@BindView(R.id.btn_main_slideshow) Button mSlideshowButton;
		@BindView(R.id.btn_main_next) Button mNextButton;

		boolean isSlideShowRunning;
		private Uri mBaseUri;
		private int mCurrentImageId;


		private ServiceConnection mServiceConnection = new ServiceConnection() {
				@Override public void onServiceConnected(ComponentName name, IBinder service) {
						mBinder = (SlideShowService.LocalBinder) service;
				}

				@Override public void onServiceDisconnected(ComponentName name) {
						mBinder = null;
				}
		};
		private SlideShowService.LocalBinder mBinder;

		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
				@Override public void onReceive(Context context, Intent intent) {
						Uri currentImageUri = intent.getParcelableExtra("uri");
						Glide.with(mSlideImage).load(currentImageUri).into(mSlideImage);
						mCurrentImageId = Integer.parseInt(currentImageUri.getLastPathSegment());
				}
		};


		//----------------------------------------------------------------------------------------------
		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				if (savedInstanceState != null) {
						isSlideShowRunning = savedInstanceState.getBoolean(KEY_IS_SLIDESHOW_RUNNING);
						mCurrentImageId = savedInstanceState.getInt(KEY_CURRENT_IMAGE_ID);
				} else {
						isSlideShowRunning = false;
						mCurrentImageId = R.raw.cat1;
				}
				setContentView(R.layout.activity_main);
				ButterKnife.bind(this);

				mBaseUri = Uri.parse("android.resource://" + getPackageName() + "/");
		}

		@Override protected void onStart() {
				super.onStart();
				Uri currentImageUri = Uri.parse(mBaseUri.toString() + mCurrentImageId);
				Glide.with(this).load(currentImageUri).into(mSlideImage);

				Intent slideShow = new Intent(this, SlideShowService.class);
				slideShow.putExtra("uri", currentImageUri);
				startService(slideShow);
				bindService(new Intent(this, SlideShowService.class), mServiceConnection,
						Service.BIND_AUTO_CREATE);
				registerReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_ACTION));
		}

		@Override protected void onResume() {
				super.onResume();
				Log.d(TAG, "onResume: isSlideShowRunning = " + isSlideShowRunning);
				if (isSlideShowRunning) {
						startSlideShow();
				}
		}

		@Override protected void onPause() {
				super.onPause();
				if (isSlideShowRunning) {
						Log.d(TAG, "onPause: stopSlideShow");
						stopSlideShow();
				}
		}

		@Override protected void onStop() {
				super.onStop();
				unbindService(mServiceConnection);
				stopService(new Intent(this, SlideShowService.class));
				unregisterReceiver(mBroadcastReceiver);
		}

		@Override protected void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
				outState.putBoolean(KEY_IS_SLIDESHOW_RUNNING, isSlideShowRunning);
				outState.putInt(KEY_CURRENT_IMAGE_ID, mCurrentImageId);
		}

		//----------------------------------------------------------------------------------------------
		private void startSlideShow() {
				mBinder.startSlideShow(Uri.parse(mBaseUri.toString() + mCurrentImageId));
				mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
		}

		private void stopSlideShow() {
				mBinder.stopSlideShow();
				mSlideshowButton.setBackground(
						this.getResources().getDrawable(android.R.drawable.ic_media_play));
		}

		//----------------------------------------------------------------------------------------------

		@OnClick(R.id.btn_main_previous) public void btnPreviousClick() {
				mBinder.previousImage(Uri.parse(mBaseUri.toString() + mCurrentImageId));
		}

		@OnClick(R.id.btn_main_next) public void btnNextClick() {
				mBinder.nextImage(Uri.parse(mBaseUri.toString() + mCurrentImageId));
		}

		@OnClick(R.id.btn_main_slideshow) public void slideShow(View view) {
				isSlideShowRunning = !isSlideShowRunning;
				if (isSlideShowRunning) {
						startSlideShow();
				} else {
						stopSlideShow();
				}
		}
}