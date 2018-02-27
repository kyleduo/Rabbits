package com.kyleduo.rabbits.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kyleduo.rabbits.P;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;

@Page(value = "https://blog.kyleduo.com/test/{param}", flags = 1, variety = {"/test", "/test/{param}"})
public class TestActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView tv = (TextView) findViewById(R.id.params_tv);
        tv.setText("testing: " + getIntent().getStringExtra("param"));

        Log.d("s", "pattern: " + getIntent().getStringExtra(Rabbit.KEY_RABBITS_PATTERN));

        findViewById(R.id.back_home_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(TestActivity.this)
                        .to(P.P_)
                        .clearTop()
                        .start();
            }
        });
    }
}
