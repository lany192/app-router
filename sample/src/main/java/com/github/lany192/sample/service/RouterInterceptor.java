package com.github.lany192.sample.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.AppRouter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;

@Interceptor(priority = 1, name = "路由拦截器")
public class RouterInterceptor implements IInterceptor {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        Log.i(TAG, "路由中转:" + postcard.getPath());
        //需要登录的path要以/user/开头，规则自己定义
        boolean needLogin = postcard.getGroup().equals("user");
        Log.i(TAG, "拦截地址:" + postcard.getPath() + "，是否需要登录：" + needLogin +
                ",登录请求码：" + postcard.getExtra());
        //判断是否需要登录
        if (needLogin) {
            //判断是否已经登录
            if (true) {
                callback.onContinue(postcard);
            } else {
                Log.i(TAG, "路由中断，先去登录");
                callback.onInterrupt(null);
                //跳转到登录界面
                AppRouter.get().skip(postcard.getPath());
            }
        } else {
            //不拦截，直接通过
            callback.onContinue(postcard);
        }
    }

    @Override
    public void init(Context context) {

    }
}
