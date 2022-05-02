package com.example.propertyproject.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    // States, use enums if possible

    public static final int LOADING = 1;
    public static final int SUCCESS = 2;
    public static final int ERROR = 3;

    private final int type;
    private final T data;
    private final String error;

    public Resource(int type, T data, String error) {
        this.type = type;
        this.data = data;
        this.error = error;
    }


    public static <T> Resource<T> loading() {
        return new Resource<>(LOADING, null, null);
    }

    public static <T> Resource<T> success(T d) {
        return new Resource<>(SUCCESS, d, null);
    }

    public static <T> Resource<T> error(String e) {
        return new Resource<>(ERROR, null, e);
    }

    public int getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}