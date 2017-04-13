package kr.jhha.engquiz.presenter_view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.addscript.ParseScriptFragment;
import kr.jhha.engquiz.presenter_view.intro.IntroFragment;
import kr.jhha.engquiz.presenter_view.addsentence.AddSentenceFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.AddQuizFolderFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.AddScriptIntoFolderFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.FolderScriptsFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences.SentenceFragment;
import kr.jhha.engquiz.presenter_view.playquiz.QuizPlayFragment;
import kr.jhha.engquiz.presenter_view.admin.report.ReportFragment;
import kr.jhha.engquiz.presenter_view.sync.SyncFragment;
import kr.jhha.engquiz.util.detact_click.BackPressedCloseHandler;
import kr.jhha.engquiz.util.detact_click.ClickDetector;

public class MainActivity extends AppCompatActivity {

    private MyToolbar mToolbar;
    private DrawerLayout mNavigationViewLayout;
    private ActionBarDrawerToggle mNavigationViewToggle;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private ClickDetector mBackPressedCloseHandler;

    private IntroFragment mIntroFragment;
    private QuizPlayFragment mPlayQuizFragment;
    private SyncFragment mSyncFragment;
    private ParseScriptFragment mParseScriptFragment;
    private AddSentenceFragment mAddSentenceFragment;
    private QuizFoldersFragment mQuizFoldersFragment;
    private FolderScriptsFragment mQuizFolderScriptListFragment;
    private AddScriptIntoFolderFragment mQuizFolderAddScriptFragment;
    private Fragment mAddQuizFolderFragment;
    private ReportFragment mReportFragment;
    private SentenceFragment mShowSentenceFragment;

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

    // We start the transaction with delay to avoid junk while closing the drawer
    private static final int sDELAY_MILLIS = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화.
        setUpToolbar();
        setUpNavigationView();
        initCloseAppHandler();
        setUpFragments();
        //restoreState(savedInstanceState);
        showDefaultFragment();
    }

    /*
        App Close
     */
    // 앱 종료 핸들러 초기화
    private void initCloseAppHandler() {
        mBackPressedCloseHandler = new BackPressedCloseHandler( this );
    }

    // 뒤로가기 버튼: 두번 누르면 앱 종료
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        mBackPressedCloseHandler.onClick(0);
        super.onBackPressed();
    }

    // 강제 종료
    public void finishApp(){
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /*
        Action Bar
     */
    // 툴바 초기화
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbar = MyToolbar.getInstance();
        mToolbar.setUpToolbar(this, toolbar);
    }

    /*
        Navigation Drawer View
     */
    // 슬라이딩 네이게이션 드로어
    private void setUpNavigationView() {
        final Toolbar toolbar = mToolbar.getToolbar();
        mNavigationViewLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationViewToggle = new ActionBarDrawerToggle(
                this, mNavigationViewLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavigationViewLayout.addDrawerListener(mNavigationViewToggle);
        mNavigationViewToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener
        (
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        // Handle navigation view item clicks here.
                        int id = item.getItemId();

                        if (id == R.id.nav_play_quiz) {
                            transaction.replace(R.id.container, mPlayQuizFragment);
                        } else if(id == R.id.nav_add_script){
                            transaction.replace(R.id.container, mParseScriptFragment);
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
                }
        );

        // report 메뉴는 관리자용이므로, 기본적으로 안보이게 한다.
        // 이후, LogIn 결과로 관리자면, 이 메뉴를 다시 보이게 한다.
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_report).setVisible(false);
    }

    // ActionBar 위의 Hamburger Icon.. 변경
    public void changeDrawerHamburgerIcon(String type){
        if( type.equals("alarm") ){
            // 알람이 왔음을 아이콘 표시
            mNavigationViewToggle.setDrawerIndicatorEnabled(false);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_content__new_red, getTheme());
            mNavigationViewToggle.setHomeAsUpIndicator(drawable);
            mNavigationViewToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNavigationViewLayout.isDrawerVisible(GravityCompat.START)) {
                        mNavigationViewLayout.closeDrawer(GravityCompat.START);
                    } else {
                        mNavigationViewLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

        } else {
            // default
            mNavigationViewToggle.setDrawerIndicatorEnabled(true);
        }
    }

    public void changeDrawerMenuIcon(int menuId, int iconID){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(menuId);
        menuItem.setIcon(iconID);
    }

    public void showAdminMenu(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_report).setVisible(true);
    }

    /*
        Fragments
     */
    // 프래그먼트 초기화
    private void setUpFragments() {
        mIntroFragment = new IntroFragment();
        mPlayQuizFragment = new QuizPlayFragment();
        mQuizFoldersFragment = new QuizFoldersFragment();
        mQuizFolderScriptListFragment = new FolderScriptsFragment();
        mQuizFolderAddScriptFragment = new AddScriptIntoFolderFragment();
        mAddQuizFolderFragment = new AddQuizFolderFragment();
        mParseScriptFragment = new ParseScriptFragment();
        mAddSentenceFragment = new AddSentenceFragment();
        mSyncFragment = new SyncFragment();
        mShowSentenceFragment = new SentenceFragment();
        mReportFragment = new ReportFragment();
    }

    private void restoreState(final @Nullable Bundle savedInstanceState)
    {
        // This allow us to know if the activity was recreated
        // after orientation change and restore the kr.jhha.engquiz.presenter_view.MyToolbar title
        if (savedInstanceState == null)
        {
            showDefaultFragment();
        }
        else
        {
            mToolbar.setToolBarTitle((String) savedInstanceState.getCharSequence("actionBarTitle"));
        }
    }

     // 첫 화면 셋팅
    private void showDefaultFragment() {
        // We start the transaction with delay to avoid junk while closing the drawer
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.container, mIntroFragment);
                transaction.commit();
            }
        }, sDELAY_MILLIS);

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
                fragment = mParseScriptFragment; break;

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

    @Override
    protected void onSaveInstanceState(final Bundle outState)
    {
        outState.putCharSequence("actionBarTitle", mToolbar.getToolbarTitle());
        super.onSaveInstanceState(outState);
    }
}

