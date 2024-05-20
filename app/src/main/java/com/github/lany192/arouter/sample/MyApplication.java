package com.github.lany192.arouter.sample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hjq.toast.Toaster;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
        ARouter.init(this);
        ARouter.openDebug();
        ARouter.openLog();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                //给Activity界面注入参数
                ARouter.getInstance().inject(activity);
            }
        });
    }
}
