package com.github.lany192.sample.provider;

import android.content.Context;

import com.alibaba.android.arouter.BlueRouter;
import com.alibaba.android.arouter.GreenRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.common.Router;

@Route(path = "/common/router")
public class RouterImpl implements Router {

    @Override
    public void init(Context context) {

    }

    @Override
    public void startBlue() {
        BlueRouter.startHello();
    }

    @Override
    public void startGreen() {
        GreenRouter.startHello();
    }
}
