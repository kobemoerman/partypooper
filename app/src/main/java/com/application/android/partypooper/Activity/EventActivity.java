package com.application.android.partypooper.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.application.android.partypooper.R;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initView();
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        String id = b.getString("id");

        System.out.println(id);
    }
}
