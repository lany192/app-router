package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import java.lang.String;

/**
 *
 * 类位置：{@link com.github.lany192.sample.activity.FourActivity}
 * 自动生成,请勿编辑!
 */
public class FourUI {
    /**
     * 标题
     * 是否必选：false
     */
    private String title;

    private FourUI() {
    }

    public static FourUI builder() {
        return new FourUI();
    }

    /**
     * 标题
     * 是否必选：false
     */
    public FourUI title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_FOUR);
        if (title != null) {
            postcard.withString("title",title);
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
