package com.estyle.databinding.entity;

import java.util.List;

/**
 * Created by zhangyi on 2018/10/8.
 */
public class MainEntity {

    private int errorCode;
    private String errorMessage;
    private List<MainListEntity> data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<MainListEntity> getData() {
        return data;
    }

    public void setData(List<MainListEntity> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MainEntity{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                '}';
    }
}
