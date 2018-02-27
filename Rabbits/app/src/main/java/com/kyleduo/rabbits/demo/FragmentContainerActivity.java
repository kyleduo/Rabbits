package com.kyleduo.rabbits.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kyleduo.rabbits.DispatchResult;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;
import com.kyleduo.rabbits.demo.base.BaseFragment;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page("/fragment_container")
public class FragmentContainerActivity extends BaseActivity {
    public static final String KEY_FRAG_URL = "frag_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        if (getTopFragment() == null) {
            String uri = getIntent().getStringExtra(KEY_FRAG_URL);
            if (uri != null) {
                DispatchResult ret = Rabbit.from(this)
                        .to(uri)
                        .putExtras(getIntent().getExtras())
                        .obtain();
                Object target = ret.getTarget();
                if (target instanceof BaseFragment) {
                    loadRootFragment(R.id.common_fragment_container, (SupportFragment) target);
                }
            }


//            String uri = getIntent().getStringExtra(Rabbit.KEY_ORIGIN_URI);
//            BaseFragment fragment = (BaseFragment) Rabbit.from(this)
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
//        BaseFragment fragment = (BaseFragment) Rabbit.from(this)
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
