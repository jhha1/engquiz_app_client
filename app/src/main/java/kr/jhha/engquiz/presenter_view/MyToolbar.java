package kr.jhha.engquiz.presenter_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-04-13.
 */
public class MyToolbar {
    private MainActivity mMainActivity;
    private android.support.v7.widget.Toolbar mToolbar;

    public static int OPTION_MENU_HELP = 1;
    public static int OPTION_MENU_SEND_REPORT = 0;

    private static MyToolbar ourInstance = new MyToolbar();
    public static MyToolbar getInstance() {
        return ourInstance;
    }
    private MyToolbar() {
    }

    // 툴바 초기화
    public void initialize(MainActivity activity, Toolbar toolbar) {
        mMainActivity = activity;
        mToolbar = toolbar;
    }

    public void show(){
        ActionBar actionBar = mMainActivity.getSupportActionBar();
        actionBar.show();
    }

    public void hide(){
        ActionBar actionBar = mMainActivity.getSupportActionBar();
        actionBar.hide();
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    public void updateToolBar(FragmentHandler.EFRAGMENT fragment) {
        updateToolbarTitle(fragment);
        setToolbarBackground(fragment);
    }

    private void updateToolbarTitle( FragmentHandler.EFRAGMENT fragment ){
        String title;
        int titleColor;
        switch (fragment)
        {
            case PLAYQUIZ:
                title = "";
                titleColor = R.color.gray;
                break;
            case SCRIPT_TAB:
                title = "Script";
                titleColor = R.color.holo_orange;
                break;
            case ADD_SCRIPT_FROM_OTHER_LOCATION:
                title = "Script";
                titleColor = R.color.holo_orange;
                break;
            case SENTENCES:
                title = "Sentences";
                titleColor = R.color.holo_orange;
                break;
            case ADD_SENTENCE:
                title = "Add Sentence";
                titleColor = R.color.holo_orange;
                break;
            case REPORT:
                title = "Report";
                titleColor = R.color.holo_orange;
                break;
            case INTRO:
            default:
                // toolbar title 안보인다.
                title = StringHelper.EMPTY_STRING;
                titleColor = R.color.PlayQuizDark;
                break;
        }

        // text
        updateToolbarTitle(title);

        // text color
        final Context context = mMainActivity.getApplicationContext();
        titleColor = ContextCompat.getColor(context, titleColor);
        mToolbar.setTitleTextColor(titleColor);
    }

    public void updateToolbarTitle( String title ){
        if (mMainActivity.getSupportActionBar() != null) {
            mMainActivity.getSupportActionBar().setTitle(title);
        }
    }

    public String getToolbarTitle()
    {
        if (mMainActivity.getSupportActionBar() != null)
        {
            return (String) mMainActivity.getSupportActionBar().getTitle();
        }

        return "ENGLISH QUIZ";
    }

    public void setToolbarBackground(FragmentHandler.EFRAGMENT fragment)
    {
        final Context context = mMainActivity.getApplicationContext();
        switch (fragment)
        {
            case INTRO:
            case PLAYQUIZ:
                int color = ContextCompat.getColor(context, R.color.PlayQuizDark);
                mToolbar.setBackgroundColor(color);
                mToolbar.setTitleMargin(0,0,0,0);
                break;
            default:
                /*
                Drawable drawable = ResourcesCompat.getDrawable(
                        mMainActivity.getResources(),
                        R.drawable.img_toolbar__background_yellow_solid,
                        null);
                mToolbar.setBackground(drawable);
                */
                //mToolbar.setTitleMarginTop(10);
                Drawable background = ContextCompat.getDrawable(context, R.drawable.img_toolbar__background);
                mToolbar.setBackground(background);
                break;
        }
    }

   // private static MenuItem mToolbarMenu_QuizplayCount;
    public void updateToolBarOptionMenu(FragmentHandler.EFRAGMENT fragment, Menu menu) {
        MenuItem sendReport = menu.findItem(R.id.action_bar__send_report);
        MenuItem help = menu.findItem(R.id.action_bar__help_webview);
       // mToolbarMenu_QuizplayCount = menu.findItem(R.id.action_bar__play_count);

        switch (fragment) {
            case INTRO:
                sendReport.setVisible(false);
                help.setVisible(false);
               // mToolbarMenu_QuizplayCount.setVisible(false);
                break;
            case PLAYQUIZ:
                sendReport.setVisible(true);
                help.setVisible(true);
             //   mToolbarMenu_QuizplayCount.setVisible(true);
                break;
            default:
                sendReport.setVisible(false);
                help.setVisible(true);
              //  mToolbarMenu_QuizplayCount.setVisible(false);
                break;
        }
    }
}
