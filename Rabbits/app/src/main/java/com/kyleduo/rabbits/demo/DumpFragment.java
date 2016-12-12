package com.kyleduo.rabbits.demo;

import android.os.Bundle;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page(name = "DUMP", type = PageType.FRAGMENT)
public class DumpFragment extends BaseFragment {
	public static DumpFragment newInstance() {

		Bundle args = new Bundle();
		args.putString("dump", Rabbit.dumpMappings());
		DumpFragment fragment = new DumpFragment();
		fragment.setArguments(args);
		return fragment;
	}
}
