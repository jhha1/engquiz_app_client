package kr.jhha.engquiz;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.jhha.engquiz.quizgroup.AddQuizGroupFragment;
import kr.jhha.engquiz.quizgroup.ShowQuizGroupsFragment;
import kr.jhha.engquiz.addscript.AddScriptFragment;
import kr.jhha.engquiz.quizplay.QuizPlayFragment;
import kr.jhha.engquiz.sync.SyncFragment;
import kr.jhha.engquiz.quizgroup.detail.ShowQuizGroupDetail;
import kr.jhha.engquiz.user.LoginFragment;
import kr.jhha.engquiz.user.SignInFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , ShowQuizGroupsFragment.OnPlayListButtonClickListener {

    private Toolbar mToolbar;
    private DrawerLayout mNavDrawer;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private BackPressCloseHandler mBackPressCloseHandler;

    private QuizPlayFragment mPlayQuizFragment;

    private SyncFragment mSyncFragment;
    private Fragment mAddScriptFragment;
    private Fragment mUpdateFragment;

    private Fragment mPlayListFragment;
    private Fragment mPlayListDetailFragment;
    private Fragment mMakeCustomQuizFragment;
    private Fragment mDelPlayListFragment;

    private SignInFragment mSignInFragment;
    private LoginFragment mLoginFragment;

    public static enum EFRAGMENT {NONE, PLAYQUIZ, QUIZQROUP_NEW,
        QUIZGROUP_DETAIL_SHOW, SYNC, ADD_SCRIPT, UPDATE, SIGNIN, LOGIN};

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
        mPlayQuizFragment = new QuizPlayFragment();
        mPlayListFragment = new ShowQuizGroupsFragment();
        mPlayListDetailFragment = new ShowQuizGroupDetail();
        mMakeCustomQuizFragment = new AddQuizGroupFragment();
        mAddScriptFragment = new AddScriptFragment();
        mSyncFragment = new SyncFragment();

        mSignInFragment = new SignInFragment();
        mLoginFragment = new LoginFragment();
    }

    // 첫 화면 셋팅
    private void initFirstView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.container, mLoginFragment);
        transaction.commit();
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
        int quizGroupId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (quizGroupId == R.quizGroupId.action_settings) {
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

    public void changeViewFragment(EFRAGMENT fragment) {
        Log.d("$$$$$$$$$$$$$$$$$","changeViewFragment called. fragment("+fragment+")");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (EFRAGMENT.PLAYQUIZ == fragment) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (EFRAGMENT.QUIZQROUP_NEW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mMakeCustomQuizFragment);
        } else if (EFRAGMENT.QUIZGROUP_DETAIL_SHOW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mPlayListDetailFragment);
        } else if (EFRAGMENT.SIGNIN == fragment) {
            transaction.replace(R.id.container, mSignInFragment);
        } else if (EFRAGMENT.LOGIN == fragment) {
            transaction.replace(R.id.container, mLoginFragment);
        }

        transaction.commit();
    }

    public void callViewFragment( EFRAGMENT fragment, Object arg ) {
        switch ( fragment ){

        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


     public Fragment getFragment( MainActivity.EFRAGMENT eFragment ){
        Fragment fragment = null;
        switch ( eFragment ){
            case PLAYQUIZ:
                fragment = mPlayQuizFragment; break;
            case QUIZQROUP_NEW:
                fragment = mMakeCustomQuizFragment; break;
            case QUIZGROUP_DETAIL_SHOW:
                fragment = mPlayListDetailFragment; break;
            case SYNC:
                fragment = mSyncFragment; break;
            case ADD_SCRIPT:
                fragment = mAddScriptFragment; break;
            case SIGNIN:
                fragment = mSignInFragment; break;
            case LOGIN:
                fragment = mLoginFragment; break;
        }
        return fragment;
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

