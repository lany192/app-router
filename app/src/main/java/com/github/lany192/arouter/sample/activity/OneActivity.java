package com.github.lany192.arouter.sample.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.arouter.sample.R;

@Route(path = "/app/one")
public class OneActivity extends AppCompatActivity {
    @Autowired(name = "ownerId", desc = "用户id", required = true)
    int ownerId;
    @Autowired(name = "isFans", desc = "是否粉丝")
    boolean isFans;
    @Autowired(name = "money", desc = "余额")
    float money;
    @Autowired(name = "data1", desc = "数据A")
    char data1;
    @Autowired(name = "data2", desc = "数据B")
    CharSequence data2;
    @Autowired(name = "data3", desc = "数据C")
    byte data3;
    @Autowired(desc = "数据D")
    String data4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
