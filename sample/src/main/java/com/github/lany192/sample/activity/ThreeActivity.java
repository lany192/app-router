package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;

@Route(path = Constants.APP_THREE)
public class ThreeActivity extends AppCompatActivity {
    @Autowired(name = "username", desc = "名称")
    String name;
    @Autowired(name = "age", desc = "年龄")
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        TextView showText = (TextView) findViewById(R.id.my_text_view);
        showText.setText("名称==" + name + "  年龄==" + age);
    }
}
