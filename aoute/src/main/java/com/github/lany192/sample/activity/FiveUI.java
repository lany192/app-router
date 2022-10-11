package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.entity.Person;
import com.github.lany192.sample.entity.User;
import java.util.List;

/**
 *
 * 类位置：{@link com.github.lany192.sample.activity.FiveActivity}
 * 自动生成,请勿编辑!
 */
public class FiveUI {
    /**
     * 用户a
     * 是否必选：false
     */
    private List<User> users;

    /**
     * 用户b
     * 是否必选：false
     */
    private List<Person> persons_items;

    private FiveUI() {
    }

    public static FiveUI builder() {
        return new FiveUI();
    }

    /**
     * 用户a
     * 是否必选：false
     */
    public FiveUI users(List<User> users) {
        this.users = users;
        return this;
    }

    /**
     * 用户b
     * 是否必选：false
     */
    public FiveUI personsItems(List<Person> persons_items) {
        this.persons_items = persons_items;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_FIVE);
        if (users != null) {
            postcard.withSerializable("users",(java.util.ArrayList<com.github.lany192.sample.entity.User>)users);
        }
        if (persons_items != null) {
            postcard.withSerializable("persons_items",(java.util.ArrayList<com.github.lany192.sample.entity.Person>)persons_items);
        }
        return postcard;
    }

    /**
     * 跳转到目标界面
     */
    public void build(NavCallback callback) {
        Postcard postcard = postcard();
        if (callback != null) {
            postcard.navigation(null, callback);
        } else {
            postcard.navigation();
        }
    }

    /**
     * 跳转到目标界面
     */
    public void build() {
        postcard().navigation();
    }

    /**
     * 获取Uri
     */
    public Uri getUri() {
        return postcard().getUri();
    }
}
