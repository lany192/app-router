package com.github.lany192.arouter.demo.sample.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.arouter.demo.R;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.arouter.demo.sample.entity.User;

@Route(path = "/app/three")
public class ThreeActivity extends AppCompatActivity {
    @Autowired(name = "username", desc = "名称")
    String name;
    @Autowired(name = "user", desc = "用户")
    User user;
    @Autowired(name = "age", desc = "年龄")
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView showText = findViewById(R.id.show_text_view);
        StringBuilder builder = new StringBuilder();
        builder.append("界面THREE");
        builder.append("\n名称:").append(name);
        builder.append("\n年龄:").append(age);
        builder.append("\n用户:").append(user);
        showText.setText(builder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
