package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import java.lang.CharSequence;
import java.lang.String;

/**
 *
 * 类位置：{@link com.github.lany192.sample.activity.OneActivity}
 * 自动生成,请勿编辑!
 */
public class OneUI {
    /**
     * 用户id
     * 是否必选：true
     */
    private int ownerId;

    /**
     * 是否粉丝
     * 是否必选：false
     */
    private boolean isFans;

    /**
     * 余额
     * 是否必选：false
     */
    private float money;

    /**
     * 数据A
     * 是否必选：false
     */
    private char data1;

    /**
     * 数据B
     * 是否必选：false
     */
    private CharSequence data2;

    /**
     * 数据C
     * 是否必选：false
     */
    private byte data3;

    /**
     * 数据D
     * 是否必选：false
     */
    private String data4;

    private OneUI() {
    }

    public static OneUI builder() {
        return new OneUI();
    }

    /**
     * 用户id
     * 是否必选：true
     */
    public OneUI ownerId(int ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    /**
     * 是否粉丝
     * 是否必选：false
     */
    public OneUI isFans(boolean isFans) {
        this.isFans = isFans;
        return this;
    }

    /**
     * 余额
     * 是否必选：false
     */
    public OneUI money(float money) {
        this.money = money;
        return this;
    }

    /**
     * 数据A
     * 是否必选：false
     */
    public OneUI data1(char data1) {
        this.data1 = data1;
        return this;
    }

    /**
     * 数据B
     * 是否必选：false
     */
    public OneUI data2(CharSequence data2) {
        this.data2 = data2;
        return this;
    }

    /**
     * 数据C
     * 是否必选：false
     */
    public OneUI data3(byte data3) {
        this.data3 = data3;
        return this;
    }

    /**
     * 数据D
     * 是否必选：false
     */
    public OneUI data4(String data4) {
        this.data4 = data4;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_ONE);
        postcard.withInt("ownerId",ownerId);
        postcard.withBoolean("isFans",isFans);
        postcard.withFloat("money",money);
        postcard.withChar("data1",data1);
        if (data2 != null) {
            postcard.withCharSequence("data2",data2);
        }
        postcard.withByte("data3",data3);
        if (data4 != null) {
            postcard.withString("data4",data4);
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
