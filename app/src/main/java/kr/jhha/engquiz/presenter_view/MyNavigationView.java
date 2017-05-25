package kr.jhha.engquiz.presenter_view;

import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.PLAYQUIZ;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.REPORT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_QUIZFOLDERS;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SCRIPT_TAB;

/**
 * Created by thyone on 2017-04-13.
 */
public class MyNavigationView {
    private MainActivity mMainActivity;
    private DrawerLayout mNavigationViewLayout;
    private ActionBarDrawerToggle mNavigationViewToggle;

    private static MyNavigationView ourInstance = new MyNavigationView();
    public static MyNavigationView getInstance() {
        return ourInstance;
    }
    private MyNavigationView() {
    }

    private NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            final FragmentHandler fragmentHandler = FragmentHandler.getInstance();

            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_play_quiz) {
                fragmentHandler.changeViewFragment(PLAYQUIZ);
            } else if(id == R.id.nav_scripts){
                fragmentHandler.changeViewFragment(SCRIPT_TAB);
            } else if(id == R.id.nav_add_sentence){
                fragmentHandler.changeViewFragment(ADD_SENTENCE);
            }  else if(id == R.id.nav_report){
                fragmentHandler.changeViewFragment(REPORT);
            }
            DrawerLayout drawer = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    public void setUpNavigationView(final MainActivity activity, Toolbar toolbar) {
        mMainActivity = activity;

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mNavigationViewLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        mNavigationViewToggle = new ActionBarDrawerToggle(
                activity, mNavigationViewLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavigationViewLayout.addDrawerListener(mNavigationViewToggle);
        mNavigationViewToggle.syncState();

        // to use custom hambergur icon ..
        mNavigationViewToggle.setDrawerIndicatorEnabled(false);
        toggleHamburgerIcon(R.drawable.ic_nav__hambergur_normal_grey);
        // hambergur image에게 drawer toggle 기능을 부여.
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

        // report 메뉴는 관리자용이므로, 기본적으로 안보이게 한다.
        // 이후, LogIn 결과로 관리자면, 이 메뉴를 다시 보이게 한다.
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_report).setVisible(false);

        // help 버튼 셋팅
        /*
        LayoutInflater inflater = (LayoutInflater) mMainActivity.getSystemService( mMainActivity.LAYOUT_INFLATER_SERVICE );
       View headerView = inflater.inflate( R.layout.nav_header, null );
        Button helpButton = (Button)headerView.findViewById(R.id.nav_header_help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
                fragmentHandler.changeViewFragment(WEB_VIEW);
            }
        });*/
    }

    public void attachAlarmIcon(int menuId)
    {
        // Attach Alarm on Menu Item
        toggleAlarmIconOnMenu(menuId, View.VISIBLE);

        // Attach Alarm on Hamburger Icon
        final FragmentHandler handler = FragmentHandler.getInstance();
        toggleHamburgerIcon( handler.getCurrentFragmentID() );
    }

    public void detachAlarmIcon(int menuId)
    {
        // Detach Alarm on Menu Item
        toggleAlarmIconOnMenu(menuId, View.INVISIBLE);

        // Detach Alarm on Hamburger Icon
        final FragmentHandler handler = FragmentHandler.getInstance();
        toggleHamburgerIcon( handler.getCurrentFragmentID() );
    }

    private void toggleAlarmIconOnMenu(int menuId, int visibleToggle){
        NavigationView navigationView = (NavigationView) mMainActivity.findViewById(R.id.nav_view);
        if( navigationView == null ) {
            MyLog.e("NavigationView is null!!");
            return;
        }

        Menu menu = navigationView.getMenu();
        if( menu == null ){
            MyLog.e("NavigationView.Menu is null!!");
            return;
        }

        MenuItem menuItem = menu.findItem(menuId);
        if( menuItem == null ){
            MyLog.e("NavigationView.Menu.menuItem is null. menuItemId:"+menuId);
            return;
        }
        //menuItem.setIcon(iconID);
        ImageView view = (ImageView) menuItem.getActionView();
        if( view == null ){
            MyLog.e("NavigationView.Menu.menuItem.ActionView is null. menuItemId:"+menuId);
            return;
        }

        view.setVisibility( visibleToggle );
    }

    public void toggleHamburgerIcon(FragmentHandler.EFRAGMENT fragment)
    {
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        boolean showAlarmIcon = (scriptRepo.getSyncNeededCount() > 0 );
        if( showAlarmIcon ) {
            switch (fragment)
            {
                case INTRO:
                case PLAYQUIZ:
                    toggleHamburgerIcon(R.drawable.ic_nav__hambergur_normal_grey);
                    //toggleHamburgerIcon(R.drawable.ic_nav__hambergur_alarm_grey);
                    break;
               // default:
                 //   toggleHamburgerIcon(R.drawable.ic_hamburge_orange);
                //    toggleHamburgerIcon(R.drawable.ic_nav__hambergur_normal_white);
                   // toggleHamburgerIcon(R.drawable.ic_nav__hambergur_alarm);
                //    break;
            }
            return;
        }

        switch (fragment)
        {
            case INTRO:
            case PLAYQUIZ:
                toggleHamburgerIcon(R.drawable.ic_nav__hambergur_normal_grey);
                break;
          //  default:
           //     toggleHamburgerIcon(R.drawable.ic_hamburge_orange);
           //     toggleHamburgerIcon(R.drawable.ic_nav__hambergur_normal_white);
           //     break;
        }
    }

    private void toggleHamburgerIcon(int iconId)
    {
        Drawable drawable = ResourcesCompat.getDrawable(
                mMainActivity.getResources(),iconId,
                mMainActivity.getTheme());
        if( drawable == null ){
            MyLog.e("NavigationView Hamburger Alarm Icon is null.");
            return;
        }
        mNavigationViewToggle.setHomeAsUpIndicator(drawable);
    }

    public void showAdminMenu(){
        NavigationView navigationView = (NavigationView) mMainActivity.findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_report).setVisible(true);
    }
}
