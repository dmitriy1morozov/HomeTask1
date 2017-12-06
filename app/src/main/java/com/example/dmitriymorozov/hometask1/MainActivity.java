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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnNextImageListener{
    private static final String TAG = "MyLogs MainActivity";

    public static final String KEY_CURRENT_IMAGE_URI = "mCurrentImageUri";
    public static final String KEY_IS_SLIDESHOW_RUNNING = "isSlideShowRunning";

    @BindView(R.id.image_main_slide) ImageView mSlideImage;
    @BindView(R.id.btn_main_previous) Button mPreviousButton;
    @BindView(R.id.btn_main_slideshow) Button mSlideshowButton;
    @BindView(R.id.btn_main_next) Button mNextButton;

    boolean isSlideShowRunning;
    private Uri mCurrentImageUri;

    private SlideShowService.LocalBinder mBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (SlideShowService.LocalBinder) service;
            mBinder.setOnNextImageListener(MainActivity.this);
            Log.d(TAG, "onServiceConnected. Binded!");
            if(isSlideShowRunning) {
                startSlideShow();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Glide.with(this).load(mCurrentImageUri).into(mSlideImage);

        Intent slideShow = new Intent(this, SlideShowService.class);
        slideShow.putExtra("uri", mCurrentImageUri);
        bindService(new Intent(this, SlideShowService.class), mServiceConnection,
            Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isSlideShowRunning = " + isSlideShowRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSlideShowRunning) {
            Log.d(TAG, "onPause: stopSlideShow");
            stopSlideShow();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
        mBinder = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SLIDESHOW_RUNNING, isSlideShowRunning);
        outState.putParcelable(KEY_CURRENT_IMAGE_URI, mCurrentImageUri);
    }

    @Override public void onNextImageReceived(Uri nextImage) {
        Log.d(TAG, "onReceive: a new image in onNextImageReceived callback");
        mCurrentImageUri = nextImage;
        runOnUiThread(new Runnable() {
            @Override public void run() {
		            Glide.with(mSlideImage).load(mCurrentImageUri).into(mSlideImage);
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    private void startSlideShow() {
        mBinder.startSlideShow(mCurrentImageUri);
        mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
    }

    private void stopSlideShow() {
        mBinder.stopSlideShow();
        mSlideshowButton.setBackground(
            this.getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_main_previous)
    public void btnPreviousClick() {
        mBinder.previousImage(mCurrentImageUri);
    }

    @OnClick(R.id.btn_main_next)
    public void btnNextClick() {
        mBinder.nextImage(mCurrentImageUri);
    }

    @OnClick(R.id.btn_main_slideshow)
    public void slideShow(View view) {
        isSlideShowRunning = !isSlideShowRunning;
        if (isSlideShowRunning) {
            startSlideShow();
        } else {
            stopSlideShow();
        }
    }
}