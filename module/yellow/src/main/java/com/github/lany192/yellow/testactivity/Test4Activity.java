package com.github.lany192.yellow.testactivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.yellow.R;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/test/activity4")
public class Test4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        ((TextView) findViewById(R.id.test)).setText("I am " + Test4Activity.class.getName());
        String extra = getIntent().getStringExtra("extra");
        if (!TextUtils.isEmpty(extra)) {
            ((TextView) findViewById(R.id.test2)).setText(extra);
        }
    }
}
