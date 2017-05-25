package kr.jhha.engquiz.presenter_view.intro;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import kr.jhha.engquiz.util.PermmissionHelper;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.util.PermmissionHelper.PERMISSION_STORAGE;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class IntroFragment extends Fragment implements IntroContract.View
{
    private IntroContract.UserActionsListener mActionListener;

    private final int mIntroAnimationSec = 1000;
    private static boolean bInitailized = false;

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
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        // 1초간 intro 화면 보이기. 이후 앱 시작.
        Handler handler = new Handler();
        handler.postDelayed( runnable, mIntroAnimationSec );

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };

    public void initData(){
        if( bInitailized == false ) {

            // 파일 r/w 에 필요한 접근 얻기.
            int permissionCode = PERMISSION_STORAGE;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};  // write에  read도 포함됨.
            boolean bPermissionAllowed = PermmissionHelper.checkAndRequestPermission(this, permissionCode, permissions);

            // 권한이 이미 허락되어있다.
            if (bPermissionAllowed) {
                mActionListener.initialize();
                bInitailized = true;
            }
        }
    }

    // 권한을 유저로부터 허락받는중.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_STORAGE:
                if (PermmissionHelper.verifyPermissions(grantResults))
                {
                    // 권한을 얻었다.
                    MyLog.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! onRequestPermissionsResult: PermissionAllowed " );
                    mActionListener.initialize();

                }
                else
                {
                    // 권한을 얻지 못했다. Show Rational Dialog
                    MyLog.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! onRequestPermissionsResult: NONE!! Permission Not Allowed " );
                    PermmissionHelper.showGoToAppSettingForPermissionDialog(getActivity());

                    /*
                    if( PermmissionHelper.isMaxPermissionRefused() ){
                        MyLog.d("onRequestPermissionsResult: MaxPermissionRefused. " );
                        MyDialog.showDialogAndForcedCloseApp(getActivity(), getString(R.string.common__finish_app));
                        return;
                    }
                    PermmissionHelper.increasePermissionRefuseCount();

                    // 권한을 얻지 못했다. Show Rational Dialog
                    MyLog.d("onRequestPermissionsResult: NONE!! Permission Not Allowed " );
                    String message = PermmissionHelper.getRationalMessage(getActivity(), PERMISSION_STORAGE);
                    PermmissionHelper.showRequestPermissionDialog(getActivity(), this, message);*/
                }
                break;
        }
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
    public void onLoginFail(int what) {
        switch (what){
            case 1:
                showLoginDialog(R.string.login__fail_no_exist_user);
                break;
            case 2:
                String msg = getString(R.string.login__fail_network_err);
                MyDialog.showDialogAndForcedCloseApp(getActivity(), msg);
                break;
            case 3:
                showLoginDialog(R.string.login__fail_invalid_name);
                break;
            default:
                msg = getString(R.string.login__fail);
                MyDialog.showDialogAndForcedCloseApp(getActivity(), msg);
                break;
        }
    }

    @Override
    public void onLoginSuccess(String username) {
        String msg =  "Welcome  "+username+" !! ";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

        updateNavDrawer(username);
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.changeViewFragment( FragmentHandler.EFRAGMENT.PLAYQUIZ );
    }

    private void updateNavDrawer(String username){
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);

        // TODO
        boolean bAdmin = true;
        if( ! bAdmin ) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_report).setVisible(false);
        }

        TextView navHeaderUserName = (TextView) getActivity().findViewById(R.id.nav_header_username);
        navHeaderUserName.setText(username);
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
