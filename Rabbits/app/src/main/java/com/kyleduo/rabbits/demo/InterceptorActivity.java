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

@Page(value = "/test/interceptor", flags = 1, variety = {"/test/rules"})
public class InterceptorActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView tv = (TextView) findViewById(R.id.params_tv);
        tv.setText("param = " + getIntent().getStringExtra("param"));

        Log.d("s", "pattern: " + getIntent().getStringExtra(Rabbit.KEY_PATTERN));

        findViewById(R.id.back_home_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(InterceptorActivity.this)
                        .to(P.P_)
                        .clearTop()
                        .singleTop()
                        .start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getIntent().getStringExtra(Rabbit.KEY_PATTERN) + "@" + this.getClass().getSimpleName());
    }
}
