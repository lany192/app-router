package com.github.lany192.blue;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.GreenPaths;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/blue/hello")
public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_blue);
        findViewById(R.id.button).setOnClickListener(v -> ARouter.getInstance().build(GreenPaths.GREEN_HELLO).navigation());
    }
}