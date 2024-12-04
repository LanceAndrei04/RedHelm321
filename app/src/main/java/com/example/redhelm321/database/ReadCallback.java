package com.example.redhelm321.database;

public interface ReadCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
