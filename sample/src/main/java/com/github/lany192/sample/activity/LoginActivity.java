package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.R;
import com.github.lany192.sample.UserHelper;

/**
 * @author Administrator
 */
@Route(path = "/app/login")
public class LoginActivity extends AppCompatActivity {
    @Autowired(name = "path", desc = "跳转路径")
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.button).setOnClickListener(v -> {
            Log.i("TAG", "跳转path: " + path);
            UserHelper.get().setLogin(true);
            ARouter.getInstance().build(path).with(getIntent().getExtras()).navigation();
            finish();
        });
    }
}