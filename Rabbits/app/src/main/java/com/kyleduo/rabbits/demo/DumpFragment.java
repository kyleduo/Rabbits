package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page("/dump")
public class DumpFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView sv = new ScrollView(getActivity());
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView tv = new TextView(getActivity());
        tv.setText(Rabbit.dump());
        tv.setTextColor(0xFFA6ABB0);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        int padding = (int) (getResources().getDisplayMetrics().density * 16);
        tv.setPadding(padding, padding, padding, padding);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sv.addView(tv);

        return sv;
    }
}
