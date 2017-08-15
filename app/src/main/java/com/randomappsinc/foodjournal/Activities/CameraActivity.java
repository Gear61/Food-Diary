package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;

import com.google.android.cameraview.CameraView;
import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 8/15/17.
 */

public class CameraActivity extends StandardActivity {
    @BindView(R.id.camera_feed) CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }
}
