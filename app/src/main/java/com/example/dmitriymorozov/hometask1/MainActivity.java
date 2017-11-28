package com.example.dmitriymorozov.hometask1;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cat1);
        Glide.with(this).load(uri).into(mSlideImage);
        mSlideshowButton.setTag(false);
    }

    @OnClick(R.id.btn_main_previous)public void previous(){
        Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_main_next)public void next(){
        Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.btn_main_slideshow)public void slideShow(View view){
        boolean isPlaying;
        if(view.getTag() == null){
            isPlaying = false;
        } else{
            isPlaying = (boolean)view.getTag();
        }

        view.setTag(!isPlaying);
        if(isPlaying){
            view.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_play));
        } else{
            view.setBackground(this.getResources().getDrawable(android.R.drawable.ic_media_pause));
        }
    }

}