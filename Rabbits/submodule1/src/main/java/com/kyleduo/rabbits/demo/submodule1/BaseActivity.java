package com.kyleduo.rabbits.demo.submodule1;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kyleduo.rabbits.Rabbit;

/**
 * Created by kyle on 28/02/2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);


                toolbar.setBackgroundColor(0xFF7A8790);
                toolbar.setTitleTextColor(Color.WHITE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getIntent().getStringExtra(Rabbit.KEY_PATTERN) + "@" + this.getClass().getSimpleName());
    }
}
