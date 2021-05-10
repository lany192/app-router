package com.github.lany192.sample.service;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@Route(path = "/service/path/replace")
public class PathReplaceServiceImpl implements PathReplaceService {
    private final String TAG = getClass().getSimpleName();
    /**
     * 新旧路径映射表
     */
    private final Map<String, String> map = new HashMap<>();

    @Override
    public void init(Context context) {
        map.put("valuePack", "/app/send/coins/pay");
        map.put("leaderboards", "/app/three");
    }

    @Override
    public String forString(String path) {
        return replace(path);
    }

    @Override
    public Uri forUri(Uri uri) {
        return Uri.parse(replace(uri.toString()));
    }

    /**
     * 检查host是否包含在集合中。为了防止产品定义的协议出现大小写不定问题,统一用小写形式进行比较
     */
    private boolean checkHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        for (String key : map.keySet()) {
            if (key.toLowerCase().contains(host.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取新的host
     *
     * @param host 旧host
     * @return 新host
     */
    private String getNewHostByOldHost(String host) {
        for (String key : map.keySet()) {
            if (key.toLowerCase().contains(host.toLowerCase())) {
                return map.get(key);
            }
        }
        return host;
    }

    /**
     * 路径转换
     *
     * @param path 旧路径
     * @return 新路径
     */
    private String replace(String path) {
        if (path.contains("//")) {
            int beginIndex = path.indexOf("//");
            String host;
            if (path.contains("?")) {
                int endIndex = path.indexOf("?");
                host = path.substring(beginIndex + 2, endIndex);
            } else {
                host = path.substring(beginIndex + 2);
            }
            Log.i(TAG, "host: " + host);
            if (checkHost(host)) {
                String newHost = getNewHostByOldHost(host);
                Log.i(TAG, "替换前: " + path);
                if (path.contains("?") && newHost.contains("?")) {
                    path = path.replace(host + "?", newHost + "&");
                } else {
                    path = path.replace(host, newHost);
                }
                Log.i(TAG, "替换后: " + path);
            } else {
                //TODO 如果不符合格式的协议跳转
                if (host.contains("//")) {

                } else {

                }
            }
        }
        return path;
    }
}