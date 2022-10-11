package com.github.lany192.sample.fragment;

import com.alibaba.android.arouter.SamplePaths;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import java.lang.String;
import java.util.List;

/**
 * 自动生成,请勿编辑!
 * {@link com.github.lany192.sample.fragment.HelloFragment}
 */
public class HelloBuilder {
    /**
     * 名称
     */
    private String username;

    /**
     * 哈哈
     */
    private String hello_lany;

    /**
     * 哈2哈
     */
    private List<String> items;

    private HelloBuilder() {
    }

    public static HelloBuilder builder() {
        return new HelloBuilder();
    }

    /**
     * 名称
     */
    public HelloBuilder username(String username) {
        this.username = username;
        return this;
    }

    /**
     * 哈哈
     */
    public HelloBuilder helloLany(String hello_lany) {
        this.hello_lany = hello_lany;
        return this;
    }

    /**
     * 哈2哈
     */
    public HelloBuilder items(List<String> items) {
        this.items = items;
        return this;
    }

    /**
     * 构建Fragment实例
     */
    public HelloFragment build() {
        Postcard postcard = ARouter.getInstance().build(SamplePaths.APP_HELLO);
        if (username != null) {
            postcard.withString("username", username);
        }
        if (hello_lany != null) {
            postcard.withString("hello_lany", hello_lany);
        }
        if (items != null) {
            postcard.withObject("items", items);
        }
        return (HelloFragment) postcard.navigation();
    }
}
