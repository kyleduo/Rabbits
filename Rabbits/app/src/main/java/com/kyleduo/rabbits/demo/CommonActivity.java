package com.kyleduo.rabbits.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page(name = "COMMON")
public class CommonActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        if (getTopFragment() == null) {
            String uri = getIntent().getStringExtra(Rabbit.KEY_ORIGIN_URI);
            BaseFragment fragment = (BaseFragment) Rabbit.from(this)
                    .obtain(uri)
                    .mergeExtras(getIntent().getExtras())
                    .obtain();
            loadRootFragment(R.id.common_fragment_container, fragment);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String uri = intent.getStringExtra(Rabbit.KEY_ORIGIN_URI);
        BaseFragment fragment = (BaseFragment) Rabbit.from(this)
                .obtain(uri)
                .mergeExtras(getIntent().getExtras())
                .obtain();

        if (getTopFragment() == null) {
            loadRootFragment(R.id.common_fragment_container, fragment);
        } else {
            start(fragment);
        }
    }
}
