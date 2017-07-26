package com.example.chrischan.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

/**
 * Created by chrischan on 4/18/17.
 */

public class AlbumsFragment extends Fragment {

    private static final String TYPE="TYPE";
    private int mId;
    private int mType;

    private List<String> GroupData;//定义组数据
    private List<List<String>> ChildrenData;//定义组中的子数据

    public static AlbumsFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);

        AlbumsFragment newFragment = new AlbumsFragment();
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


        if(bundle != null){

            LoadData();

            if(GroupData.size() ==0){
                view = inflater.inflate(R.layout.fragment_empty,container,false);

                TextView textView = (TextView) view.findViewById(R.id.text_empty);
                textView.setText("No albums available to display");


            }else{
                view = inflater.inflate(R.layout.fragment_albums,container,false);
                ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.tab_listview2);

                listView.setAdapter(new ExpandableAdapter());

                listView.setOnGroupClickListener(new mGroupListener());
            }


        }

        return view;
    }

    private void LoadData(){

        final DataStorage dataStorage = (DataStorage) getActivity().getApplication();
        try{
            JSONObject albums = dataStorage.getAlbums();

            GroupData = new ArrayList<>();
            ChildrenData = new ArrayList<>();

            if(albums == null || !albums.has("data"))
                return;

            for(int i=0;i< albums.getJSONArray("data").length();i++){
                JSONObject album = albums.getJSONArray("data").getJSONObject(i);
                GroupData.add(album.getString("name"));

                List<String> imgUrls = new ArrayList<>();
                if(album.has("photos")&&album.getJSONObject("photos").has("data")){
                    JSONArray imgs = album.getJSONObject("photos").getJSONArray("data");
                    for(int j=0;j< imgs.length();j++){
                        imgUrls.add(imgs.getJSONObject(j).getString("id"));
                    }
                }
                ChildrenData.add(imgUrls);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    private class mGroupListener implements ExpandableListView.OnGroupClickListener{
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            int count = parent.getAdapter().getCount();
            for(int i =0;i< count;i++){
                if(i != groupPosition)
                    parent.collapseGroup(i);
            }
            if(parent.isGroupExpanded(groupPosition))
                parent.collapseGroup(groupPosition);
            else
                parent.expandGroup(groupPosition);

            return true;
        }
    }

    private class ExpandableAdapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if(ChildrenData.get(groupPosition).size() ==0)
                return "";
            else
                return ChildrenData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ImageView myView =createImageView(ChildrenData.get(groupPosition).get(childPosition));


            return myView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return ChildrenData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return GroupData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return GroupData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView myText = null;
            if (convertView != null) {
                myText = (TextView)convertView;
                myText.setText(GroupData.get(groupPosition));
            } else {
                myText = createView(GroupData.get(groupPosition));
            }
            return myText;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private TextView createView(String content){
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 150);

            TextView myText = new TextView(getActivity());
            myText.setLayoutParams(layoutParams);
            myText.setBackgroundColor(getResources().getColor(R.color.colorExpandableItem));
            myText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            myText.setTextColor(getResources().getColor(R.color.black));
            myText.setTypeface(null, Typeface.BOLD);
            myText.setTextSize(20);
            myText.setPadding(80, 0, 0, 0);
            myText.setText(content);

            return myText;
        }
        private ImageView createImageView(String content) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ImageView myView = new ImageView(getActivity());

            myView.setLayoutParams(layoutParams);
            String imageUrl = "https://graph.facebook.com/v2.8/"+ content+"/picture?access_token=EAACZCupGCWd8BAIVBywZCgteM0mZA3pwdTWeDcF9xTtLsbufxYznTCOZAwYWDhn4HKX1PRsrYUolsVGwj5WJZARouxZCBcWff18ZBq8DwmyzxQkbKhvEjed431BC4e2pPPOAWbGVoO2VU8PfJy87lmYhRZCNZByuL4RcZD";
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imageUrl, myView);

            return myView;
        }
    }
}
