package com.github.lany192.sample.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.AppRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;
import com.github.lany192.sample.User;

import java.util.ArrayList;
import java.util.List;

@Route(path = Constants.APP_MAIN, group = "app", name = "main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(v ->
                AppRouter.get().one(66, true, 10.5f, 'w', "哈哈", (byte) 1, "流利")
        );
        findViewById(R.id.button2).setOnClickListener(v -> {
                    List<String> items = new ArrayList<>();
                    items.add("张三");
                    items.add("王五");
                    AppRouter.get().two(88, "这是一个标题", 8.68, items, (short) 9);
                }
        );
        findViewById(R.id.button3).setOnClickListener(v ->
                AppRouter.get().three("张三", new User("李四", 888), 18)
        );
    }
}
