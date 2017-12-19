package com.kyleduo.rabbits.dispatcher;

import com.kyleduo.rabbits.Callback;

/**
 * 保存是否跳转成功，以及过程信息：拦截信息、参数、目标等
 *
 * Created by kyle on 19/12/2017.
 */

public class DispatchResult {
    private Callback mCb;

    public void send() {
        if (mCb != null) {
            mCb.callback(this);
        }
    }
}
