package com.lens.core.componet.net.exeception;

/**
 * 服务器返回错误， HTTP 200 ,但数据内标志未非正确数据，通常包含 status 和 message
 * {@link ExceptionEngine#handleException(Throwable)}.
 */

public class ServerException extends RuntimeException {

    public int code;
    public String message;
    public Object object;

    public ServerException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServerException(int code, String message, Object object) {
        this.code = code;
        this.message = message;
        this.object = object;
    }

    /**
     * 权限失效
     * @return
     */
    public boolean isForbidden() {
        if(code == -2) {
            return true;
        }
        return false;
    }
}