package com.github.lany192.green;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.common.IBlueRouter;

@Route(path = "/green/hello")
public class HelloActivity extends AppCompatActivity {
//    @Autowired
//    Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_hello_green);
        findViewById(R.id.button).setOnClickListener(v -> {
            IBlueRouter blueRouter = ARouter.getInstance().navigation(IBlueRouter.class);
            blueRouter.startBlue();
        });
    }
}