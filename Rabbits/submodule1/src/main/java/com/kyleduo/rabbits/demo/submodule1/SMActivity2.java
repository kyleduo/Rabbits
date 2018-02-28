package com.kyleduo.rabbits.demo.submodule1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;

/**
 * for Rabbits
 * Created by kyleduo on 2017/5/10.
 */

@Page(value = "/sm1/activity2", alias = "ACT2")
public class SMActivity2 extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sub_module_2);

        findViewById(R.id.back_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(SMActivity2.this)
                        .to("/")
                        .clearTop()
                        .singleTop()
                        .start();
            }
        });
    }
}
