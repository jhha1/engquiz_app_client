package kr.jhha.engquiz;

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

import kr.jhha.engquiz.quizfolder.AddQuizFolderFragment;
import kr.jhha.engquiz.quizfolder.ShowQuizFoldersFragment;
import kr.jhha.engquiz.addscript.AddScriptFragment;
import kr.jhha.engquiz.quizfolder.detail.AddQuizFolderDetailFragment;
import kr.jhha.engquiz.quizfolder.detail.ShowQuizFolderDetailFragment;
import kr.jhha.engquiz.quizplay.QuizPlayFragment;
import kr.jhha.engquiz.sync.SyncFragment;
import kr.jhha.engquiz.user.LoginFragment;
import kr.jhha.engquiz.user.SignInFragment;
import kr.jhha.engquiz.util.click.BackPressedCloseHandler;
import kr.jhha.engquiz.util.click.ClickDetector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mNavDrawer;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private ClickDetector mBackPressedCloseHandler;

    private QuizPlayFragment mPlayQuizFragment;

    private SyncFragment mSyncFragment;
    private Fragment mAddScriptFragment;

    private ShowQuizFoldersFragment mQuizFoldersFragment;
    private ShowQuizFolderDetailFragment mQuizFolderDetailFragment;
    private AddQuizFolderDetailFragment mAddQuizFolderDetailFragment;
    private Fragment mMakeCustomQuizFragment;

    private SignInFragment mSignInFragment;
    private LoginFragment mLoginFragment;

    public static enum EFRAGMENT {NONE, PLAYQUIZ, QUIZQROUP_NEW,
        QUIZFOLDER_DETAIL_SHOW, QUIZFOLDER_DETAIL_NEW, SYNC, ADD_SCRIPT, UPDATE, SIGNIN, LOGIN};

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
        mBackPressedCloseHandler = new BackPressedCloseHandler( this );
    }

    // 프래그먼트 초기화
    private void initFragments() {
        mPlayQuizFragment = new QuizPlayFragment();
        mQuizFoldersFragment = new ShowQuizFoldersFragment();
        mQuizFolderDetailFragment = new ShowQuizFolderDetailFragment();
        mAddQuizFolderDetailFragment = new AddQuizFolderDetailFragment();
        mMakeCustomQuizFragment = new AddQuizFolderFragment();
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        mBackPressedCloseHandler.onClick(0);
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
        int quizFolderId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (quizFolderId == R.quizFolderId.action_settings) {
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
            transaction.replace(R.id.container, mQuizFoldersFragment);
        } else if (id == R.id.nav_sync) {
            transaction.replace(R.id.container, mSyncFragment);
        }

        //transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Fragment getFragment( MainActivity.EFRAGMENT eFragment ){
        Fragment fragment = null;
        switch ( eFragment ){
            case PLAYQUIZ:
                fragment = mPlayQuizFragment; break;
            case QUIZQROUP_NEW:
                fragment = mMakeCustomQuizFragment; break;
            case QUIZFOLDER_DETAIL_SHOW:
                fragment = mQuizFolderDetailFragment; break;
            case QUIZFOLDER_DETAIL_NEW:
                fragment = mAddQuizFolderDetailFragment; break;
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

    public void changeViewFragment(EFRAGMENT fragment) {
        Log.d("$$$$$$$$$$$$$$$$$","changeViewFragment called. fragment("+fragment+")");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (EFRAGMENT.PLAYQUIZ == fragment) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (EFRAGMENT.QUIZQROUP_NEW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mMakeCustomQuizFragment);
        } else if (EFRAGMENT.QUIZFOLDER_DETAIL_SHOW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mQuizFolderDetailFragment);
        } else if (EFRAGMENT.QUIZFOLDER_DETAIL_NEW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mAddQuizFolderDetailFragment);
        } else if (EFRAGMENT.SIGNIN == fragment) {
            transaction.replace(R.id.container, mSignInFragment);
        } else if (EFRAGMENT.LOGIN == fragment) {
            transaction.replace(R.id.container, mLoginFragment);
        }

        transaction.commit();
    }
}

