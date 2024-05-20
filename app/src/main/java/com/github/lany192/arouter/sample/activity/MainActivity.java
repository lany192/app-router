package com.github.lany192.arouter.sample.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.arouter.sample.R;
import com.github.lany192.arouter.sample.databinding.ActivityMainBinding;
import com.github.lany192.arouter.sample.activity.SampleRouter;
import com.github.lany192.arouter.sample.service.HelloService;
import com.github.lany192.arouter.sample.service.model.TestObj;
import com.github.lany192.arouter.sample.service.model.TestParcelable;
import com.github.lany192.arouter.sample.service.model.TestSerializable;
import com.github.lany192.purple.KotlinTestRouter;
import com.github.lany192.yellow.BlankBuilder;
import com.github.lany192.yellow.TestWebviewRouter;
import com.github.lany192.yellow.testactivity.Test2Router;
import com.github.lany192.yellow.testactivity.Test4Router;
import com.github.lany192.yellow.testactivity.TestDynamicActivity;
import com.github.lany192.yellow.testservice.SingleService;
import com.hjq.toast.Toaster;

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
        binding.test.setOnClickListener(v -> Test2Router.start("哈哈"));
        binding.test3.setOnClickListener(v -> KotlinTestRouter.start("张三", 18));
        binding.testWithParams.setOnClickListener(v -> {
            Uri testUriMix = Uri.parse("arouter://m.aliyun.com/test/activity2");
            ARouter.getInstance().build(testUriMix)
                    .withString("key1", "value1")
                    .navigation();
        });
        binding.oldVersionAnim.setOnClickListener(v -> {
            Test2Router.getPostcard("张三")
                    .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .navigation(this);
        });
        binding.newVersionAnim.setOnClickListener(v -> {
            ActivityOptionsCompat compat = ActivityOptionsCompat.
                    makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
            Test2Router.getPostcard("张三")
                    .withOptionsCompat(compat)
                    .navigation();
        });
        binding.interceptor.setOnClickListener(v -> Test4Router.start(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {

            }
            @Override
            public void onInterrupt(Postcard postcard) {
                Toaster.show("被拦截了");
            }
        }));
        binding.navByUrl.setOnClickListener(v -> TestWebviewRouter.start("file) {///android_asset/scheme-test.html"));
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
        binding.yellow.setOnClickListener(v -> {
            ARouter.getInstance().build("/yellow/test").navigation();
        });
        binding.navToMoudle2.setOnClickListener(v -> {
            // 这个页面主动指定了Group名
            ARouter.getInstance().build("/module/2", "m2").navigation();
        });

        binding.failNav.setOnClickListener(v -> {
            ARouter.getInstance().build("/xxx/xxx").navigation(this, new NavCallback() {
                @Override
                public void onFound(Postcard postcard) {
                    Toaster.show("找到了");
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
        binding.test2.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/test/activity2")
                    .navigation(this, 666);
        });
        binding.test4.setOnClickListener(v -> {
            TestSerializable testSerializable = new TestSerializable("Titanic", 555);
            TestParcelable testParcelable = new TestParcelable("jack", 666);
            TestObj testObj = new TestObj("Rose", 777);
            List<TestObj> objList = new ArrayList<>();
            objList.add(testObj);
            Map<String, String> map = new HashMap<>();
            map.put("testMap", "666");

            Fragment fragment = BlankBuilder.getFragment("老王", testObj, 18, 180, true, 'a', 1.8f, 1.8, testSerializable, testParcelable, objList);
            Toaster.show("找到Fragment:" + fragment.toString());
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
            ARouter.getInstance().addRouteGroup(atlas -> {
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
            });
        });
        binding.other.setOnClickListener(v -> SampleRouter.start());
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
