package kr.jhha.engquiz.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.backend_logic.FileManager;
import kr.jhha.engquiz.backend_logic.Initailizer;
import kr.jhha.engquiz.ui.fragments.AddScriptFragment;
import kr.jhha.engquiz.ui.fragments.PlayQuizFragment;
import kr.jhha.engquiz.ui.fragments.SyncFragment;
import kr.jhha.engquiz.ui.fragments.UpdateFragment;
import kr.jhha.engquiz.ui.fragments.playlist.AddList;
import kr.jhha.engquiz.ui.fragments.playlist.DelList;
import kr.jhha.engquiz.ui.fragments.playlist.PlayList;
import kr.jhha.engquiz.ui.fragments.playlist.PlayListAdapter;
import kr.jhha.engquiz.ui.fragments.playlist.PlayListDetail;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , PlayList.OnPlayListButtonClickListener {

    private Toolbar mToolbar;
    private DrawerLayout mNavDrawer;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private BackPressCloseHandler mBackPressCloseHandler;

    private Fragment mPlayQuizFragment;

    private Fragment mSyncFragment;
    private Fragment mAddScriptFragment;
    private Fragment mUpdateFragment;

    private Fragment mPlayListFragment;
    private Fragment mPlayListDetailFragment;
    private Fragment mMakeCustomQuizFragment;
    private Fragment mDelPlayListFragment;

    public static enum FRAGMENT {NONE, PLAYQUIZ, NEW_CUSTOM_QUIZ, QUIZ_DETAIL_LIST, SYNC, UPLOAD, UPDATE}

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화.
        initToolbar();
        initNavigationDrawer();
        initCloseAppHandler();
        initFragments();
        initFirstView(); // 첫 화면. initFragments() 다음에 와야함!!

        // 파일에 있는 커스텀리스트를 읽어와 list에 저장.
        // Adapter에서는 getActivity()가 안되서,, 여기서 초기화.
        initMyQuizList();

        //SharedPreferences preferences = getSharedPreferences("myapp_properties", MODE_PRIVATE);
        //Initailizer.getInstance().initBackend(preferences);

        new InitailizeAsync().execute();
    }

    // 툴바 초기화
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.LTGRAY); // 툴바 글자색
        setSupportActionBar(mToolbar);
    }

    // 슬라이딩 네이게이션 드로어
    private void initNavigationDrawer() {
        mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 앱 종료 핸들러
    private void initCloseAppHandler() {
        mBackPressCloseHandler = new BackPressCloseHandler(this);
    }

    // 프래그먼트 초기화
    private void initFragments() {
        mPlayQuizFragment = new PlayQuizFragment();
        mPlayListFragment = new PlayList();
        mPlayListDetailFragment = new PlayListDetail();
        mMakeCustomQuizFragment = new AddList();
        mDelPlayListFragment = new DelList();
        mAddScriptFragment = new AddScriptFragment();
        mSyncFragment = new SyncFragment();
        mUpdateFragment = new UpdateFragment();
    }

    // 첫 화면 셋팅
    private void initFirstView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.container, mPlayQuizFragment);
        transaction.commit();
    }

    // 내 퀴즈 카테고리의 내 퀴즈 리스트 초기화
    private void initMyQuizList() {
        if (PlayListAdapter.getInstance().getCount() == 0) {
            Drawable img = ContextCompat.getDrawable(this, R.drawable.ic_format_align_left_grey600_48dp);
            PlayListAdapter.getInstance().addItem(img, "New..", "원하는 스크립트를 선택해, 나만의 퀴즈를 만듭니다.");
            PlayListAdapter.getInstance().addItem(img, "Default Quiz", "스크립트 전체가 들어있습니다.");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        mBackPressCloseHandler.onBackPressed();
        super.onBackPressed();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play_quiz) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (id == R.id.nav_my_quizs) {
            transaction.replace(R.id.container, mPlayListFragment);
        } else if (id == R.id.nav_sync) {
            transaction.replace(R.id.container, mSyncFragment);
        }

        //transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlayListBtnClicked(View v) {
        try {
            changeViewFragment(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeViewFragment(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.playlist_set_for_play_btn:
                transaction.replace(R.id.container, mPlayQuizFragment);
                break;
        }
        transaction.commit();
    }

    public void changeViewFragment(FRAGMENT fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (FRAGMENT.PLAYQUIZ == fragment) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (FRAGMENT.NEW_CUSTOM_QUIZ == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mMakeCustomQuizFragment);
        } else if (FRAGMENT.QUIZ_DETAIL_LIST == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mPlayListDetailFragment);
        }

        transaction.commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    class InitailizeAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... unused) {
            SharedPreferences preferences = getSharedPreferences("myapp_properties", MODE_PRIVATE);
            Initailizer.getInstance().initBackend(preferences);
            return null;
        }

        protected void onPostExecute(final String... unused) {
            ;
        }
    }
}

class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            SystemExit();
        }
    }
    public void SystemExit() {
        activity.moveTaskToBack(true);
        activity.finish();
        toast.cancel();
        android.os.Process.killProcess(android.os.Process.myPid() );
        System.exit(0);
    }
    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}

