package com.github.lany192.green;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.common.BluePaths;

@Route(path = "/green/hello")
public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_green);
        findViewById(R.id.button).setOnClickListener(v -> ARouter.getInstance().build(BluePaths.BLUE_HELLO).navigation());
    }
}