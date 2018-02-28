package com.kyleduo.rabbits.demo;

import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.demo.utils.Constants;

/**
 * Created by kyle on 28/02/2018.
 */

@Page(value = "/test_embedded", flags = Constants.FLAG_FRAG_EMBED)
public class EmbeddedFragment extends BaseFragment {

}
