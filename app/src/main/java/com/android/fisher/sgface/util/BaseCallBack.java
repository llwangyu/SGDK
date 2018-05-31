package com.android.fisher.sgface.util;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Fisher on 2017-11-14.
 */

public abstract class BaseCallBack<T> {
    public Type type;
    static Type getSuperclassTypeParameter(Class<?> subclass){
        Type superclass = subclass.getGenericSuperclass();
        if(superclass instanceof Class){
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }
    public BaseCallBack() {
        type = getSuperclassTypeParameter(getClass());
    }

    protected abstract void OnRequestBefore(Request request);

    protected abstract void onFailure(Call call, IOException e);
    /**
     *
     * 状态码大于200，小于300 时调用此方法
     * @param response
     * @param t
     * @throws IOException
     */
    protected abstract void onSuccess(Call call, Response response, T t);

    /**
     *请求成功时调用此方法
     * @param response
     */
    protected abstract void onResponse(Response response);
    /**
     * 状态码400，404，403，500等时调用此方法
     * @param response
     * @param code
     * @param e
     */
    protected abstract void onError(Call call, int statusCode, Exception e);

    protected abstract void inProgress(int progress, long total , int id);

}
