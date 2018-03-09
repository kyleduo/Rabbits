package com.kyleduo.rabbits;

/**
 * 保存是否跳转成功，以及过程信息：拦截信息、参数、目标等
 * <p>
 * Created by kyle on 19/12/2017.
 */

public class RabbitResult {
    public static final int STATUS_ERROR = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_NOT_FOUND = 2;
    public static final int STATUS_NOT_FINISH = 3;

    private int code;
    private String reason;
    private Object target;

    public RabbitResult(int code, String reason, Object target) {
        this.code = code;
        this.reason = reason;
        this.target = target;
    }

    public static RabbitResult error(String reason) {
        return new RabbitResult(STATUS_ERROR, reason, null);
    }

    public static RabbitResult notFound(String url) {
        return new RabbitResult(STATUS_NOT_FOUND, "Page not found: " + url, null);
    }

    public static RabbitResult success(Object target) {
        return new RabbitResult(STATUS_SUCCESS, "success", target);
    }

    public static RabbitResult success() {
        return success(null);
    }

    public static RabbitResult notFinished() {
        return new RabbitResult(STATUS_NOT_FINISH, "", null);
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

    public boolean isSuccess() {
        return getCode() == STATUS_SUCCESS;
    }

    public boolean isFinished() {
        return getCode() != STATUS_NOT_FINISH;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RabbitResult) {
            return this.code == ((RabbitResult) obj).code && this.target == ((RabbitResult) obj).target;
        }
        return false;
    }

    @Override
    public String toString() {
        switch (code) {
            case STATUS_ERROR:
                return "Result: ERROR, " + reason;
            case STATUS_SUCCESS:
                if (target == null) {
                    return "Result: SUCCESS";
                } else {
                    return "Result: SUCCESS, " + target;
                }
            case STATUS_NOT_FOUND:
                return "Result: NOT_FOUND, " + reason;
            case STATUS_NOT_FINISH:
                return "Result: NOT_FINISH";
        }
        return "Result: UNKNOWN";
    }
}
