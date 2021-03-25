package com.github.lany192.sample.fragment;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/app/hello", group = "app")
public class HelloFragment extends Fragment {
    @Autowired(name = "username", desc = "名称")
    String name;


}
