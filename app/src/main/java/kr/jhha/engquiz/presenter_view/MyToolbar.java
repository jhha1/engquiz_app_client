package kr.jhha.engquiz.presenter_view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.util.StringHelper;

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

        // 툴바 글자색.
        // 첫 화면이 게임플레이이므로, 겜플레이 화면용 글자색셋팅 (회색)
        final Context context = mMainActivity.getApplicationContext();
        int color = ContextCompat.getColor(context, R.color.PlayQuizLight);
        mToolbar.setTitleTextColor(color);

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

    public void show(){
        ActionBar actionBar = mMainActivity.getSupportActionBar();
        actionBar.show();
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    public void setToolBar(FragmentHandler.EFRAGMENT fragment) {
        setToolbarTitle(fragment);
        setToolbarBackground(fragment);
        MyNavigationView.getInstance().toggleHamburgerIcon(fragment);
    }

    private void setToolbarTitle(FragmentHandler.EFRAGMENT fragment ){
        String title;
        int titleColor;
        switch (fragment)
        {
            case INTRO:
                // intro 에는 toolbar가 안보인다.
                title = StringHelper.EMPTY_STRING;
                titleColor = R.color.PlayQuizDark;
                break;
            case PLAYQUIZ:
                title = "Quiz Playing";
                titleColor = R.color.PlayQuizLight;
                break;
            case SHOW_SCRIPTS:
                title = "Scripts";
                titleColor = R.color.black_alpha_80;
                break;
            case ADD_SCRIPT:
                title = "Add Script";
                titleColor = R.color.black_alpha_80;
                break;
            case ADD_SENTENCE:
                title = "Add Sentence";
                titleColor = R.color.black_alpha_80;
                break;
            case SHOW_QUIZFOLDERS:
                title = "Folders";
                titleColor = R.color.black_alpha_80;
                break;
            case SYNC:
                title = "Sync";
                titleColor = R.color.black_alpha_80;
                break;
            case REPORT:
                title = "Report";
                titleColor = R.color.black_alpha_80;
                break;
            case NEW_QUIZFOLDER:
                title = "New Folder";
                titleColor = R.color.black_alpha_80;
                break;
            case SHOW_SCRIPTS_IN_QUIZFOLDER:
                title = "Scripts";
                titleColor = R.color.black_alpha_80;
                break;
            case SHOW_SENTENCES_IN_SCRIPT:
                title = "Sentences";
                titleColor = R.color.black_alpha_80;
                break;
            case ADD_SCRIPT_INTO_QUIZFOLDER:
                title = "Add Script";
                titleColor = R.color.black_alpha_80;
                break;
            default:
                title = "ENGLISH QUIZ";
                titleColor = R.color.PlayQuizLight;
                break;
        }

        // text
        setToolbarTitle(title);

        // text color
        final Context context = mMainActivity.getApplicationContext();
        titleColor = ContextCompat.getColor(context, titleColor);
        mToolbar.setTitleTextColor(titleColor);
    }

    public void setToolbarTitle( String title ){
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
                break;
            default:
                /*
                Drawable drawable = ResourcesCompat.getDrawable(
                        mMainActivity.getResources(),
                        R.drawable.img_toolbar__background_yellow_solid,
                        null);
                mToolbar.setBackground(drawable);
                */
                color = ContextCompat.getColor(context, R.color.yellow100);
                mToolbar.setBackgroundColor(color);
                break;
        }
    }
}
