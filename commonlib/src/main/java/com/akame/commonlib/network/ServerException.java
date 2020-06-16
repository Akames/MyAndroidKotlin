package com.akame.commonlib.network;

/**
 * @Author: Restring
 * @Date: 2018/4/23
 * @Description:
 */
public class ServerException extends Exception {
    private int returnCode;

    public ServerException(String returnMsg) {
        super(returnMsg);
    }

    public ServerException(String returnMsg, int code) {
        super(returnMsg);
        this.returnCode = code;
    }

    public int getCode() {
        return returnCode;
    }

    public void setCode(int code) {
        this.returnCode = code;
    }
}
