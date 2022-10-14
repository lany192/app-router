package com.github.lany192.common;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface SampleProvider extends IProvider {

    String sayHello();

    void startFive();
}
