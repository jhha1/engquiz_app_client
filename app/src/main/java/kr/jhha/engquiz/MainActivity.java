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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import kr.jhha.engquiz.Intro.IntroFragment;
import kr.jhha.engquiz.addsentence.AddSentenceFragment;
import kr.jhha.engquiz.quizfolder.AddQuizFolderFragment;
import kr.jhha.engquiz.quizfolder.ShowQuizFoldersFragment;
import kr.jhha.engquiz.addscript.AddScriptFragment;
import kr.jhha.engquiz.quizfolder.scripts.AddQuizFolderScriptFragment;
import kr.jhha.engquiz.quizfolder.scripts.ShowQuizFolderScriptsFragment;
import kr.jhha.engquiz.quizfolder.scripts.sentences.ShowSentenceFragment;
import kr.jhha.engquiz.quizplay.QuizPlayFragment;
import kr.jhha.engquiz.report.ReportFragment;
import kr.jhha.engquiz.sync.SyncFragment;
import kr.jhha.engquiz.util.click.BackPressedCloseHandler;
import kr.jhha.engquiz.util.click.ClickDetector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mNavDrawer;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private ClickDetector mBackPressedCloseHandler;

    private IntroFragment mIntroFragment;
    private QuizPlayFragment mPlayQuizFragment;
    private SyncFragment mSyncFragment;
    private AddScriptFragment mAddScriptFragment;
    private AddSentenceFragment mAddSentenceFragment;
    private ShowQuizFoldersFragment mQuizFoldersFragment;
    private ShowQuizFolderScriptsFragment mQuizFolderScriptListFragment;
    private AddQuizFolderScriptFragment mQuizFolderAddScriptFragment;
    private Fragment mAddQuizFolderFragment;
    private ReportFragment mReportFragment;

    private ShowSentenceFragment mShowSentenceFragment;

    public enum EFRAGMENT {
        NONE,
        PLAYQUIZ,
        ADD_SCRIPT,
        ADD_SENTENCE,
        QUIZFOLDER_NEW,
        QUIZFOLDER_SCRIPT_LIST_SHOW,
        QUIZFOLDER_SCRIPT_ADD,
        QUIZFOLDER_SENTENCE_LIST_SHOW,
        SYNC,
        Report
    };

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

        // TODO
        boolean bAdmin = true;
        if( ! bAdmin ) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_report).setVisible(false);
        }
    }

    // 앱 종료 핸들러
    private void initCloseAppHandler() {
        mBackPressedCloseHandler = new BackPressedCloseHandler( this );
    }

    // 프래그먼트 초기화
    private void initFragments() {
        mIntroFragment = new IntroFragment();
        mPlayQuizFragment = new QuizPlayFragment();
        mQuizFoldersFragment = new ShowQuizFoldersFragment();
        mQuizFolderScriptListFragment = new ShowQuizFolderScriptsFragment();
        mQuizFolderAddScriptFragment = new AddQuizFolderScriptFragment();
        mAddQuizFolderFragment = new AddQuizFolderFragment();
        mAddScriptFragment = new AddScriptFragment();
        mAddSentenceFragment = new AddSentenceFragment();
        mSyncFragment = new SyncFragment();

        mShowSentenceFragment = new ShowSentenceFragment();
        mReportFragment = new ReportFragment();
    }

    // 첫 화면 셋팅
    private void initFirstView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.container, mIntroFragment);
        transaction.commit();
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
        Action Bar
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play_quiz) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if(id == R.id.nav_add_script){
            transaction.replace(R.id.container, mAddScriptFragment);
        } else if(id == R.id.nav_add_sentence){
            transaction.replace(R.id.container, mAddSentenceFragment);
        } else if (id == R.id.nav_quiz_folders) {
            transaction.replace(R.id.container, mQuizFoldersFragment);
        } else if (id == R.id.nav_sync) {
            transaction.replace(R.id.container, mSyncFragment);
        } else if(id == R.id.nav_report){
            transaction.replace(R.id.container,mReportFragment);
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
            case QUIZFOLDER_NEW:
                fragment = mAddQuizFolderFragment; break;
            case QUIZFOLDER_SCRIPT_LIST_SHOW:
                fragment = mQuizFolderScriptListFragment; break;
            case QUIZFOLDER_SCRIPT_ADD:
                fragment = mQuizFolderAddScriptFragment; break;
            case QUIZFOLDER_SENTENCE_LIST_SHOW:
                fragment = mShowSentenceFragment; break;
            case SYNC:
                fragment = mSyncFragment; break;
            case ADD_SCRIPT:
                fragment = mAddScriptFragment; break;

        }
        return fragment;
    }

    public void changeViewFragment(EFRAGMENT fragment) {
        Log.d("$$$$$$$$$$$$$$$$$","changeViewFragment called. fragment("+fragment+")");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (EFRAGMENT.PLAYQUIZ == fragment) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (EFRAGMENT.QUIZFOLDER_NEW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mAddQuizFolderFragment);
        } else if (EFRAGMENT.QUIZFOLDER_SCRIPT_LIST_SHOW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mQuizFolderScriptListFragment);
        } else if (EFRAGMENT.QUIZFOLDER_SENTENCE_LIST_SHOW == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mShowSentenceFragment);
        } else if (EFRAGMENT.QUIZFOLDER_SCRIPT_ADD == fragment) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mQuizFolderAddScriptFragment);
        }

        transaction.commit();
    }

    public void finishApp(){
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

