package com.github.lany192.yellow.testactivity;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;

/**
 * Base Activity (Used for test inject)
 */
public class BaseActivity extends AppCompatActivity {
    @Autowired(desc = "姓名")
    String name = "jack";
}
