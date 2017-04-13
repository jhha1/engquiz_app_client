package kr.jhha.engquiz.presenter_view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import kr.jhha.engquiz.R;

/**
 * Created by thyone on 2017-04-13.
 */
public class MyToolbar {
    private MainActivity mMainActivity;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView mToolbarTextview;

    private static MyToolbar ourInstance = new MyToolbar();
    public static MyToolbar getInstance() {
        return ourInstance;
    }
    private MyToolbar() {
    }

    // 툴바 초기화
    public void setUpToolbar(MainActivity activity, Toolbar toolbar) {
        mMainActivity = activity;
        mToolbar = toolbar;

        mToolbar.setTitleTextColor(Color.LTGRAY); // 툴바 글자색

        /*
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
         */
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    public void setToolBarTitle(final String title) {
        if (mMainActivity.getSupportActionBar() != null)
        {
            mMainActivity.getSupportActionBar().setTitle(title);
        }
    }

    public String getToolbarTitle()
    {
        if (mMainActivity.getSupportActionBar() != null)
        {
            return (String) mMainActivity.getSupportActionBar().getTitle();
        }

        return mMainActivity.getString(R.string.app_name);
    }

    public void switchBackground(String what){
        if( "gameplay".equals(what) ){
            mToolbar.setBackgroundColor(Color.parseColor("#424242"));
        } else {
            Drawable drawable = ResourcesCompat.getDrawable(mMainActivity.getResources(), R.drawable.image_toolbar__background, null);
            mToolbar.setBackground(drawable);
        }
    }
}
