package com.github.lany192.sample.activity;

import android.net.Uri;
import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.entity.Person;
import java.lang.String;
import java.util.List;

/**
 *
 * 类位置：{@link com.github.lany192.sample.activity.TwoActivity}
 * 自动生成,请勿编辑!
 */
public class TwoUI {
    /**
     * 用户id
     * 是否必选：false
     */
    private long ownerId;

    /**
     * 标题
     * 是否必选：false
     */
    private String title;

    /**
     * 积分
     * 是否必选：false
     */
    private double cent;

    /**
     * 列表
     * 是否必选：false
     */
    private List<String> items;

    /**
     * 测试A
     * 是否必选：false
     */
    private short data;

    /**
     * 个人
     * 是否必选：false
     */
    private Person person;

    private TwoUI() {
    }

    public static TwoUI builder() {
        return new TwoUI();
    }

    /**
     * 用户id
     * 是否必选：false
     */
    public TwoUI ownerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    /**
     * 标题
     * 是否必选：false
     */
    public TwoUI title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 积分
     * 是否必选：false
     */
    public TwoUI cent(double cent) {
        this.cent = cent;
        return this;
    }

    /**
     * 列表
     * 是否必选：false
     */
    public TwoUI items(List<String> items) {
        this.items = items;
        return this;
    }

    /**
     * 测试A
     * 是否必选：false
     */
    public TwoUI data(short data) {
        this.data = data;
        return this;
    }

    /**
     * 个人
     * 是否必选：false
     */
    public TwoUI person(Person person) {
        this.person = person;
        return this;
    }

    /**
     * 组建Postcard
     */
    public Postcard postcard() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_TWO);
        postcard.withLong("ownerId",ownerId);
        if (title != null) {
            postcard.withString("title",title);
        }
        postcard.withDouble("cent",cent);
        if (items != null) {
        }
        postcard.withShort("data",data);
        if (person != null) {
            postcard.withSerializable("person",(com.github.lany192.sample.entity.Person)person);
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
