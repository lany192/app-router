package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.entity.User;
import java.lang.String;

/**
 *
 * 类位置：{@link com.github.lany192.sample.activity.ThreeActivity}
 * 自动生成,请勿编辑!
 */
public class ThreeUI {
    /**
     * 名称
     * 是否必选：false
     */
    private String username;

    /**
     * 用户
     * 是否必选：false
     */
    private User user;

    /**
     * 年龄
     * 是否必选：false
     */
    private int age;

    private ThreeUI() {
    }

    public static ThreeUI builder() {
        return new ThreeUI();
    }

    /**
     * 名称
     * 是否必选：false
     */
    public ThreeUI username(String username) {
        this.username = username;
        return this;
    }

    /**
     * 用户
     * 是否必选：false
     */
    public ThreeUI user(User user) {
        this.user = user;
        return this;
    }

    /**
     * 年龄
     * 是否必选：false
     */
    public ThreeUI age(int age) {
        this.age = age;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_THREE);
        if (username != null) {
            postcard.withString("username",username);
        }
        if (user != null) {
            postcard.withSerializable("user",(com.github.lany192.sample.entity.User)user);
        }
        postcard.withInt("age",age);
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
