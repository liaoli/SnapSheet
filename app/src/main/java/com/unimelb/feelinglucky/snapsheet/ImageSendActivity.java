package com.unimelb.feelinglucky.snapsheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.unimelb.feelinglucky.snapsheet.Camera.ImageViewFragment;
import com.unimelb.feelinglucky.snapsheet.Util.StatusBarUtils;

/**
 * Created by leveyleonhardt on 10/4/16.
 */

public class ImageSendActivity extends AppCompatActivity {
    //    private ImageView imageView;
    private static final String TAG = "ImageSendActivity";
//    private Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_image_send);
        StatusBarUtils.setStatusBarVisable(this);
        String imagePath = intent.getExtras().getString("image");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_image_send_container);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(R.id.activity_image_send_container, ImageViewFragment.newInstance(imagePath)).commit();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}


