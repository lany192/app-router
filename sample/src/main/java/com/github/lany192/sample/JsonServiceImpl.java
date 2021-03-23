package com.github.lany192.sample;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 如果需要传递自定义对象，需要实现 SerializationService,并使用@Route注解标注(方便用户自行选择序列化方式)
 */
@Route(path = "/service/json")
public class JsonServiceImpl implements SerializationService {

    @Override
    public void init(Context context) {

    }

    @Override
    public <T> T json2Object(String json, Class<T> clazz) {
        return JsonUtils.json2object(json, clazz);
    }

    @Override
    public String object2Json(Object object) {
        String json = JsonUtils.object2json(object);
        return json;
    }

    @Override
    public <T> T parseObject(String json, Type clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            T t = null;
            try {
                t = (new Gson()).fromJson(json, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }
    }
}