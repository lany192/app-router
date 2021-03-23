package com.github.lany192.sample.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;
import com.github.lany192.sample.User;

@Route(path = Constants.APP_MAIN, group = "app", name = "main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(v ->
                ARouter.getInstance().build(Constants.APP_ONE)
                        .withLong("ownerId", 666)
                        .withBoolean("isFans", false)
                        .navigation()
        );
        findViewById(R.id.button2).setOnClickListener(v ->
                ARouter.getInstance().build(Constants.APP_TWO)
                        .withLong("ownerId", 666)
                        .withString("title", "这是一个标题")
                        .navigation()
        );
        findViewById(R.id.button3).setOnClickListener(v ->
                ARouter.getInstance().build(Constants.APP_THREE)
                        .withString("username", "张三")
                        .withObject("user", new User("李四", 888))
                        .navigation()
        );
    }
}
