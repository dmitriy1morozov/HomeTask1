package com.example.dmitriymorozov.hometask1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    @BindView(R.id.image_main_slide)
    ImageView mSlideImage;
    @BindView(R.id.btn_main_previous)
    Button mPreviousButton;
    @BindView(R.id.btn_main_slideshow)
    Button mSlideshowButton;
    @BindView(R.id.btn_main_next)
    Button mNextButton;

    boolean isSlideShowRunning = false;
    private Uri mBaseUri;
    private int mCurrentImageId;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            isSlideShowRunning = savedInstanceState.getBoolean(KEY_IS_SLIDESHOW_RUNNING);
            mCurrentImageId = savedInstanceState.getInt(KEY_CURRENT_IMAGE_ID);
        } else{
            isSlideShowRunning = false;
            mCurrentImageId = R.raw.cat1;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBaseUri = Uri.parse("android.resource://" + getPackageName() + "/");
        Uri currentImageUri = Uri.parse(mBaseUri.toString() + mCurrentImageId);
        Glide.with(this).load(currentImageUri).into(mSlideImage);
    }

    @Override
    protected void onResume() {
        if(isSlideShowRunning){
            startSlideShow();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(isSlideShowRunning){
            stopSlideShow();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SLIDESHOW_RUNNING, isSlideShowRunning);
        outState.putInt(KEY_CURRENT_IMAGE_ID, mCurrentImageId);
        super.onSaveInstanceState(outState);
    }

    //----------------------------------------------------------------------------------------------
    private void previousImage() {
        if(mCurrentImageId <= R.raw.cat1){
            mCurrentImageId = R.raw.cat5;
        } else{
            mCurrentImageId--;
        }

        Uri newImageUri = Uri.parse(mBaseUri.toString() + mCurrentImageId);
        Glide.with(this).load(newImageUri).into(mSlideImage);
    }
    private void nextImage() {
        if(mCurrentImageId >= R.raw.cat5){
            mCurrentImageId = R.raw.cat1;
        } else{
            mCurrentImageId++;
        }

        Uri newImageUri = Uri.parse(mBaseUri.toString() + mCurrentImageId);
        Glide.with(this).load(newImageUri).into(mSlideImage);
    }
    private void startSlideShow(){
        Intent slideShow = new Intent(this, SlideShowService.class);
        Uri currentImage = Uri.parse(mBaseUri.toString() + mCurrentImageId);
        slideShow.putExtra("currentImage", currentImage);
        startService(slideShow);
        mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
    }
    private void stopSlideShow(){
        stopService(new Intent(this, SlideShowService.class));
        mSlideshowButton.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_main_previous)public void btnPreviousClick(){
        if(isSlideShowRunning){
            stopService(new Intent(this, SlideShowService.class));
            previousImage();
            startService(new Intent(this, SlideShowService.class));
        }else{
            previousImage();
        }
    }

    @OnClick(R.id.btn_main_next)public void btnNextClick(){
        if(isSlideShowRunning){
            stopService(new Intent(this, SlideShowService.class));
            nextImage();
            startService(new Intent(this, SlideShowService.class));
        }else{
            nextImage();
        }
    }

    @OnClick(R.id.btn_main_slideshow)public void slideShow(View view){
        isSlideShowRunning = !isSlideShowRunning;

        if(isSlideShowRunning){
            startSlideShow();
        } else{
            stopSlideShow();
        }
    }
}