package com.kyleduo.rabbits;

/**
 * Used for Fragment which need to be displayed in an Activity.
 *
 * Created by kyle on 26/01/2018.
 */
public class ParentInterceptor implements Interceptor {
    @Override
    public DispatchResult intercept(Dispatcher dispatcher) {
        Action action = dispatcher.action();

        if (action.getParent() != null) {
            action = createParentAction(action);
        }

        return dispatcher.dispatch(action);
    }

    private  Action createParentAction(Action action) {
        Action parent = new Action();

        parent.setFrom(action.getFrom());
        action.setFrom(null);
        parent.setOriginUrl(action.getParent());
        action.setParent(null);
        parent.setIntentFlags(action.getIntentFlags());
        action.setIntentFlags(0);
        parent.setExtras(action.getExtras());
        parent.setTransitionAnimations(action.getTransitionAnimations());
        parent.setReferer(action.getReferer());
        parent.setRequestCode(action.getRequestCode());
        parent.setNext(action);

        return parent;
    }
}
