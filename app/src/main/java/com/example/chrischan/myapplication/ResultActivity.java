package com.example.chrischan.myapplication;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ResultActivity extends AppCompatActivity {

    private ViewPager viewPager;//页卡内容
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra("data");
        boolean isFavs = bundle.getBoolean("FAVS");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Results");
        }


        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new ResultAdapter(this ,getSupportFragmentManager(), isFavs));
        tabs.setupWithViewPager(viewPager);

        tabs.getTabAt(0).setIcon(R.drawable.users);
        tabs.getTabAt(1).setIcon(R.drawable.pages);
        tabs.getTabAt(2).setIcon(R.drawable.events);
        tabs.getTabAt(3).setIcon(R.drawable.places);
        tabs.getTabAt(4).setIcon(R.drawable.groups);


//        LinearLayout ll = (LinearLayout)findViewById(R.id.btnLay);
//        LinearLayout.LayoutParams lp = new     LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        final Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setText("Previous");
        btn1.setEnabled(false);



        final Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setText("Next");
        btn2.setEnabled(false);

        if(isFavs){
            ((ViewGroup)btn1.getParent()).removeView(btn1);
            ((ViewGroup)btn2.getParent()).removeView(btn2);
        }

        DataStorage dataStorage = (DataStorage) getApplication();
        dataStorage.setResultActivity(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
