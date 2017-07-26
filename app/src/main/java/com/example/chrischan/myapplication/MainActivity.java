package com.example.chrischan.myapplication;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ASYNC_TASK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final EditText keywordText =  (EditText) findViewById(R.id.editText);

        final Button clear = (Button) findViewById(R.id.clear_btn);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keywordText.setText("");
            }
        });
        final Button submit = (Button) findViewById(R.id.search_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String keyword = keywordText.getText().toString();
                if(keyword.equals("")){
                    Toast.makeText(getBaseContext(), "No Empty Keyword!", Toast.LENGTH_LONG).show();
                    return;
                }

                String url = "http://default-environment.n4y2pxmrhz.us-west-2.elasticbeanstalk.com/index.php?keyword="+keyword;
                new HttpAsyncTask().execute(url);


            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(getBaseContext(), ResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("FAVS", true);
            intent.putExtra("data", bundle);
            startActivity(intent);

        } else if (id == R.id.nav_me) {

            Intent intent = new Intent(getBaseContext(), AboutMeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class HttpAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {
            return getJSON(url[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject json = new JSONObject(s);
                final DataStorage data = (DataStorage) getApplication();
                data.setUsers(json.getJSONObject("user"));
                data.setPages(json.getJSONObject("page"));
                data.setEvents(json.getJSONObject("event"));
                data.setGroups(json.getJSONObject("group"));
                data.setPlaces(json.getJSONObject("place"));

            }catch (JSONException e){
                e.printStackTrace();
            }


            Intent intent = new Intent(getBaseContext(), ResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("FAVS", false);
            intent.putExtra("data", bundle);

            startActivity(intent);
        }


        private String getJSON(String url){
            StringBuilder sb = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL u = new URL(url);
                urlConnection = (HttpURLConnection) u.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String input = null;
                while ((input= reader.readLine()) != null){
                    sb.append(input);
                }

                urlConnection.disconnect();

            }catch (IOException e){
                System.out.println(e.toString());
            }

            return sb.toString();
        }
    }

}

