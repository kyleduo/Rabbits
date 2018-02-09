package com.kyleduo.rabbits;

/**
 * 保存是否跳转成功，以及过程信息：拦截信息、参数、目标等
 * <p>
 * Created by kyle on 19/12/2017.
 */

public class DispatchResult {
    public static final int STATUS_ERROR = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_NOT_FOUND = 2;
    public static final int STATUS_NOT_FINISH = 3;

    private int code;
    private String reason;
    private Object target;

    public DispatchResult(int code, String reason, Object target) {
        this.code = code;
        this.reason = reason;
        this.target = target;
    }

    public static DispatchResult error(String reason) {
        return new DispatchResult(STATUS_ERROR, reason, null);
    }

    public static DispatchResult notFound(String url) {
        return new DispatchResult(STATUS_NOT_FOUND, "Page not found: " + url, null);
    }

    public static DispatchResult success(Object target) {
        return new DispatchResult(STATUS_SUCCESS, "success", target);
    }

    public static DispatchResult success() {
        return success(null);
    }

    public static DispatchResult notFinished() {
        return new DispatchResult(STATUS_NOT_FINISH, "", null);
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DispatchResult) {
            return this.code == ((DispatchResult) obj).code && this.target == ((DispatchResult) obj).target;
        }
        return false;
    }
}
