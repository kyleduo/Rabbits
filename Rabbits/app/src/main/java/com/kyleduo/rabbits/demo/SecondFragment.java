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

/**
 * Created by kyle on 2016/12/12.
 */
@Page("/second/{id:l}")
public class SecondFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        assert ll != null;

        Button button = new Button(getActivity());
        button.setText("Open Embedded Fragment");
        button.setTextColor(0xFF49A1FF);
        button.setBackgroundDrawable(null);
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rabbit.from(SecondFragment.this)
                        .to(P.P_TEST_EMBEDDED)
                        .putExtra("param", "send to embedded fragment")
                        .start();
            }
        });
        ll.addView(button);

        return ll;
    }
}
