package kr.jhha.engquiz.presenter_view.scripts;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.scripts.custom.CustomScriptsFragment;
import kr.jhha.engquiz.presenter_view.scripts.regular.RegularScriptsFragment;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SCRIPT_TAB;
import static kr.jhha.engquiz.presenter_view.scripts.ScriptTabFragment.TabView.CUSTOM;
import static kr.jhha.engquiz.presenter_view.scripts.ScriptTabFragment.TabView.REGULAR;

public class ScriptTabFragment extends Fragment
{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyToolbar mToolbar;
    public class TabView {
        public static final int REGULAR = 0;
        public static final int CUSTOM = 1;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_script_tab, container, false);

        // 탭 셋팅
        mTabLayout = (TabLayout) view.findViewById(R.id.layout_tab);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.script_tab_regular)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.script_tab_custom)));

        // 탭 페이지 넘김 관련 (view pager) 셋팅
        mViewPager = (ViewPager) view.findViewById(R.id.tab_pager);
        TabPagerAdapter pagerAdapter = new TabPagerAdapter( getChildFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(pagerAdapter);

        //ViewPager에서 페이지의 상태가 변경될 때 페이지 변경 이벤트를 TabLayout에 전달하여  탭의 선택 상태를 동기화해주는 역할
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        // 탭 선택 리스너 이벤트
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.updateToolBar(SCRIPT_TAB);
    }

    TabLayout.OnTabSelectedListener mTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab){}

        @Override
        public void onTabReselected(TabLayout.Tab tab){}
    };

    private void changeFragment() {

    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        // Count number of tabs
        private int tabCount;
        Fragment tab1;
        Fragment tab2;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            tab1 = new RegularScriptsFragment();
            tab2 = new CustomScriptsFragment();
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment currentTab = tab1;
            // Returning the current tabs
            switch (position) {
                case REGULAR:
                    currentTab = tab1;
                    break;
                case CUSTOM:
                    currentTab = tab2;
                    break;
            }
            return currentTab;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    /*
         Action Bar
        */
    private void initToolbar() {
        // 액션 바 보이기
        mToolbar = MyToolbar.getInstance();
        setHasOptionsMenu(true);
    }

    // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 화면에 보여질때 마다 호출됨
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mToolbar.updateToolBarOptionMenu(SCRIPT_TAB, menu);
        super.onPrepareOptionsMenu(menu);
    }
}



