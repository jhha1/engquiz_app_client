package kr.jhha.engquiz.presenter_view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.Stack;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.presenter_view.scripts.ParseScriptFragment;
import kr.jhha.engquiz.presenter_view.scripts.ScriptsFragment;
import kr.jhha.engquiz.presenter_view.addsentence.AddSentenceFragment;
import kr.jhha.engquiz.presenter_view.admin.report.ReportFragment;
import kr.jhha.engquiz.presenter_view.intro.IntroFragment;
import kr.jhha.engquiz.presenter_view.playquiz.QuizPlayFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.AddQuizFolderFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.AddScriptIntoFolderFragment;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.FolderScriptsFragment;
import kr.jhha.engquiz.presenter_view.sentences.SentenceFragment;
import kr.jhha.engquiz.presenter_view.sync.SyncFragment;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_INTO_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.INTRO;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.NEW_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.PLAYQUIZ;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.REPORT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_QUIZFOLDERS;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SCRIPTS;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SCRIPTS_IN_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SENTENCES_IN_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SYNC;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

public class FragmentHandler {

    private MainActivity mMainActivity;

    private IntroFragment mIntroFragment;
    private QuizPlayFragment mPlayQuizFragment;
    private SyncFragment mSyncFragment;
    private ParseScriptFragment mParseScriptFragment;
    private ScriptsFragment  mScriptsFragment;
    private AddSentenceFragment mAddSentenceFragment;
    private QuizFoldersFragment mQuizFoldersFragment;
    private FolderScriptsFragment mQuizFolderScriptListFragment;
    private AddScriptIntoFolderFragment mQuizFolderAddScriptFragment;
    private Fragment mAddQuizFolderFragment;
    private ReportFragment mReportFragment;
    private SentenceFragment mShowSentenceFragment;
    private WebViewFragment mWebViewFragment;

    // 현재 display되는 fragment가 어떤건지 알고싶어서.
    // onBackPressed 시에 특정 fragment에서만 뒤로가기시 종료하게 하고싶어서 넣음.
    // 안드로이드에서는 back stack 내부에 index 0번의 fragment를 리턴하므로,  내가 원하는 형태로 지원안하는거 같음
    private Stack<EFRAGMENT> mFragmentStack;

    public enum EFRAGMENT {
        NONE,
        INTRO,
        PLAYQUIZ,
        SHOW_SCRIPTS,
        ADD_SCRIPT,
        ADD_SENTENCE,
        SHOW_QUIZFOLDERS,
        NEW_QUIZFOLDER,
        SHOW_SCRIPTS_IN_QUIZFOLDER,
        ADD_SCRIPT_INTO_QUIZFOLDER,
        SHOW_SENTENCES_IN_SCRIPT,
        SYNC,
        REPORT,
        WEB_VIEW
    }

    private static FragmentHandler ourInstance = new FragmentHandler();
    public static FragmentHandler getInstance() {
        return ourInstance;
    }
    private FragmentHandler() {
        mFragmentStack = new Stack<>();
    }

    public void setUpFragments(MainActivity mainActivity) {
        mMainActivity = mainActivity;

        mIntroFragment = new IntroFragment();
        mPlayQuizFragment = new QuizPlayFragment();
        mParseScriptFragment = new ParseScriptFragment();
        mScriptsFragment = new ScriptsFragment();
        mAddSentenceFragment = new AddSentenceFragment();
        mQuizFoldersFragment = new QuizFoldersFragment();
        mSyncFragment = new SyncFragment();
        mReportFragment = new ReportFragment();
        mQuizFolderScriptListFragment = new FolderScriptsFragment();
        mQuizFolderAddScriptFragment = new AddScriptIntoFolderFragment();
        mAddQuizFolderFragment = new AddQuizFolderFragment();
        mShowSentenceFragment = new SentenceFragment();
        mWebViewFragment = new WebViewFragment();
    }

    public EFRAGMENT getCurrentFragmentID(){
        return mFragmentStack.peek();
    }

