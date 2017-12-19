package com.kyleduo.rabbits;

import android.net.Uri;
import android.util.Log;

import com.kyleduo.rabbits.dispatcher.DefaultDispatcher;
import com.kyleduo.rabbits.dispatcher.DispatchResult;
import com.kyleduo.rabbits.dispatcher.IDispatcher;
import com.kyleduo.rabbits.dispatcher.InterceptorDispatcher;
import com.kyleduo.rabbits.interceptor.IInterceptor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

	@Test
	public void dispatcherChain() throws Exception {
		IDispatcher dispatcher = new DefaultDispatcher() {
            @Override
            public DispatchResult dispatch(Target target) {
                System.out.println("dispatch");
                return super.dispatch(target);
            }
        };

		List<IInterceptor> is = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
            final int finalI = i;
            is.add(new IInterceptor() {
				@Override
				public DispatchResult dispatch(IDispatcher dispatcher, Target target) {
					System.out.println("dispatcher: " + dispatcher + " " + finalI + "   " +  target.hashCode());
                    if (finalI == 1) {
                        target = new Target(Uri.EMPTY);
                    }
                    DispatchResult r = dispatcher.dispatch(target);
                    System.out.println("after dispatcher: " + dispatcher + " " + finalI + "   " +  target.hashCode());
					return r;
				}
			});
		}
		InterceptorDispatcher id = null;
		for (IInterceptor i : is) {
			id = new InterceptorDispatcher(i, id == null ? dispatcher : id);
		}

        DispatchResult r = null;
        if (id != null) {
            r = id.dispatch(new Target(Uri.EMPTY));
        }
        System.out.println("r: " + r);
    }
}