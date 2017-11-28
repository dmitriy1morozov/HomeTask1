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

    private static final boolean FLAG_PLAY = true;
    private static final boolean FLAG_STOP = false;

    @BindView(R.id.image_main_slide)
    ImageView mSlideImage;
    @BindView(R.id.btn_main_previous)
    Button mPreviousButton;
    @BindView(R.id.btn_main_slideshow)
    Button mSlideshowButton;
    @BindView(R.id.btn_main_next)
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSlideshowButton.setTag(FLAG_STOP);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cat1);
        Glide.with(this).load(uri).into(mSlideImage);
    }

    private void previousImage() {
        Log.d(TAG, "previousImage: ");
    }
    private void nextImage() {
        Log.d(TAG, "nextImage: ");
    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_main_previous)public void btnPreviousClick(){
        if((boolean)mSlideshowButton.getTag() == FLAG_PLAY){
            stopService(new Intent(this, SlideShowService.class));
            previousImage();
            startService(new Intent(this, SlideShowService.class));
        }else{
            previousImage();
        }
    }

    @OnClick(R.id.btn_main_next)public void btnNextClick(){
        if((boolean)mSlideshowButton.getTag() == FLAG_PLAY){
            stopService(new Intent(this, SlideShowService.class));
            nextImage();
            startService(new Intent(this, SlideShowService.class));
        }else{
            nextImage();
        }
    }

    @OnClick(R.id.btn_main_slideshow)public void slideShow(View view){
        if(view.getTag() == null){
            view.setTag(FLAG_STOP);
        } else{
            boolean isPlaying = (boolean)view.getTag();
            view.setTag(!isPlaying);
        }


        if((boolean)view.getTag() == FLAG_PLAY){
            startService(new Intent(this, SlideShowService.class));
            view.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else{
            stopService(new Intent(this, SlideShowService.class));
            view.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_play));
        }
    }
}