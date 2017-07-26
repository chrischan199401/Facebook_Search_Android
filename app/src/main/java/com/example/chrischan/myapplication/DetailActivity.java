package com.example.chrischan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private String mId;
    private String mName;
    private String mPhoto;
    private int mType;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("More Details");
        }

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs2);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);

        viewPager.setAdapter(new DetailAdapter(this, getSupportFragmentManager()));
        tabs.setupWithViewPager(viewPager);

        tabs.getTabAt(0).setIcon(R.drawable.albums);
        tabs.getTabAt(1).setIcon(R.drawable.posts);


        Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra("data");
        mId = bundle.getString("id");
        mName = bundle.getString("name");
        mPhoto = bundle.getString("photo");
        mType = bundle.getInt("type");




        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        LoginManager.getInstance().logOut();
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getBaseContext(), "Post Fail!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getBaseContext(), "Post Success!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DataStorage dataStorage = (DataStorage) getApplication();

        int id = item.getItemId();
        // handle arrow click here
        if (id == android.R.id.home) {
            this.finish();
            return true;// close this activity and return to preview activity (if there is any)
        }else if(id == R.id.Fav){
            HashMap<String, Object> map =new HashMap<>();
            map.put("id", mId);
            map.put("name", mName);
            map.put("photo", mPhoto);
            map.put("type", mType);

            if(dataStorage.addFavs(mType,mId, map)){
                Toast.makeText(getBaseContext(), "Add to Favorites!", Toast.LENGTH_SHORT).show();
                item.setTitle("Remove from Favorites");
            }
            else{
                Toast.makeText(getBaseContext(), "Remove from Favorites!", Toast.LENGTH_SHORT).show();
                item.setTitle("Add to Favorites");
            }

            Activity resultActivity = dataStorage.getResultActivity();
            ViewPager viewPager = (ViewPager) resultActivity.findViewById(R.id.viewpager);
            viewPager.getAdapter().notifyDataSetChanged();

            return true;
        }else if(id == R.id.shareFB){
            Toast.makeText(getBaseContext(), "Sharing Facebook!", Toast.LENGTH_LONG).show();
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentTitle(mName)
                    .setContentDescription("FB SEARCH FROM USC CSCI571")
                    .setImageUrl(Uri.parse(mPhoto))
                    .setRef("571 Spring 2017")
                    .build();

            shareDialog.show(content);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem favItem = menu.findItem(R.id.Fav);

        final DataStorage dataStorage = (DataStorage) getApplication();
        if(dataStorage.isOnFavs(mId))
            favItem.setTitle("Remove from Favorites");
        else
            favItem.setTitle("Add to Favorites");
        return true;
    }

}
