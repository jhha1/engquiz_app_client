package kr.jhha.engquiz.presenter_view.intro;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.presenter_view.MyNavigationView;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.PermmissionHelper;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.util.PermmissionHelper.PERMISSION_STORAGE;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class IntroFragment extends Fragment implements IntroContract.View
{
    private IntroContract.UserActionsListener mActionListener;

    private final int mIntroAnimationSec = 1000;
    private static boolean bAppInitailized = false;

    private EditText mInputText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionListener = new IntroPresenter( getActivity(), this, UserRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_intro, container, false);

        // 액션 바 감추기
        MyToolbar.getInstance().hide();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if( bAppInitailized )
            return;

        // onResume() 에서 반복 호출로 중복 퍼미션 체킹 및 권한 다이알로그 띄워서
        // UX의 루프에 빠지는걸 막기 위해 플레그 셋팅.
        if( PermmissionHelper.bAlreadyCheckAndShowDialog)
            return;

        // 1초간 intro 화면 보이기. 이후 앱 시작.
        Handler handler = new Handler();
        handler.postDelayed( runnable, mIntroAnimationSec );
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run()
        {
            // onResume() 에서 반복 호출 막기 위해 플레그 셋팅.
            PermmissionHelper.bAlreadyCheckAndShowDialog = true;

            // 앱 권한 체크 및 권한얻기 다이알로그 띄우기
            IntroFragment thisFragmentObject = (IntroFragment)FragmentHandler.getInstance().getFragment(FragmentHandler.EFRAGMENT.INTRO);
            boolean bPermissionAllowed = PermmissionHelper.checkAndRequestPermission( thisFragmentObject );
            if (bPermissionAllowed) {
                // 권한이 이미 허락되어있다.
                // 앱 데이터 로드 및 셋팅
                appInitialize();
            }
        }
    };

    // 권한을 유저로부터 허락받는중.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_STORAGE:
                if (PermmissionHelper.verifyPermissions(grantResults)) {
                    // 권한을 얻었다.
                    appInitialize();
                } else {
                    // 권한을 얻지 못했다. Show Dialog
                    PermmissionHelper.showGoToAppSettingForPermissionDialog(getActivity());
                }
                break;
        }
    }

    private void appInitialize(){
        // 앱 데이터 로드 및 셋팅
        mActionListener.initialize();
        bAppInitailized = true;
    }

    // 중복으로 뜨ㅣ울때, 전 다이알로그 삭제 안됬다는 에러가 나서
    // 매번 새로 만들어 띄우는걸로 함.  재사용하면서 에러수정할수있는 방법찾을 시간없어서.
    @Override
    public void showLoginDialog(int msgId) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.login__title));
        dialog.setMessage(getString(msgId));
        mInputText = Etc.makeEditText(getActivity());
        mInputText.setFocusable(true);
        dialog.setEditText(mInputText);
        dialog.setNeutralButton("로그인", new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // 제목입력완료 버튼
                String username = mInputText.getText().toString();
                mActionListener.login( username );
                dialog.dismiss();
            }});
        dialog.setNegativeButton(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss();
                String msg = getString(R.string.login__cancel);
                MyDialog.showDialogAndForcedCloseApp(getActivity(), msg);
            }});
        dialog.showUp();
    }

    @Override
    public void onLoginFail(int msgId) {
        switch (msgId){
            case 1:
                showLoginDialog(msgId);
                break;
            case 2:
                String msg = getString(msgId);
                MyDialog.showDialogAndForcedCloseApp(getActivity(), msg);
                break;
            case 3:
                showLoginDialog(msgId);
                break;
            default:
                msg = getString(msgId);
                MyDialog.showDialogAndForcedCloseApp(getActivity(), msg);
                break;
        }
    }

    // 로긴성공.
    // 게임 진입 첫 화면으로 이동 : 퀴즈 플레이 화면
    @Override
    public void onLoginSuccess(String username) {
        String msg =  "Welcome  "+username+" !! ";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

        MyNavigationView.getInstance().setUserName(username);

        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.changeViewFragment( FragmentHandler.EFRAGMENT.PLAYQUIZ );
    }

    @Override
    public void showSignInDialog(int msgId){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.signin__title));
        dialog.setMessage(getString(msgId));
        mInputText = Etc.makeEditText(getActivity());
        mInputText.setFocusable(true);
        dialog.setEditText(mInputText);
        dialog.setNeutralButton(getString(R.string.signin__neutral_btn),
                new View.OnClickListener() {
                    public void onClick(View arg0)
                    {
                            mActionListener.alreadySignIn();
                            dialog.dismiss();
                    }});
        dialog.setPositiveButton(getString(R.string.signin__positive_btn),
                new View.OnClickListener() {
                    public void onClick(View arg0)
                    {
                        String username = mInputText.getText().toString();
                        mActionListener.signIn( username );
                        dialog.dismiss();
                    }});
        dialog.showUp();
    }

    @Override
    public void onSignInSuccess(Integer userId) {

    }

    @Override
    public void onSignInFail(int msgId) {
        MyDialog.showDialogAndForcedCloseApp(getActivity(), getString(msgId));
    }

}
