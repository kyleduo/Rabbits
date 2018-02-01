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

    private int status;
    private String reason;
    private Object target;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    DispatchResult error(String reason) {
        this.status = STATUS_ERROR;
        this.reason = reason;
        return this;
    }

    DispatchResult notFound(String url) {
        this.status = STATUS_NOT_FOUND;
        this.reason = "Page NOT FOUND: " + url;
        this.target = null;
        return this;
    }

    DispatchResult success() {
        this.status = STATUS_SUCCESS;
        this.reason = null;
        return this;
    }
}
