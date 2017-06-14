package com.kyleduo.rabbits.demo.base;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.rabbits.Rabbit;

import java.util.Set;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by kyle on 2016/12/12.
 */

public class BaseFragment extends SupportFragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setBackgroundColor(0xFFFFFFFF);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		TextView tv = createInfoTextView();
		ll.addView(tv);
		return ll;
	}

	protected TextView createInfoTextView() {
		TextView tv = new TextView(getActivity());
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(lp);

		StringBuilder params = new StringBuilder();
		Set<String> keys = getArguments().keySet();
		for (String key : keys) {
			Object value = getArguments().get(key);
			params.append(key).append("\n\t->\t").append(value == null ? "null" : value.toString()).append('\n').append('\n');
		}
		tv.setText(params.toString());
		return tv;
	}

	@Override
	public void onSupportVisible() {
		super.onSupportVisible();
		String originUri = getArguments().getString(Rabbit.KEY_ORIGIN_URI);
		Uri uri = Uri.parse(originUri);
		getActivity().setTitle(uri.getPath() + " @" + getActivity().getClass().getSimpleName());
	}
}
