package com.github.lany192.sample.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.Router;
import com.alibaba.android.arouter.facade.annotation.Route;
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
                Router.get().one(66, true, 10.5f,'w',"哈哈",(byte)1,"流利")
        );
        findViewById(R.id.button2).setOnClickListener(v ->
                Router.get().two(88, "这是一个标题", 8.68)
        );
        findViewById(R.id.button3).setOnClickListener(v ->
                Router.get().three("张三", new User("李四", 888), 18)
        );
    }
}
