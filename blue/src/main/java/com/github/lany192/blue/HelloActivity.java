package com.github.lany192.blue;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.common.GreenProvider;
import com.github.lany192.common.SampleProvider;

@Route(path = "/blue/hello")
public class HelloActivity extends AppCompatActivity {
    @Autowired
    GreenProvider greenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_hello_blue);
        findViewById(R.id.button).setOnClickListener(v -> {
            greenProvider.startGreen();
        });
        findViewById(R.id.show).setOnClickListener(v -> {
            SampleProvider sampleProvider = ARouter.getInstance().navigation(SampleProvider.class);
            Toast.makeText(this, sampleProvider.sayHello(), Toast.LENGTH_SHORT).show();
        });
    }
}