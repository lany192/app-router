package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.Constants;
import com.github.lany192.sample.R;

@Route(path = Constants.APP_ONE)
public class OneActivity extends AppCompatActivity {
    @Autowired(name = "ownerId", desc = "用户id")
    long ownerId;
    @Autowired(name = "isFans", desc = "是否粉丝")
    boolean isFans;
    @Autowired(name = "money", desc = "余额")
    float money;
    @Autowired(name = "data1", desc = "数据A")
    char data1;
    @Autowired(name = "data2", desc = "数据B")
    CharSequence data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        TextView showText = findViewById(R.id.show_text_view);
        StringBuilder builder = new StringBuilder();
        builder.append("界面ONE");
        builder.append("\n用户id:").append(ownerId);
        builder.append("\n是否粉丝:").append(isFans);
        builder.append("\n余额:").append(money);
        builder.append("\n数据A:").append(data1);
        builder.append("\n数据B:").append(data2);
        showText.setText(builder);
    }
}
