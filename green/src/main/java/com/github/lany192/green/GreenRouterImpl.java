package com.github.lany192.green;

import android.content.Context;

import com.alibaba.android.arouter.GreenRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.common.IGreenRouter;

@Route(path = "/green/router")
public class GreenRouterImpl implements IGreenRouter {

    @Override
    public void init(Context context) {

    }

    @Override
    public void startGreen() {
        GreenRouter.startHello();
    }
}
