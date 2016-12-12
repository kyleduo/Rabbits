package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.demo.base.BaseFragment;

import java.util.Random;

/**
 * Created by kyle on 2016/12/12.
 */
@Page(name = "TEST_F", type = PageType.FRAGMENT, parent = "COMMON", intExtras = {"index", "1"})
public class TestFragment extends BaseFragment {
	public static TestFragment newInstance() {

		Bundle args = new Bundle();

		TestFragment fragment = new TestFragment();
		fragment.setArguments(args);
		return fragment;
	}

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
						.to("/second/" + new Random().nextInt(10))
						.start();
			}
		});
		ll.addView(button);

		return ll;
	}
}
