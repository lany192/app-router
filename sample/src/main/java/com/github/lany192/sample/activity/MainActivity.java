package com.github.lany192.sample.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.AppRouter;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;
import com.github.lany192.sample.User;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;

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
        Fragment fragment = AppRouter.get().getHelloFragment("张无忌");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fl_fragment_content, fragment);
        transaction.commit();

        String link1 = "demo://m.test.com/app/one";
        String link2 = "demo://m.test.com/app/two";
        String link3 = "demo://m.test.com/app/three?age=18&username=张三";

        String content = "跳转:\n\n第一个界面:" + link1 + "\n\n第二个界面:" + link2 + "\n\n第三个界面:" + link3;
        TextView demoText = findViewById(R.id.textView);
        demoText.setText(LinkBuilder.from(this, content)
                .addLink(new Link(link1)
                        .setTextColor(Color.BLUE)
                        .setOnClickListener(clickedText -> {
                            AppRouter.get().skip(Uri.parse(clickedText));
                        }))
                .addLink(new Link(link2)
                        .setTextColor(Color.BLUE)
                        .setOnClickListener(clickedText -> {
                            AppRouter.get().skip(Uri.parse(clickedText));
                        }))
                .addLink(new Link(link3)
                        .setTextColor(Color.BLUE)
                        .setOnClickListener(clickedText -> {
                            AppRouter.get().skip(Uri.parse(clickedText));
                        }))
                .build());
        demoText.setMovementMethod(TouchableMovementMethod.getInstance());
    }
}
