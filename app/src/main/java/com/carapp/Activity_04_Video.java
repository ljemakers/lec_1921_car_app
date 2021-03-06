package com.carapp;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class Activity_04_Video extends ComActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView status = findViewById(R.id.status);

        if( null != status ) {
            status.setText( "" );
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.v( TAG, "onResume");

        this.playVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //this.stopPlayVideo();
    }

}
