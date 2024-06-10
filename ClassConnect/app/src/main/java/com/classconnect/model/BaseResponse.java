package com.classconnect.model;

public class BaseResponse<T> {
    public boolean success;
    public String message;
    public T payload;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", payload=" + payload +
                '}';
    }
}
