package com.github.lany192.blue;

import android.content.Context;

import com.alibaba.android.arouter.BlueRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.common.IBlueRouter;

@Route(path = "/blue/router")
public class BlueRouterImpl implements IBlueRouter {

    @Override
    public void init(Context context) {

    }

    @Override
    public void startBlue() {
        BlueRouter.startHello();
    }
}
