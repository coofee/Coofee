package com.coofee.component.json;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zhaocongying on 16/8/6.
 */
public class JsonUtils {

    private static final ThreadLocal<Gson> GSON_THREAD_LOCAL = new ThreadLocal<Gson>() {
        @Override
        public Gson get() {
            return new Gson();
        }
    };

    public static <T> T fromJson(String jsonText, Class<T> resultClass) {
        final Gson gson = GSON_THREAD_LOCAL.get();
        return gson.fromJson(jsonText, resultClass);
    }

    public static <T> T fromJson(InputStreamReader inputStreamReader, Class<T> resultClass) {
        final Gson gson = GSON_THREAD_LOCAL.get();
        return gson.fromJson(inputStreamReader, resultClass);
    }

    public static <T> T fromJsonList(String jsonText, Class<T> resultClass) {
        final Gson gson = GSON_THREAD_LOCAL.get();
        return gson.fromJson(jsonText, new ClassWrapper<T>(resultClass));
    }

    public static <T> T fromJsonList(InputStreamReader inputStreamReader, Class<T> resultClass) {
        final Gson gson = GSON_THREAD_LOCAL.get();
        return gson.fromJson(inputStreamReader, new ClassWrapper<T>(resultClass));
    }

    public static String toJson(Object result) {
        final Gson gson = GSON_THREAD_LOCAL.get();
        return gson.toJson(result);
    }

    private static final class ClassWrapper<T> implements ParameterizedType {
        private Class<T> wrapped;

        public ClassWrapper(Class<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{
                    this.wrapped
            };
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type getRawType() {
            return ClassWrapper.class;
        }
    }


    public static class Result<T> implements ParameterizedType {
        private Class<?> wrapped;

        public int code;
        public String msg;
        public T data;

        public Result(Class<T> wrapper) {
            this.wrapped = wrapper;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @Override
        public Type getRawType() {
            return Result.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
