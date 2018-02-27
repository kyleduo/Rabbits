package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kyleduo.rabbits.P;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseFragment;

import java.util.Random;

/**
 * Created by kyle on 2016/12/12.
 */
@Page("/test/{param}")
public class TestFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        assert ll != null;

        Button button = new Button(getActivity());
        button.setText("Start Second Fragment");
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Rabbit.from(TestFragment.this)
						.to(P.P_SECOND_ID(new Random().nextInt(10)))
						.start();
            }
        });
        ll.addView(button);

        return ll;
    }
}
