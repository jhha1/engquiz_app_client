package kr.jhha.engquiz.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.Const;
import kr.jhha.engquiz.view.fragments.AddScriptFragment;
import kr.jhha.engquiz.view.fragments.PlayQuizFragment;
import kr.jhha.engquiz.view.fragments.playlist.AddList;
import kr.jhha.engquiz.view.fragments.playlist.DelList;
import kr.jhha.engquiz.view.fragments.playlist.PlayList;
import kr.jhha.engquiz.view.fragments.playlist.PlayListAdapter;
import kr.jhha.engquiz.view.fragments.playlist.PlayListDetail;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , PlayList.OnPlayListButtonClickListener {

    private Toolbar mToolbar;
    private DrawerLayout mNavDrawer;

    private Fragment mPlayQuizFragment;
    private Fragment mAddScriptFragment;

    private Fragment mPlayListFragment;
    private Fragment mPlayListDetailFragment;
    private Fragment mMakeCustomQuizFragment;
    private Fragment mDelPlayListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayQuizFragment = new PlayQuizFragment();
        mPlayListFragment = new PlayList();
        mPlayListDetailFragment = new PlayListDetail();
        mMakeCustomQuizFragment = new AddList();
        mDelPlayListFragment = new DelList();
        mAddScriptFragment = new AddScriptFragment();

        // 툴바
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.LTGRAY); // 툴바 글자색
        setSupportActionBar(mToolbar);

        // 슬라이딩 네비게이션 드로어
        mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 첫 화면 셋팅
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, mPlayQuizFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // 파일에 있는 커스텀리스트를 읽어와 list에 저장.
        // Adapter에서는 getActivity()가 안되서,, 여기서 초기화.
        initMyQuizList();
    }

    private void initMyQuizList() {
        // 초기에는 전체 데이터가 들어간 퀴즈셋 하나.
        // 지금은 가라 데이터...
        if(PlayListAdapter.getInstance().getCount() == 0) {
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
        } else {
            super.onBackPressed();
        }
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
    public boolean onNavigationItemSelected(MenuItem item)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play_quiz) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        } else if (id == R.id.nav_my_quizs) {
            transaction.replace(R.id.container, mPlayListFragment);
        } else if (id == R.id.nav_sync_script) {
            transaction.replace(R.id.container, mAddScriptFragment);
            //AddScriptDialog dialog = new AddScriptDialog(this);
            //dialog.show();
        }

        //transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlayListBtnClicked( View v )  {
        try {
            changeViewFragment(v);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void changeViewFragment( View v )
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch( v.getId() )
        {
            case R.id.playlist_set_for_play_btn:
                transaction.replace(R.id.container, mPlayQuizFragment);
                break;
        }
        transaction.commit();
    }

    public void changeViewFragment( Const.View view )
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if( Const.View.PLAYQUIZ == view ) {
            transaction.replace(R.id.container, mPlayQuizFragment);
        }
        else if( Const.View.NEW_CUSTOM_QUIZ == view ) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mMakeCustomQuizFragment);
        }
        else if( Const.View.DETAILQUIZ == view ) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.container, mPlayListDetailFragment);
        }

        transaction.commit();
    }

    public void startAppContent()
    {
        mToolbar.setVisibility(View.VISIBLE);
        mNavDrawer.setVisibility(View.VISIBLE);

        changeViewFragment( Const.View.PLAYQUIZ );
    }

    public void setActionBarTitle( String title ) {
        getSupportActionBar().setTitle( title );
    }
}
