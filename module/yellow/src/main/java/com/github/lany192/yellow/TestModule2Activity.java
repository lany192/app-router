package com.github.lany192.yellow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.yellow.R;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/module/2", group = "m2")
public class TestModule2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_module2);
    }
}
