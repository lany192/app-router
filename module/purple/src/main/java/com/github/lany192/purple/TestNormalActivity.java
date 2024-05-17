package com.github.lany192.purple;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.purple.R;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/kotlin/java")
public class TestNormalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_normal);
    }
}
