package com.estyle.viewmodel.viewmodel;

public interface Callback<T> {
    void onSuccess(T t);

    void onFailure(Throwable t);
}
