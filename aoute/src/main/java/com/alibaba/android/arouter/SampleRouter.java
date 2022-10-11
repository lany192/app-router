package com.alibaba.android.arouter;

import android.net.Uri;
import android.os.Bundle;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.sample.activity.FiveUI;
import com.github.lany192.sample.activity.FourUI;
import com.github.lany192.sample.activity.LoginUI;
import com.github.lany192.sample.activity.MainUI;
import com.github.lany192.sample.activity.OneUI;
import com.github.lany192.sample.activity.ThreeUI;
import com.github.lany192.sample.activity.TwoUI;
import com.github.lany192.sample.entity.Person;
import com.github.lany192.sample.entity.User;
import com.github.lany192.sample.fragment.HelloBuilder;
import com.github.lany192.sample.fragment.HelloFragment;
import java.lang.CharSequence;
import java.lang.String;
import java.util.List;

/**
 * 路由助手,自动生成,请勿编辑!
 */
public class SampleRouter {
    /**
     * 通用跳转
     * @param path 路由路径
     */
    public static void skip(String path) {
        ARouter.getInstance().build(path).navigation();
    }

    /**
     * 通用跳转
     * @param path 路由路径
     * @param bundle Bundle对象
     */
    public static void skip(String path, Bundle bundle) {
        ARouter.getInstance().build(path).with(bundle).navigation();
    }

    /**
     * 通用跳转
     * @param uri 路由路径
     */
    public static void skip(Uri uri) {
        ARouter.getInstance().build(uri).navigation();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.FiveActivity}
     * @param users 用户a
     * @param persons_items 用户b
     */
    public static void startFive(List<User> users, List<Person> persons_items) {
        FiveUI.builder().users(users).personsItems(persons_items).build();
    }

    /**
     * 登录界面
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.LoginActivity}
     * @param route_path 跳转路径,不含参数
     */
    public static void startLogin(String route_path) {
        LoginUI.builder().routePath(route_path).build();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.MainActivity}
     */
    public static void startMain() {
        MainUI.builder().build();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.OneActivity}
     * @param ownerId 用户id
     * @param isFans 是否粉丝
     * @param money 余额
     * @param data1 数据A
     * @param data2 数据B
     * @param data3 数据C
     * @param data4 数据D
     */
    public static void startOne(int ownerId, boolean isFans, float money, char data1,
            CharSequence data2, byte data3, String data4) {
        OneUI.builder().ownerId(ownerId).isFans(isFans).money(money).data1(data1).data2(data2).data3(data3).data4(data4).build();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.ThreeActivity}
     * @param username 名称
     * @param user 用户
     * @param age 年龄
     */
    public static void startThree(String username, User user, int age) {
        ThreeUI.builder().username(username).user(user).age(age).build();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.TwoActivity}
     * @param ownerId 用户id
     * @param title 标题
     * @param cent 积分
     * @param items 列表
     * @param data 测试A
     * @param person 个人
     */
    public static void startTwo(long ownerId, String title, double cent, List<String> items,
            short data, Person person) {
        TwoUI.builder().ownerId(ownerId).title(title).cent(cent).items(items).data(data).person(person).build();
    }

    /**
     * 获取实例{@link com.github.lany192.sample.fragment.HelloFragment}
     * @param username 名称
     * @param hello_lany 哈哈
     * @param items 哈2哈
     */
    public static HelloFragment getHello(String username, String hello_lany, List<String> items) {
        return HelloBuilder.builder().username(username).helloLany(hello_lany).items(items).build();
    }

    /**
     *
     *
     * 类位置：{@link com.github.lany192.sample.activity.FourActivity}
     * @param title 标题
     */
    public static void startFour(String title) {
        FourUI.builder().title(title).build();
    }
}
