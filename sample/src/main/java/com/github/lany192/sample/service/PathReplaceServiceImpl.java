package com.github.lany192.sample.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.github.lany192.sample.Constants;

@Route(path = "/service/path/replace")
public class PathReplaceServiceImpl implements PathReplaceService {
    Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public String forString(String path) {
        Log.e("/test", "forString " + path);
        if ("/one".equals(path)) {
            return Constants.APP_ONE;
        }
        return path;
    }

    @Override
    public Uri forUri(Uri uri) {
//        Log.e("/test", "forUri " + uri);
//        if ("arouter://m.aliyun.com/test/activity1".equals(uri.toString())) {
//            uri = Uri.parse("arouter://m.aliyun.com/test/activity2");
//        } else if ("arouter://m.aliyun.com/test/activity2".equals(uri.toString())) {
//            uri = Uri.parse("arouter://m.aliyun.com/test/activity1");
//        }
        return uri;
    }
}