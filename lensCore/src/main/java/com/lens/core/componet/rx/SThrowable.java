package com.lens.core.componet.rx;

public class SThrowable extends Throwable {

    public final static int THROWABLE_EMPTY = 1;
    public final static int THROWABLE_ERROR_NET = 2;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    int status;

    public SThrowable(int status) {
        this.status = status;
    }

    public SThrowable(int status, String message) {
        super(message);
        this.status = status;

    }
}
