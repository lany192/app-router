package com.alibaba.android.arouter.demo.sample.provider;

import android.content.Context;

import com.alibaba.android.arouter.AppRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.common.SampleProvider;
import com.alibaba.android.arouter.demo.sample.entity.Person;
import com.alibaba.android.arouter.demo.sample.entity.User;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/provider/hello")
public class HelloProviderImpl implements SampleProvider {

    @Override
    public String sayHello() {
        return "你好";
    }

    @Override
    public void startFive() {
        List<User> items = new ArrayList<>();
        items.add(new User("战三", 123));
        items.add(new User("哈哈", 321));
        List<Person> items2 = new ArrayList<>();
        items2.add(new Person("2战三", 123));
        items2.add(new Person("2哈哈", 321));
        AppRouter.startFive(items, items2);
    }

    @Override
    public void init(Context context) {

    }
}
