package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import java.lang.String;

/**
 * 登录界面
 * 类位置：{@link com.github.lany192.sample.activity.LoginActivity}
 * 自动生成,请勿编辑!
 */
public class LoginUI {
    /**
     * 跳转路径,不含参数
     * 是否必选：false
     */
    private String route_path;

    private LoginUI() {
    }

    public static LoginUI builder() {
        return new LoginUI();
    }

    /**
     * 跳转路径,不含参数
     * 是否必选：false
     */
    public LoginUI routePath(String route_path) {
        this.route_path = route_path;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_LOGIN);
        if (route_path != null) {
            postcard.withString("route_path",route_path);
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
