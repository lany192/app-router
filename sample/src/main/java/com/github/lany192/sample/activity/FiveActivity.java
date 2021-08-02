package com.github.lany192.sample.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.sample.JsonUtils;
import com.github.lany192.sample.R;
import com.github.lany192.sample.entity.Person;
import com.github.lany192.sample.entity.User;

import java.util.ArrayList;

@Route(path = "/app/five")
public class FiveActivity extends AppCompatActivity {
    @Autowired(name = "users", desc = "用户a")
    ArrayList<User> users;
    @Autowired(name = "persons", desc = "用户b")
    ArrayList<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TextView showText = findViewById(R.id.show_text_view);
        showText.setText(JsonUtils.object2json(users) + "\n" + JsonUtils.object2json(persons));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}