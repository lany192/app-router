package com.github.lany192.arouter.demo.sample.service;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lany192
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
            if (key.toLowerCase().equals(host.toLowerCase())) {
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
            if (key.toLowerCase().equals(host.toLowerCase())) {
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
            if (checkHost(host)) {
                String newHost = getNewHostByOldHost(host);
                if (path.contains("?") && newHost.contains("?")) {
                    path = path.replace(host + "?", newHost + "&");
                } else {
                    path = path.replace(host, newHost);
                }
            } else {
                //只有斜杆开头的才是符合要求的，不符合要求的自动添加一个error，用于防止报错
                if (!host.startsWith("/")) {
                    path = path.replace(host, "/error/" + host);
//                    log.i("协议不符合规则,映射表和自定义协议中都找不到:" + path);
                }
            }
            return path;
        } else {
            return checkPath(path);
        }
    }

    /**
     * 提前检查路径是否合理,避免ARouter报错，检查规则参考ARouter内部判断
     */
    private String checkPath(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
//            log.e("协议不符合规则，重置跳转到主界面，原路径：" + path);
            return "/error/" + path;
        }
        try {
            String group = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(group)) {
//                log.e("协议不符合规则，重置跳转到主界面，原路径：" + path);
                return "/error/" + path;
            }
        } catch (Exception e) {
//            log.e("协议不符合规则，重置跳转到主界面，原路径：" + path);
            return "/error/" + path;
        }
        return path;
    }
}