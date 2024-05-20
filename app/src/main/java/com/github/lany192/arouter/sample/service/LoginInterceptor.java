package com.github.lany192.arouter.sample.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.github.lany192.arouter.sample.activity.LoginRouter;
import com.github.lany192.arouter.sample.activity.ThreeRouter;
import com.github.lany192.arouter.sample.activity.TwoRouter;
import com.github.lany192.arouter.sample.UserHelper;

import java.util.ArrayList;
import java.util.List;

@Interceptor(priority = 1, name = "路由拦截器")
public class LoginInterceptor implements IInterceptor {
    private final String TAG = getClass().getSimpleName();
    /**
     * 需要登录的路径
     */
    private final List<String> paths = new ArrayList<>();

    @Override
    public void init(Context context) {
        //这里添加需要登录的界面路径
        paths.add(ThreeRouter.PATH);
        paths.add(TwoRouter.PATH);
    }

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        if (!UserHelper.get().isLogin() && paths.contains(postcard.getPath())) {
            Log.i(TAG, postcard.getPath() + "路由中断，该path需要登录但未登录，重定向到登录界面");
            callback.onInterrupt(null);
            String path = postcard.getPath();
            Bundle bundle = postcard.getExtras();
            bundle.putString("route_path", path);
            LoginRouter.start(bundle);
            return;
        }
        Log.i(TAG, "不拦截，直接通过" + postcard);
        callback.onContinue(postcard);
    }
}
