package kr.jhha.engquiz.presenter_view;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.ui.click_detector.BackPressedCloseHandler;
import kr.jhha.engquiz.util.ui.click_detector.ClickDetector;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.INTRO;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.NONE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

public class MainActivity extends AppCompatActivity {

    private MyToolbar mMyToolbar;
    private MyNavigationView mMyNavigationDrawer;
    private FragmentHandler mFragmentHandler;

    // 앱 종료 핸들러 (백 버튼 2번 누르면 종료)
    private ClickDetector mBackPressedCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        // 초기화 순서를 꼭 지켜야 한다.
        setUpToolbar();
        setUpNavigationView();
        initCloseAppHandler();
        setUpFragments();
        //restoreState(savedInstanceState);
        showDefaultFragment();
    }

    /*
        Action Bar (ToolBar)
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMyToolbar = MyToolbar.getInstance();
        mMyToolbar.setUpToolbar(this, toolbar);
    }

    /*
        Navigation Drawer View
     */
    private void setUpNavigationView(){
        mMyNavigationDrawer = MyNavigationView.getInstance();
        mMyNavigationDrawer.setUpNavigationView(this, mMyToolbar.getToolbar());
    }

    /*
        Fragments
     */
    private void setUpFragments() {
        mFragmentHandler = FragmentHandler.getInstance();
        mFragmentHandler.setUpFragments(this);
    }

     /*
        첫 화면 셋팅
     */
    private void showDefaultFragment() {
        mFragmentHandler.changeViewFragment(INTRO);

        /*
        We start the transaction with delay to avoid junk while closing the drawer
         : 어차피 intro 에서 서버통신 등 시간이 걸려서, drawer ui 준비위해 delay 필요없음.
         : 오히려 delay 때문에, intro 뜨기 전에 잠깐 다른 프레그먼트 화면이 보여서 이상함.
        int sDELAY_MILLIS = 250;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mFragmentHandler.changeViewFragment(INTRO);
            }
        }, sDELAY_MILLIS);
        */
    }

    /*
       앱 종료 핸들러 셋팅
    */
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

        WebViewFragment webView = (WebViewFragment)mFragmentHandler.getFragment(WEB_VIEW);
        if( webView.canGoBack() ){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // 강제 종료
    public void finishApp(){
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /*
        onSaveInstanceState(), restoreState()
        종종 앱 시작때 onSaveInstanceState 에러뜨고 앱 시작안되는것 방지위해 넣은 코드
        근데, 좀 줄긴했지만 완전히 에러가 없어지진 않음..
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState)
    {
        outState.putCharSequence("actionBarTitle", mMyToolbar.getToolbarTitle());
        super.onSaveInstanceState(outState);
    }

    private void restoreState(final Bundle savedInstanceState)
    {
        // This allow us to know if the activity was recreated
        // after orientation change and restore the kr.jhha.engquiz.presenter_view.MyToolbar title
        if (savedInstanceState == null) {
            showDefaultFragment();
        }
        else {
            mMyToolbar.setToolBar(NONE);
        }
    }
}

