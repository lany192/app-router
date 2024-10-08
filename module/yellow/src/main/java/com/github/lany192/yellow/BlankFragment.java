package com.github.lany192.yellow;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.lany192.arouter.sample.service.model.TestObj;
import com.github.lany192.arouter.sample.service.model.TestParcelable;
import com.github.lany192.arouter.sample.service.model.TestSerializable;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@Route(path = "/test/fragment")
public class BlankFragment extends Fragment {

    @Autowired(desc = "名称")
    String name;

    @Autowired(required = true)
    TestObj obj;

    @Autowired
    int age = 10;

    @Autowired
    int height = 175;

    @Autowired(name = "boy", required = true)
    boolean girl;

    @Autowired
    char ch = 'A';

    @Autowired
    float fl = 12.00f;

    @Autowired
    double dou = 12.01d;

    @Autowired
    TestSerializable ser;

    @Autowired
    TestParcelable pac;

    @Autowired
    List<TestObj> items;

//    @Autowired
//    Map<String, String> map;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        return textView;
    }

}
