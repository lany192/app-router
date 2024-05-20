package com.github.lany192.arouter.sample.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.github.lany192.arouter.sample.JsonUtils;

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
    public <T> T json2Object(String input, Class<T> clazz) {
        return null;
    }

    @Override
    public String object2Json(Object object) {
        return JsonUtils.object2json(object);
    }

    @Override
    public <T> T parseObject(String json, Type clazz) {
        return JsonUtils.json2object(json, clazz);
    }
}