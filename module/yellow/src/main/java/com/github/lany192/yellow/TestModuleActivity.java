package com.github.lany192.yellow;

import android.app.Activity;
import android.os.Bundle;

import com.github.lany192.yellow.R;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/yellow/test")
public class TestModuleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_module);
    }
}