    public Fragment getFragment( FragmentHandler.EFRAGMENT eFragment ){
        Fragment fragment = null;
        switch ( eFragment ){
            case PLAYQUIZ:
                fragment = mPlayQuizFragment; break;
            case NEW_QUIZFOLDER:
                fragment = mAddQuizFolderFragment; break;
            case SHOW_SCRIPTS_IN_QUIZFOLDER:
                fragment = mQuizFolderScriptListFragment; break;
            case ADD_SCRIPT_INTO_QUIZFOLDER:
                fragment = mQuizFolderAddScriptFragment; break;
            case SHOW_SENTENCES_IN_SCRIPT:
                fragment = mShowSentenceFragment; break;
            case SYNC:
                fragment = mSyncFragment; break;
            case ADD_SCRIPT:
                fragment = mParseScriptFragment; break;
            case WEB_VIEW:
                fragment = mWebViewFragment; break;

        }
        return fragment;
    }

    /*
        1. transaction.replace
            transaction.addToBackStack(null); : 뒤로가기버튼 클릭시, 전 단계 프레그먼트 화면이 보이도록. (전 단계 프레그먼트가 FragmentTransaction stack에 남아있다)
        2. transaction.replace : 뒤로가기버튼 클릭시, 전 단계 프레그먼트 화면이 보이는거 방지
     */
    public void changeViewFragment(EFRAGMENT fragment)
    {
        MyLog.d("changeViewFragment called. fragment("+fragment+")");

        boolean bAddStack = true;
        FragmentTransaction transaction = mMainActivity.getSupportFragmentManager().beginTransaction();
        switch (fragment)
        {
            case INTRO:
                transaction.addToBackStack(null); // 첫 화면이라서 넣어줘야함
                transaction.add(R.id.container, mIntroFragment, INTRO.toString());
                break;
            case PLAYQUIZ:
                // play 화면에서 뒤로가면 종료하기 위해. 프레그먼트 백스택을 안넣음
                bAddStack = false;
                transaction.replace(R.id.container, mPlayQuizFragment, PLAYQUIZ.toString());
                break;
            case ADD_SCRIPT:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mParseScriptFragment, ADD_SCRIPT.toString());
                break;
            case SHOW_SCRIPTS:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mScriptsFragment, SHOW_SCRIPTS.toString());
                break;
            case ADD_SENTENCE:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mAddSentenceFragment, ADD_SENTENCE.toString());
                break;
            case SHOW_QUIZFOLDERS:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mQuizFoldersFragment, SHOW_QUIZFOLDERS.toString());
                break;
            case SYNC:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mSyncFragment, SYNC.toString());
                break;
            case REPORT:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mReportFragment, REPORT.toString());
                break;
            case NEW_QUIZFOLDER:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mAddQuizFolderFragment, NEW_QUIZFOLDER.toString());
                break;
            case SHOW_SCRIPTS_IN_QUIZFOLDER:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mQuizFolderScriptListFragment, SHOW_SCRIPTS_IN_QUIZFOLDER.toString());
                break;
            case SHOW_SENTENCES_IN_SCRIPT:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mShowSentenceFragment, SHOW_SENTENCES_IN_SCRIPT.toString());
                break;
            case ADD_SCRIPT_INTO_QUIZFOLDER:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mQuizFolderAddScriptFragment, ADD_SCRIPT_INTO_QUIZFOLDER.toString());
                break;
            case WEB_VIEW:
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, mWebViewFragment, WEB_VIEW.toString());
                break;
            default:
                // quiz play fragment
                bAddStack = false;
                transaction.replace(R.id.container, mPlayQuizFragment, PLAYQUIZ.toString());
                break;
        }
        transaction.commit();

        // mFragmentStack는 '뒤로가기 클릭시 종료..' 메세지를 뿌리기용으로 사용한다.
        // FragmentTransaction가 실시간으로 display되고 있는 fragment를 안알려줘서. 현재 display fragment 알려고.
        // test결과 실제 앱 종료는 어차피 FragmentTransaction 에서 back stack 관리에 의해 실행된다.
        //  - (BackPressedCloseHandler 의 자체 앱종료 있지만, 이 함수 호출전에  FragmentTransaction에서 back stack == 0 으로 먼저 앱 종료됨)
        if( bAddStack ){
            mFragmentStack.push(fragment);
        } else {
            // replace fragment
            if( ! mFragmentStack.isEmpty() )
                mFragmentStack.pop();
            mFragmentStack.push(fragment);
        }
    }

    public void onBackPressed()
    {
        if( mFragmentStack.size() > 1 ){
            // mFragmentStack을 empty로 만들면 peek() 하는 곳에서 뻑난다.
            // pop() 안되도 괜찮음. 최악으로는 '뒤로 버튼을 누르시면 앱종료됩니다..'메세지가 안나오는 것 뿐이다.
            mFragmentStack.pop();
        }
    }
}

