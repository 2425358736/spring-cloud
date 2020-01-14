package com.springcloud.demo.config.error;


import java.io.Serializable;

/**
 * @author admin
 */

public class AppResult<T> implements Serializable {

    private Integer status;


    private String subMsg;


    private String msg;


    private T data;


    private String version;


    public AppResult() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static <T> AppResult<T> successReturnDate(T data) {
        AppResult<T> result = new AppResult();
        result.setData(data);
        result.setMsg("成功");
        result.setStatus(200);
        result.setSubMsg("");
        result.setVersion("1.0");
        return result;
    }

    public static <T> AppResult<T> successReturnDate(T data, String msg) {
        AppResult<T> result = new AppResult();
        result.setData(data);
        result.setMsg(msg);
        result.setStatus(200);
        result.setSubMsg("");
        result.setVersion("1.0");
        return result;
    }

    public static <T> AppResult<T> successReturn(T data, String msg, String version) {
        AppResult<T> result = new AppResult();
        result.setData(data);
        result.setMsg(msg);
        result.setStatus(200);
        result.setSubMsg("");
        result.setVersion(version);
        return result;
    }

    public static <T> AppResult<T> successReturn(String msg) {
        AppResult<T> result = new AppResult();
        result.setData(null);
        result.setMsg(msg);
        result.setStatus(200);
        result.setSubMsg("");
        result.setVersion("1.0");
        return result;
    }

    public static <T> AppResult<T> errorReturn(Integer status, String msg, String subMsg) {
        AppResult<T> result = new AppResult();
        result.setData(null);
        result.setMsg(msg);
        result.setStatus(status);
        result.setSubMsg(subMsg);
        result.setVersion("1.0");
        return result;
    }

    public static <T> AppResult<T> errorReturn(Integer status, String msg, String subMsg, String version) {
        AppResult<T> result = new AppResult();
        result.setData(null);
        result.setMsg(msg);
        result.setStatus(status);
        result.setSubMsg(subMsg);
        result.setVersion(version);
        return result;
    }

    public static <T> AppResult<T> errorReturn(Integer status, String msg) {
        AppResult<T> result = new AppResult();
        result.setData(null);
        result.setMsg(msg);
        result.setStatus(status);
        result.setSubMsg("");
        result.setVersion("1.0");
        return result;
    }
}

