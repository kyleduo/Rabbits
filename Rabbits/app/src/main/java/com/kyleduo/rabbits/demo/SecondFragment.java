package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
//@Page(name = "SECOND", type = PageType.FRAGMENT)
public class SecondFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        assert ll != null;

        Button button = new Button(getActivity());
        button.setText("Back to Home and clearTop");
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(SecondFragment.this)
                        .to("")
                        .clearTop()
                        .start();
            }
        });
        ll.addView(button);

        return ll;
    }
}
