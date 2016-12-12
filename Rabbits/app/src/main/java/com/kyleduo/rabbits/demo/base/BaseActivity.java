package com.kyleduo.rabbits.demo.base;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by kyle on 2016/12/12.
 */

public class BaseActivity extends SupportActivity {

	@Override
	protected FragmentAnimator onCreateFragmentAnimator() {
		return new DefaultHorizontalAnimator();
	}
}
