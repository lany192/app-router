package com.alibaba.android.arouter.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.demo.databinding.ActivityMainBinding;
import com.alibaba.android.arouter.demo.kotlin.KotlinTestUI;
import com.alibaba.android.arouter.demo.module1.testactivity.TestDynamicActivity;
import com.alibaba.android.arouter.demo.module1.testservice.SingleService;
import com.alibaba.android.arouter.demo.service.HelloService;
import com.alibaba.android.arouter.demo.service.model.TestObj;
import com.alibaba.android.arouter.demo.service.model.TestParcelable;
import com.alibaba.android.arouter.demo.service.model.TestSerializable;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = "/app/main", name = "测试用 Activity")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.openLog.setOnClickListener(v -> ARouter.openLog());
        binding.init.setOnClickListener(v -> {
            // 调试模式不是必须开启，但是为了防止有用户开启了InstantRun，但是
            // 忘了开调试模式，导致无法使用Demo，如果使用了InstantRun，必须在
            // 初始化之前开启调试模式，但是上线前需要关闭，InstantRun仅用于开
            // 发阶段，线上开启调试模式有安全风险，可以使用BuildConfig.DEBUG
            // 来区分环境
            ARouter.openDebug();
            ARouter.init(getApplication());
        });
        binding.normalNavigation.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/activity2")
                    .navigation();
            // 也可以通过依赖对方提供的二方包来约束入参
            // 非必须，可以通过这种方式调用
            // Entrance.redirect2Test1Activity("张飞", 48, this);
        });
        binding.kotlinNavigation.setOnClickListener(v -> {
            KotlinTestUI.builder().name("哈哈张三").age(18).build();
        });
        binding.normalNavigationWithParams.setOnClickListener(v -> {
            Uri testUriMix = Uri.parse("arouter://m.aliyun.com/test/activity2");
            ARouter.getInstance().build(testUriMix)
                    .withString("key1", "value1")
                    .navigation();
        });
        binding.oldVersionAnim.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/activity2")
                    .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .navigation(this);
        });
        binding.newVersionAnim.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 16) {
                ActivityOptionsCompat compat = ActivityOptionsCompat.
                        makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);

                ARouter.getInstance()
                        .build("/test/activity2")
                        .withOptionsCompat(compat)
                        .navigation();
            } else {
                Toast.makeText(this, "API < 16,不支持新版本动画", Toast.LENGTH_SHORT).show();
            }
        });
        binding.interceptor.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/activity4")
                    .navigation(this, new NavCallback() {
                        @Override
                        public void onArrival(Postcard postcard) {

                        }

                        @Override
                        public void onInterrupt(Postcard postcard) {
                            Log.d("ARouter", "被拦截了");
                        }
                    });
        });
        binding.navByUrl.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/webview")
                    .withString("url", "file) {///android_asset/scheme-test.html")
                    .navigation();
        });
        binding.autoInject.setOnClickListener(v -> {
            TestSerializable testSerializable = new TestSerializable("Titanic", 555);
            TestParcelable testParcelable = new TestParcelable("jack", 666);
            TestObj testObj = new TestObj("Rose", 777);
            List<TestObj> objList = new ArrayList<>();
            objList.add(testObj);
            Map<String, List<TestObj>> map = new HashMap<>();
            map.put("testMap", objList);
            ARouter.getInstance().build("/test/activity1")
                    .withString("name", "老王")
                    .withInt("age", 18)
                    .withBoolean("boy", true)
                    .withLong("high", 180)
                    .withString("url", "https) {//a.b.c")
                    .withSerializable("ser", testSerializable)
                    .withParcelable("pac", testParcelable)
                    .withObject("obj", testObj)
                    .withObject("objList", objList)
                    .withObject("map", map)
                    .navigation();
        });
        binding.navByName.setOnClickListener(v -> {
            ((HelloService) ARouter.getInstance().build("/yourservicegroupname/hello").navigation()).sayHello("mike");
        });
        binding.navByType.setOnClickListener(v -> {
            ARouter.getInstance().navigation(HelloService.class).sayHello("mike");
        });
        binding.navToMoudle1.setOnClickListener(v -> {
            ARouter.getInstance().build("/module/1").navigation();
        });
        binding.navToMoudle2.setOnClickListener(v -> {
            // 这个页面主动指定了Group名
            ARouter.getInstance().build("/module/2", "m2").navigation();
        });
        binding.destroy.setOnClickListener(v -> {
            ARouter.getInstance().destroy();
        });
        binding.failNav.setOnClickListener(v -> {
            ARouter.getInstance().build("/xxx/xxx").navigation(this, new NavCallback() {
                @Override
                public void onFound(Postcard postcard) {
                    Log.d("ARouter", "找到了");
                }

                @Override
                public void onLost(Postcard postcard) {
                    Log.d("ARouter", "找不到了");
                }

                @Override
                public void onArrival(Postcard postcard) {
                    Log.d("ARouter", "跳转完了");
                }

                @Override
                public void onInterrupt(Postcard postcard) {
                    Log.d("ARouter", "被拦截了");
                }
            });
        });
        binding.callSingle.setOnClickListener(v -> {
            ARouter.getInstance().navigation(SingleService.class).sayHello("Mike");
        });
        binding.failNav2.setOnClickListener(v -> {
            ARouter.getInstance().build("/xxx/xxx").navigation();
        });
        binding.failNav3.setOnClickListener(v -> {
            ARouter.getInstance().navigation(MainActivity.class);
        });
        binding.normalNavigation2.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/activity2")
                    .navigation(this, 666);
        });
        binding.getFragment.setOnClickListener(v -> {
            TestSerializable testSerializable = new TestSerializable("Titanic", 555);
            TestParcelable testParcelable = new TestParcelable("jack", 666);
            TestObj testObj = new TestObj("Rose", 777);
            List<TestObj> objList = new ArrayList<>();
            objList.add(testObj);
            Map<String, List<TestObj>> map = new HashMap<>();
            map.put("testMap", objList);
            Fragment fragment = (Fragment) ARouter.getInstance().build("/test/fragment")
                    .withString("name", "老王")
                    .withInt("age", 18)
                    .withBoolean("boy", true)
                    .withLong("high", 180)
                    .withString("url", "https) {//a.b.c")
                    .withSerializable("ser", testSerializable)
                    .withParcelable("pac", testParcelable)
                    .withObject("obj", testObj)
                    .withObject("objList", objList)
                    .withObject("map", map).navigation();
            Toast.makeText(this, "找到Fragment) {" + fragment.toString(), Toast.LENGTH_SHORT).show();
        });
        binding.addGroup.setOnClickListener(v -> {
            ARouter.getInstance().addRouteGroup(new IRouteGroup() {
                @Override
                public void loadInto(Map<String, RouteMeta> atlas) {
                    atlas.put("/dynamic/activity", RouteMeta.build(
                            RouteType.ACTIVITY,
                            TestDynamicActivity.class,
                            "/dynamic/activity",
                            "dynamic", 0, 0));
                }
            });
        });
        binding.dynamicNavigation.setOnClickListener(v -> {
            ARouter.getInstance().addRouteGroup(new IRouteGroup() {
                @Override
                public void loadInto(Map<String, RouteMeta> atlas) {
                    TestSerializable testSerializable = new TestSerializable("Titanic", 555);
                    TestParcelable testParcelable = new TestParcelable("jack", 666);
                    TestObj testObj = new TestObj("Rose", 777);
                    List<TestObj> objList = new ArrayList<>();
                    objList.add(testObj);
                    Map<String, List<TestObj>> map = new HashMap<>();
                    map.put("testMap", objList);
                    // 该页面未配置 Route 注解，动态注册到 ARouter
                    ARouter.getInstance().build("/dynamic/activity")
                            .withString("name", "老王")
                            .withInt("age", 18)
                            .withBoolean("boy", true)
                            .withLong("high", 180)
                            .withString("url", "https) {//a.b.c")
                            .withSerializable("ser", testSerializable)
                            .withParcelable("pac", testParcelable)
                            .withObject("obj", testObj)
                            .withObject("objList", objList)
                            .withObject("map", map).navigation(MainActivity.this);
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 666:
                Log.e("activityResult", String.valueOf(resultCode));
                break;
            default:
                break;
        }
    }
}
