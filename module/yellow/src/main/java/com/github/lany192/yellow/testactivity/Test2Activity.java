package com.github.lany192.yellow.testactivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.yellow.R;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/test/activity2")
public class Test2Activity extends AppCompatActivity {
    @Autowired
    String key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        if (!TextUtils.isEmpty(key1)) {
            Toast.makeText(this, "exist param :" + key1, Toast.LENGTH_LONG).show();
        }
        setResult(999);
    }
}
