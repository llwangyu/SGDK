package com.android.fisher.sgface.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Fisher on 2017-11-16.
 */

public class GsonUtil {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.getTime());
                }
            })
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    //return new Date(json.getAsJsonPrimitive().getAsLong());
                    return new Date(new Date().getTime());
                }
            })
            .create();
    /**
     * 转成Json字符串
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Json转Java对象
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }

    /**
     * Json转Java对象
     */
    public static <T> T fromJsonView(String json,String jsontitle, Class<T> clz) {
        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            if(jsonObject.isJsonNull()){
                return null;
            }else{
                JsonObject jsonObject2 =  jsonObject.getAsJsonObject(jsontitle);
                return gson.fromJson(jsonObject2, clz);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Json转List集合
     */
    public static <T> List<T> jsonToList(String json, Class<T> clz) {
        Type type = new TypeToken<List<T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Json转List集合,遇到解析不了的，就使用这个
     */
    public static <T> List<T> fromJsonList(String json, Class<T> cls) {
        List<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            mList.add(gson.fromJson(elem, cls));
        }
        return mList;
    }

    /**
     * Json转List集合,jsonview二次封装的，带头的
     */
    public static <T> List<T> fromJsonViewList(String json,String jsonTitle, Class<T> cls) {
        List<T> mList = new ArrayList<T>();
        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            JsonArray array = jsonObject.getAsJsonArray(jsonTitle);
            for (final JsonElement elem : array) {
                mList.add(gson.fromJson(elem, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mList;
    }

    /**
     * Json转换成Map的List集合对象
     */
    public static <T> List<Map<String, T>> toListMap(String json, Class<T> clz) {
        Type type = new TypeToken<List<Map<String, T>>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Json转Map对象
     */
    public static <T> Map<String, T> toMap(String json, Class<T> clz) {
        Type type = new TypeToken<Map<String, T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
