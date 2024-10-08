package com.github.lany192.arouter.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.lany192.arouter.sample.R;

import java.util.List;

@Route(path = "/app/hello")
public class HelloFragment extends Fragment {
    @Autowired(name = "username", desc = "名称")
    String name;
    @Autowired(name = "hello_lany", desc = "哈哈")
    String hello;

    @Autowired(name = "items", desc = "哈2哈")
    List<String> items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hello, null);
        TextView showText = view.findViewById(R.id.textView);
        showText.setText(hello + "姓名：" + name);
        return view;
    }
}
