package com.github.lany192.sample;

import com.alibaba.android.arouter.RoutePath;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.entity.Person;
import com.github.lany192.sample.entity.User;

import java.util.List;

public class HelloRouter {
    private List<User> users;
    private List<Person> persons;
    private NavigationCallback callback;

    private HelloRouter() {
    }

    public static HelloRouter builder() {
        return new HelloRouter();
    }

    public HelloRouter callback(NavigationCallback callback) {
        this.callback = callback;
        return this;
    }

    public HelloRouter users(List<User> users) {
        this.users = users;
        return this;
    }

    public HelloRouter persons(List<Person> persons) {
        this.persons = persons;
        return this;
    }

    public void build() {
        Postcard postcard = ARouter.getInstance().build(RoutePath.APP_FIVE);
        if (users != null) {
            postcard.withSerializable("users", (java.util.ArrayList<com.github.lany192.sample.entity.User>) users);
        }
        if (persons != null) {
            postcard.withSerializable("persons", (java.util.ArrayList<com.github.lany192.sample.entity.Person>) persons);
        }
        if (callback != null) {
            postcard.navigation(null, callback);
        } else {
            postcard.navigation();
        }
    }
}
