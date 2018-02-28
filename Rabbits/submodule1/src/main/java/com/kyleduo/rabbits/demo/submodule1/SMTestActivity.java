package com.kyleduo.rabbits.demo.submodule1;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kyleduo.rabbits.annotations.Page;

/**
 * Created by kyle on 28/02/2018.
 */
@Page("/test")
public class SMTestActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sm1_test);
    }
}
