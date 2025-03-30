package com.example.health.api.config.exception;


import lombok.Data;

@Data
public class HealthException  extends  RuntimeException{
    private  String msg;
    private  int code=500;

    public HealthException(Exception e) {
        super(e);
        this.msg="执行异常";
    }

    public HealthException(String  msg) {
        super(msg);
        this.msg=msg;
    }

    public HealthException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public HealthException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public HealthException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
