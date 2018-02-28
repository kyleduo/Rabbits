package com.kyleduo.rabbits.demo.submodule2;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kyleduo.rabbits.annotations.Page;

/**
 * Created by kyle on 28/02/2018.
 */

@Page("/sm2/activity")
public class SM2Activity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sm2);
    }
}
