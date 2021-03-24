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
    @Autowired(name = "title", desc = "标题")
    String title;
    @Autowired(name = "cent", desc = "积分")
    double cent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        TextView showText = findViewById(R.id.show_text_view);
        StringBuilder builder = new StringBuilder();
        builder.append("界面TWO");
        builder.append("\n用户id:").append(ownerId);
        builder.append("\n标题:").append(title);
        builder.append("\n积分:").append(cent);
        showText.setText(builder);
    }
}
