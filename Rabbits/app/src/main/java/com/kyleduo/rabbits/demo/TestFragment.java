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
@Page("/test_fragment")
public class TestFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        assert ll != null;

        Button button = createButton("Start Second Fragment");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Rabbit.from(TestFragment.this)
						.to(P.P_SECOND_ID(new Random().nextInt(10)))
						.start();
            }
        });
        ll.addView(button);

        Button button1 = createButton("Redirect to Second Fragment");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(TestFragment.this)
                        .to(P.P_SECOND_ID(new Random().nextInt(10)))
                        .redirect()
                        .start();
            }
        });
        ll.addView(button1);

        return ll;
    }

    private Button createButton(String text) {
        Button button = new Button(getActivity());
        button.setText(text);
        button.setTextColor(0xFF49A1FF);
        button.setBackgroundDrawable(null);
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return button;
    }
}
