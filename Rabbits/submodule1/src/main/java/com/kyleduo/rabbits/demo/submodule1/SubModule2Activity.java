package com.kyleduo.rabbits.demo.submodule1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kyleduo.rabbits.annotations.Page;

/**
 * for Module
 * Created by kyleduo on 2017/5/10.
 */

@Page(name = "SUB_MODULE_2")
public class SubModule2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sub_module_2);
    }
}
