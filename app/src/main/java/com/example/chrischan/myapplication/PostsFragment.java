package com.example.chrischan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by chrischan on 4/19/17.
 */

public class PostsFragment extends Fragment {
    private static final String TYPE="TYPE";
    private ArrayList<HashMap<String, Object>> mylist = new ArrayList<>();
    private int mType;

    private String mId;
    private String mName;
    private String mPhoto;
    private SimpleAdapter mSchedule;


    public static PostsFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);

        PostsFragment newFragment = new PostsFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(TYPE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        Intent intent = getActivity().getIntent();
        final Bundle bundle = intent.getBundleExtra("data");

        mId = bundle.getString("id");
        mName = bundle.getString("name");
        mPhoto = bundle.getString("photo");

        if(bundle!= null){
            LoadData();

            if(mylist.size() == 0){
                view = inflater.inflate(R.layout.fragment_empty,container,false);

                TextView textView = (TextView) view.findViewById(R.id.text_empty);
                textView.setText("No posts available to display");
            }else{
                view = inflater.inflate(R.layout.fragment_posts,container,false);

                ListView listView = (ListView) view.findViewById(R.id.posts_listView);

                mSchedule = new MySimpleAdapter(getActivity(), mylist, R.layout.post_entity,
                        new String[]{"name", "photo", "date", "message"},
                        new int[]{R.id.name, R.id.photo, R.id.date, R.id.message});
                listView.setAdapter(mSchedule);

            }


        }

        return view;
    }

    private String DateForm(String s){
        String res;
        res = s.replace("T"," ");
        res = res.replace("+0000","");
        return res;
    }

    private void LoadData(){

        final DataStorage dataStorage = (DataStorage) getActivity().getApplication();
        mylist = new ArrayList<>();
        try{
            JSONObject posts = dataStorage.getPosts();

            if(posts == null || !posts.has("data"))
                return;

            for(int i=0;i< posts.getJSONArray("data").length();i++){
                JSONObject album = posts.getJSONArray("data").getJSONObject(i);

                HashMap<String,Object> map = new HashMap();

                map.put("name", mName);
                map.put("id", mId);
                map.put("photo", mPhoto);
                map.put("date", album.has("created_time")?DateForm(album.getString("created_time")):"");
                map.put("message", album.has("message")?album.getString("message"):"");

                mylist.add(map);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

}
