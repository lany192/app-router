package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;
import com.github.lany192.sample.entity.Person;

import java.util.List;

@Route(path = Constants.APP_TWO)
public class TwoActivity extends AppCompatActivity {
    @Autowired(name = "ownerId", desc = "用户id")
    long ownerId;
    @Autowired(name = "title", desc = "标题")
    String mTitle;
    @Autowired(name = "cent", desc = "积分")
    double cent;
    @Autowired(name = "items", desc = "列表")
    List<String> items;
    @Autowired(name = "data", desc = "测试A")
    short data;
    @Autowired(name = "person", desc = "个人")
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView showText = findViewById(R.id.show_text_view);
        StringBuilder builder = new StringBuilder();
        builder.append("界面TWO");
        builder.append("\n用户id:").append(ownerId);
        builder.append("\n标题:").append(mTitle);
        builder.append("\n积分:").append(cent);
        builder.append("\n列表:").append(items);
        builder.append("\n测试A:").append(data);
        builder.append("\n个人:").append(person);
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
