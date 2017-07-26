package com.example.chrischan.myapplication;

/**
 * Created by chrischan on 4/14/17.
 */

import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;


public class DataStorage extends Application{
    private static Context context;
    private JSONObject users;
    private JSONObject pages;
    private JSONObject events;
    private JSONObject places;
    private JSONObject groups;

    private JSONObject posts;
    private JSONObject albums;

    private HashMap<String, HashMap<String, Object>>[] favs = new LinkedHashMap[5];
    private HashSet<String> favsSet;

    private Activity resultActivity;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DataStorage.context = context;
    }

    public JSONObject getUsers() {
        return users;
    }

    public void setUsers(JSONObject users) {
        this.users = users;
    }

    public JSONObject getPages() {
        return pages;
    }

    public void setPages(JSONObject pages) {
        this.pages = pages;
    }

    public JSONObject getEvents() {
        return events;
    }

    public void setEvents(JSONObject events) {
        this.events = events;
    }

    public JSONObject getPlaces() {
        return places;
    }

    public void setPlaces(JSONObject places) {
        this.places = places;
    }

    public JSONObject getGroups() {
        return groups;
    }

    public void setGroups(JSONObject groups) {
        this.groups = groups;
    }

    public JSONObject getPosts() {
        return posts;
    }

    public void setPosts(JSONObject posts) {
        this.posts = posts;
    }

    public JSONObject getAlbums() {
        return albums;
    }

    public void setAlbums(JSONObject albums) {
        this.albums = albums;
    }

    public Activity getResultActivity() {
        return resultActivity;
    }

    public void setResultActivity(Activity resultActivity) {
        this.resultActivity = resultActivity;
    }

    public boolean addFavs(int type, String id, HashMap<String, Object> map){
        HashMap<String, HashMap<String, Object>> fav = favs[type];
        if(fav.containsKey(id)) {
            fav.remove(id);
            favsSet.remove(id);
            return false;
        }else{
            fav.put(id, map);
            favsSet.add(id);
            return true;
        }
    }

    public HashMap<String, HashMap<String, Object>> getFavs(int type){
        return favs[type];
    }

    public boolean isOnFavs(String id){
        return favsSet.contains(id);
    }

    @Override
    public void onCreate() {

        users = null;
        pages = null;
        events = null;
        places = null;
        groups = null;
        posts = null;
        albums = null;

        for(int i =0;i< favs.length;i++){
            favs[i] = new LinkedHashMap<>();
        }
        favsSet = new HashSet<>();

        super.onCreate();
        context = getApplicationContext();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app


        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

}
