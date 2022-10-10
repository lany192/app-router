package com.github.lany192.sample.provider;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/provider/hello")
public class HelloProviderImpl implements HelloProvider{

    @Override
    public String sayHello() {
        return "你好";
    }

    @Override
    public void init(Context context) {

    }
}
