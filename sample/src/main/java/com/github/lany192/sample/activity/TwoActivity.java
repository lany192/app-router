package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;

@Route(path = Constants.APP_TWO)
public class TwoActivity extends AppCompatActivity {
    @Autowired(name = "ownerId", desc = "用户id")
    long ownerId;
    @Autowired(name = "isFans", desc = "标题")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        TextView showText = (TextView) findViewById(R.id.textView);
        showText.setText("用户id==" + ownerId + "  标题==" + title);
    }
}
