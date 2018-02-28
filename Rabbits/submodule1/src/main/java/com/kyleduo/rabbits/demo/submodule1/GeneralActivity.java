package com.kyleduo.rabbits.demo.submodule1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Rabbits
 * Created by kyleduo on 2017/6/14.
 */
//@Page(name = "GENERAL")
public class GeneralActivity extends SupportActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sm1_test);

        if (getTopFragment() == null) {
//            String uri = getIntent().getStringExtra(Rabbit.KEY_ORIGIN_URI);
//            SupportFragment fragment = (SupportFragment) Rabbit.from(this)
//                    .obtain(uri)
//                    .mergeExtras(getIntent().getExtras())
//                    .obtain();
//            loadRootFragment(R.id.common_fragment_container, fragment);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        String uri = intent.getStringExtra(Rabbit.KEY_ORIGIN_URI);
//        SupportFragment fragment = (SupportFragment) Rabbit.from(this)
//                .obtain(uri)
//                .mergeExtras(getIntent().getExtras())
//                .obtain();

//        if (getTopFragment() == null) {
//            loadRootFragment(R.id.common_fragment_container, fragment);
//        } else {
//            start(fragment);
//        }
    }
}
