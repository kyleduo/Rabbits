package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
//@Page(name = "DUMP", type = PageType.FRAGMENT)
public class DumpFragment extends BaseFragment {
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Bundle args = getArguments();
//		args.putString("dump", Rabbit.dumpMappings());
		super.onCreate(savedInstanceState);
	}
}
