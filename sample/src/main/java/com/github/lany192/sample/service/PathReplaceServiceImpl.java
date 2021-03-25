package com.github.lany192.sample.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.github.lany192.sample.Constants;

/**
 * @author Administrator
 */
@Route(path = "/service/path/replace")
public class PathReplaceServiceImpl implements PathReplaceService {
    private final String TAG = getClass().getSimpleName();
    Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public String forString(String path) {
        if ("/one".equals(path)) {
            return Constants.APP_ONE;
        }
        return path;
    }

    @Override
    public Uri forUri(Uri uri) {
        Log.i(TAG, "Uri: " + uri);
        if ("lzj3000://leaderboards?age=18&username=张三".equals(uri.toString())) {
            uri = Uri.parse("demo://m.test.com/app/three?age=18&username=张三");
        }
        return uri;
    }
}