package com.github.lany192.blue;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.common.Router;

@Route(path = "/blue/hello")
public class HelloActivity extends AppCompatActivity {
    @Autowired
    Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_hello_blue);
        findViewById(R.id.button).setOnClickListener(v -> router.startGreen());
    }
}