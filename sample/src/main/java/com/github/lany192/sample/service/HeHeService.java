package com.github.lany192.sample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/service/hehe")
public class HeHeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
