package com.github.lany192.blue;

import android.content.Context;

import com.alibaba.android.arouter.BlueRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.common.BlueProvider;

@Route(path = "/blue/router")
public class BlueProviderImpl implements BlueProvider {

    @Override
    public void init(Context context) {

    }

    @Override
    public void startBlue() {
        BlueRouter.startHello();
    }
}
