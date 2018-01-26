package com.kyleduo.rabbits;

import android.net.Uri;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<Interceptor> interceptors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            interceptors.add(new Interceptor() {
                @Override
                public DispatchResult intercept(Dispatcher dispatcher) {
                    print(finalI + " - before " + dispatcher.target().getPage());
                    if (finalI == 3) {
                        dispatcher.target().setPage("page 3");
                    }
                    if (finalI == 4) {
                        return new DispatchResult();
                    }
                    DispatchResult result = dispatcher.dispatch(dispatcher.target());
                    print(finalI + " - after");
                    return result;
                }
            });
        }
        Interceptor real = new Interceptor() {
            @Override
            public DispatchResult intercept(Dispatcher dispatcher) {
                Target target = dispatcher.target();
                print("real navigation to " + target.getPage());
                return new DispatchResult();
            }
        };

        interceptors.add(real);

        Target target = new Target(Uri.EMPTY);
        target.setPage("origin");
        RealDispatcher realDispatcher = new RealDispatcher(target, interceptors, 0);

        DispatchResult ret = realDispatcher.dispatch(target);

        print(ret);
    }

    private void print(Object obj) {
        System.out.println(obj instanceof String ? obj : obj.toString());
    }

}