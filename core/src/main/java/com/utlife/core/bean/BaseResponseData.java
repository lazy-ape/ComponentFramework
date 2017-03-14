package com.utlife.core.bean;



/**
 * Created by xuqiang on 2017/1/4.
 */

public class BaseResponseData<T>{

    public int code = -1;
    public String msg;
    public T data;
    public boolean success = false;

    public boolean isSuccess(){
        return success;
    }

    public String getErrorMsg(){
        return msg;
    }

    public int getErrorCode(){
        return code;
    }

    public T getData(){
        return data;
    }

}
