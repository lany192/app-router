package com.github.lany192.arouter.sample.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.arouter.sample.R;
import com.github.lany192.arouter.sample.UserHelper;

/**
 * @author lany192
 */
@Route(path = "/app/login", name = "登录界面")
public class LoginActivity extends AppCompatActivity {
    @Autowired(name = "route_path", desc = "跳转路径,不含参数")
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