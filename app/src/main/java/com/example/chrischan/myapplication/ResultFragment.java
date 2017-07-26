package com.example.chrischan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


/**
 * Created by chrischan on 4/16/17.
 */

public class ResultFragment extends Fragment {

    private SimpleAdapter mSchedule;

    private static final String TYPE = "ARG_TIMELINE_TYPE";
    private static final String FAVS = "FAVS";
    private String[] typeString = new String[]{"user", "page", "event", "place", "group"};
    private int mType;
    private boolean isFavs;

    private ArrayList<HashMap<String, Object>> mylist = new ArrayList<>();


    public static ResultFragment newInstance(int type, boolean isFavs) {

        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putBoolean(FAVS, isFavs);
        ResultFragment newFragment = new ResultFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = getArguments().getInt(TYPE);
        isFavs = getArguments().getBoolean(FAVS);
    }

    protected int getLayoutResId() {
        return R.layout.fragment_result;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result,container,false);

        Bundle bundle = this.getArguments();

        if(bundle != null){

            ListView listView = (ListView) view.findViewById(R.id.tab_listview);

            if(!isFavs)
                updateData();
            else
                updateFavsData();

            mSchedule = new MySimpleAdapter(getActivity(), mylist, R.layout.activity_entity,
                    new String[]{"photo", "name", "isFav"},
                    new int[]{R.id.photo, R.id.name, R.id.fav}
            );
            listView.setAdapter(mSchedule);
            listView.setOnItemClickListener(new MyListener());
        }
        return view;
    }

    private void updateFavsData(){
        final DataStorage dataStorage = (DataStorage) getActivity().getApplication();

//        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
//        int page= tabs.getSelectedTabPosition();

        HashMap<String, HashMap<String, Object>> map = dataStorage.getFavs(mType);

        mylist = new ArrayList<>();
        for(HashMap<String, Object> values: map.values()){
            String id =(String) values.get("id");
            values.put("isFav", dataStorage.isOnFavs(id));
            mylist.add(values);
        }

    }

    private void updateData(){
        final DataStorage dataStorage = (DataStorage) getActivity().getApplication();

        JSONObject json = null;
        if(mType ==0){
            json = dataStorage.getUsers();
        }else if(mType ==1)
            json = dataStorage.getPages();
        else if(mType ==2)
            json = dataStorage.getEvents();
        else if(mType ==3)
            json = dataStorage.getPlaces();
        else
            json = dataStorage.getGroups();

        Activity activity = getActivity();
        Button btn1 = (Button) activity.findViewById(R.id.btn1);
        Button btn2 = (Button) activity.findViewById(R.id.btn2);

        try {

            TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
            int page= tabs.getSelectedTabPosition();
            JSONObject pageJSON;
            if(page ==0){
                pageJSON = dataStorage.getUsers();
            }else if(page ==1)
                pageJSON = dataStorage.getPages();
            else if(page ==2)
                pageJSON = dataStorage.getEvents();
            else if(page ==3)
                pageJSON = dataStorage.getPlaces();
            else
                pageJSON = dataStorage.getGroups();

            final JSONObject paging = pageJSON.has("paging") ? pageJSON.getJSONObject("paging") : null;

            if(paging != null){
                btn1.setEnabled(paging.has("previous"));
                btn2.setEnabled(paging.has("next"));
            }

            btn1.setOnClickListener(new View.OnClickListener()  {
                @Override
                public void onClick(View v) {

                    try{
                        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
                        int page= tabs.getSelectedTabPosition();
                        JSONObject pageJSON;
                        if(page ==0){
                            pageJSON = dataStorage.getUsers();
                        }else if(page ==1)
                            pageJSON = dataStorage.getPages();
                        else if(page ==2)
                            pageJSON = dataStorage.getEvents();
                        else if(page ==3)
                            pageJSON = dataStorage.getPlaces();
                        else
                            pageJSON = dataStorage.getGroups();

                        final JSONObject paging = pageJSON.has("paging") ? pageJSON.getJSONObject("paging") : null;

                        if(paging.has("previous")){
                            String url = paging.getString("previous");
                            new PagingAsyncTask().execute(url);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            btn2.setOnClickListener(new View.OnClickListener()  {
                @Override
                public void onClick(View v) {
                    try{
                        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
                        int page= tabs.getSelectedTabPosition();
                        JSONObject pageJSON;
                        if(page ==0){
                            pageJSON = dataStorage.getUsers();
                        }else if(page ==1)
                            pageJSON = dataStorage.getPages();
                        else if(page ==2)
                            pageJSON = dataStorage.getEvents();
                        else if(page ==3)
                            pageJSON = dataStorage.getPlaces();
                        else
                            pageJSON = dataStorage.getGroups();

                        final JSONObject paging = pageJSON.has("paging") ? pageJSON.getJSONObject("paging") : null;
                        if(paging.has("next")){
                            String url = paging.getString("next");
                            new PagingAsyncTask().execute(url);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }

        mylist = new ArrayList<>();

        try{
            for(int i=0;i< json.getJSONArray("data").length();i++){
                HashMap<String, Object> map= new HashMap<>();

                JSONObject record = json.getJSONArray("data").getJSONObject(i);

                map.put("id", record.getString("id"));
                map.put("photo", record.getJSONObject("picture").getJSONObject("data").getString("url"));
                map.put("name", record.getString("name"));
                map.put("isFav", dataStorage.isOnFavs(record.getString("id")));
                map.put("type", typeString[mType]);

                mylist.add(map);
            }


        }catch (JSONException e) {
            e.printStackTrace();
        }
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




    private class MyListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> mMap = (HashMap<String, String>)mSchedule.getItem(position);

            String fbId = mMap.get("id");
            String type = mMap.get("type");
            String url;
            if(type.equals("event"))
                url = "http://default-environment.n4y2pxmrhz.us-west-2.elasticbeanstalk.com/index.php?id="+fbId+"&type=event";
            else
                url = "http://default-environment.n4y2pxmrhz.us-west-2.elasticbeanstalk.com/index.php?id="+fbId;

            new HttpAsyncTask().execute(url);

        }
    }

    class PagingAsyncTask extends AsyncTask<String, Void, String>{
        LayoutInflater inflater;
        ViewGroup container;
        Bundle savedInstanceState;
//        PagingAsyncTask(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//            this.inflater =inflater;
//            this.container = container;
//            this.savedInstanceState = savedInstanceState;
//        }
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

                final DataStorage data = (DataStorage) getActivity().getApplication();
                TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
                int page= tabs.getSelectedTabPosition();
                if(page == 0)
                    data.setUsers(json);
                else if(page ==1)
                    data.setPages(json);
                else if(page ==2)
                    data.setEvents(json);
                else if(page == 3)
                    data.setPlaces(json);
                else
                    data.setGroups(json);

//                updataData();
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
                viewPager.getAdapter().notifyDataSetChanged();

                tabs.getTabAt(0).setIcon(R.drawable.users);
                tabs.getTabAt(1).setIcon(R.drawable.pages);
                tabs.getTabAt(2).setIcon(R.drawable.events);
                tabs.getTabAt(3).setIcon(R.drawable.places);
                tabs.getTabAt(4).setIcon(R.drawable.groups);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }

    class HttpAsyncTask extends AsyncTask<String, Void, String> {

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
                final DataStorage data = (DataStorage) getActivity().getApplication();


                data.setAlbums(json.has("albums")? json.getJSONObject("albums"):null);

                data.setPosts(json.has("posts")? json.getJSONObject("posts"):null);


                Bundle bundle = new Bundle();
                bundle.putString("id", json.getString("id"));
                bundle.putString("name", json.getString("name"));
                bundle.putString("photo", json.getJSONObject("picture").getJSONObject("data").getString("url"));
                bundle.putInt("type", mType);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        
    }



}

