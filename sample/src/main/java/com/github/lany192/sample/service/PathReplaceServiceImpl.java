package com.github.lany192.sample.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * 路径转换
     *
     * @param path 路径
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
            if (map.containsKey(host)) {
                Log.i(TAG, "路径替换前path: " + path);
                path = path.replace(host, Objects.requireNonNull(map.get(host)));
                Log.i(TAG, "路径替换后path: " + path);
            }
        }
        return path;
    }
}