package com.example.hometask1;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
		private static final String TAG = "MyLogs MainActivity";

		@BindView(R.id.image_main_slide) ImageView mSlideImage;
		@BindView(R.id.btn_main_service_start) Button mServiceStart;
		@BindView(R.id.btn_main_service_stop) Button mServiceStop;
		@BindView(R.id.btn_main_thread_start) Button mThreadStart;
		@BindView(R.id.btn_main_thread_stop) Button mThreadStop;

		private ServiceConnection mServiceConnection = new ServiceConnection() {
				@Override public void onServiceConnected(ComponentName name, IBinder service) {
						Log.d(TAG, "onServiceConnected: " + service.hashCode());
						mBinder = (BinderService.LocalBinder) service;
				}

				@Override public void onServiceDisconnected(ComponentName name) {
						mBinder = null;
						Log.d(TAG, "onServiceDisconnected: ");
				}
		};
		private BinderService.LocalBinder mBinder;

		//----------------------------------------------------------------------------------------------
		@Override protected void onCreate(Bundle savedInstanceState) {
				Log.d(TAG, "onCreate: ");
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				ButterKnife.bind(this);
		}

		@Override protected void onStart() {
				Log.d(TAG, "onStart: ");
				super.onStart();

				bindService(new Intent(this, BinderService.class), mServiceConnection,
						Service.BIND_AUTO_CREATE);
		}

		@Override protected void onResume() {
				Log.d(TAG, "onResume: ");
				super.onResume();
		}

		@Override protected void onPause() {
				Log.d(TAG, "onPause: ");
				super.onPause();
		}

		@Override protected void onStop() {
				Log.d(TAG, "onStop: ");
				super.onStop();
				unbindService(mServiceConnection);
				stopService(new Intent(this, BinderService.class));
		}

		@Override protected void onSaveInstanceState(Bundle outState) {
				super.onSaveInstanceState(outState);
		}

		//----------------------------------------------------------------------------------------------
		@OnClick(R.id.btn_main_service_start) public void btnServiceStartClick() {
				bindService(new Intent(this, BinderService.class), mServiceConnection,
						Service.BIND_AUTO_CREATE);
				Intent simpleService = new Intent(this, BinderService.class);
				startService(simpleService);
		}

		@OnClick(R.id.btn_main_service_stop) public void btnServiceStopClick() {
				unbindService(mServiceConnection);
				Intent simpleService = new Intent(this, BinderService.class);
				stopService(simpleService);
		}

		@OnClick(R.id.btn_main_thread_start) public void btnThreadStartClick() {
				mBinder.startThread();
		}

		@OnClick(R.id.btn_main_thread_stop) public void btnThreadStop() {
				mBinder.stopThread();
		}
}
