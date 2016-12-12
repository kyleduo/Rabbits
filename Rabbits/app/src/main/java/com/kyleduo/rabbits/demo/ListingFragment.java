package com.kyleduo.rabbits.demo;

import android.os.Bundle;

import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page(name = "LISTING", type = PageType.FRAGMENT)
public class ListingFragment extends BaseFragment {

	public static ListingFragment newInstance() {

		Bundle args = new Bundle();

		ListingFragment fragment = new ListingFragment();
		fragment.setArguments(args);
		return fragment;
	}
}
