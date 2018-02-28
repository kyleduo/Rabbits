package com.kyleduo.rabbits.demo.submodule1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.sm1.P;

/**
 * for Module
 * Created by kyleduo on 2017/5/8.
 */
@Page("/sm1/activity")
public class SMActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sub_module_1);

        findViewById(R.id.open_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rabbit.from(SMActivity.this).to("/test/from%20submodule").start();
            }
        });

        findViewById(R.id.open_submodule2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rabbit.from(SMActivity.this).to(P.ACT2).start();
            }
        });
    }
}
