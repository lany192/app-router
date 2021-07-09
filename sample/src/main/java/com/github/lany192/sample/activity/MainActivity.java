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
import com.github.lany192.sample.R;
import com.github.lany192.sample.UserHelper;
import com.github.lany192.sample.entity.Person;
import com.github.lany192.sample.entity.User;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/app/main")
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
                    AppRouter.get().two(88, "这是一个标题", 8.68, items, (short) 9, new Person("王武", 89));
                }
        );
        findViewById(R.id.button3).setOnClickListener(v ->
                AppRouter.get().three("张三,我来至按钮", new User("李四", 888), 18)
        );
        findViewById(R.id.button4).setOnClickListener(v -> {
                    UserHelper.get().setLogin(false);
                }
        );
        findViewById(R.id.button5).setOnClickListener(v -> {
                    List<User> items = new ArrayList<>();
                    items.add(new User("战三", 123));
                    items.add(new User("哈哈", 321));
                    List<Person> items2 = new ArrayList<>();
                    items2.add(new Person("2战三", 123));
                    items2.add(new Person("2哈哈", 321));
                    AppRouter.get().five(items, items2);
                }
        );
        Fragment fragment = AppRouter.get().getHello("张无忌","哈哈");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fl_fragment_content, fragment);
        transaction.commit();

        String link1 = "demo://m.test.com/app/one";
        String link2 = "demo://m.test.com/app/two";
        String link3 = "demo://m.test.com/app/three?age=18&username=张三";
        String link4 = "hello://leaderboards?age=18&username=张三";
        String link5 = "demo://m.test.com/app/four?title=呵呵";

        String content = "跳转:\n\n第一个界面:" + link1
                + "\n\n第二个界面:" + link2
                + "\n\n第三个界面:" + link3
                + "\n\n第三个界面:" + link4
                + "\n\n第四个界面:" + link5;
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
                .addLink(new Link(link4)
                        .setTextColor(Color.BLUE)
                        .setOnClickListener(clickedText -> {
                            AppRouter.get().skip(Uri.parse(clickedText));
                        }))
                .addLink(new Link(link5)
                        .setTextColor(Color.BLUE)
                        .setOnClickListener(clickedText -> {
                            AppRouter.get().skip(Uri.parse(clickedText));
                        }))
                .build());
        demoText.setMovementMethod(TouchableMovementMethod.getInstance());
    }
}
