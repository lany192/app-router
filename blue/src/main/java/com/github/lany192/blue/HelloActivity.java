package com.github.lany192.blue;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.common.GreenProvider;

@Route(path = "/blue/hello")
public class HelloActivity extends AppCompatActivity {
//    @Autowired
//    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_blue);
        findViewById(R.id.button).setOnClickListener(v -> {
            GreenProvider greenProvider = ARouter.getInstance().navigation(GreenProvider.class);
            greenProvider.startGreen();
        });
    }
}