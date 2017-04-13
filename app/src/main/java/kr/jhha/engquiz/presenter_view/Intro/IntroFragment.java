package kr.jhha.engquiz.presenter_view.intro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.UserRepository;

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
        Log.d("$$$$$$$$$$$$$$$$$","IntroFragment called");

        View view = inflater.inflate(R.layout.content_intro, container, false);

        // 액션 바 감추기
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        // 1초간 intro 화면 보이기. 이후 유저 로긴 프로세스 시작
        Handler handler = new Handler();
        handler.postDelayed( runnable, mIntroAnimationSec );

        return view;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initUser();
        }
    };

    public void initUser(){
        if( bInitailized == false ) {
            bInitailized = true;
            mActionListener.checkUserExist();
        }
    }

    // 중복으로 뜨ㅣ울때, 전 다이알로그 삭제 안됬다는 에러가 나서
    // 매번 새로 만들어 띄우는걸로 함.  재사용하면서 에러수정할수있는 방법찾을 시간없어서.
    @Override
    public void showLoginDialog()
    {
        AlertDialog.Builder mDialogLogin = new AlertDialog.Builder( getActivity());
        mDialogLogin.setIcon(android.R.drawable.alert_dark_frame);
        mDialogLogin.setTitle("로그인");
        mDialogLogin.setMessage("수업에서 사용하는 영어이름을 영어로 입력해주세요.");
        mInputText = new EditText(getActivity());
        mInputText.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogLogin.setView(mInputText);
        mDialogLogin.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        mInputText.setText("");
        mInputText.setFocusable(true);
        mDialogLogin.setPositiveButton( "로그인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 제목입력완료 버튼
                String username = mInputText.getText().toString();
                mActionListener.login( username );
                d.dismiss();
            }
        });
        mDialogLogin.show();
    }

    @Override
    public void onLoginFail(int what) {
        switch (what){
            case 1:
                showLoginDialog();
                String msg =  "로그인에 실패했습니다. \nID(학원에서 사용하는 영어이름)를 다시 입력해주세요.";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                break;
            default:
                msg =  "알수 없는 오류로 로그인에 실패했습니다. \n앱을 종료합니다";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).finishApp();
                break;
        }
    }

    @Override
    public void onLoginSuccess(String username) {
        String msg =  "Welcome "+username+"!";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

        updateNavDrawer(username);
        ((MainActivity)getActivity()).changeViewFragment( MainActivity.EFRAGMENT.PLAYQUIZ );
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
    public void showSignInDialog(String msg){

        //AlertDialog.Builder mDialogSignin = new AlertDialog.Builder( getActivity(),
         //       android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        AlertDialog.Builder mDialogSignin = new AlertDialog.Builder( getActivity());
        mDialogSignin.setIcon(android.R.drawable.alert_dark_frame);
        mDialogSignin.setTitle("계정생성");
        mInputText = new EditText(getActivity());
        mInputText.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogSignin.setView(mInputText);
        mDialogSignin.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        mInputText.setText("");
        mInputText.setFocusable(true);
        mDialogSignin.setMessage(msg);
        mDialogSignin.setNeutralButton("계정이 있어요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.alreadySignIn();
                d.dismiss();
            }
        });
        mDialogSignin.setPositiveButton( "계정생성", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 제목입력완료 버튼
                String username = mInputText.getText().toString();
                mActionListener.signIn( username );
                d.dismiss();
            }
        });
        mDialogSignin.show();
    }

    @Override
    public void onSignInSuccess(Integer userId) {

    }



/*
    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getActivity().getApplicationContext(), "왼쪽버튼 클릭",
                    Toast.LENGTH_SHORT).switchBackground();
            mLoginDialog.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getActivity().getApplicationContext(), "오른쪽버튼 클릭",
                    Toast.LENGTH_SHORT).switchBackground();
        }
    };

     // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
*/





}
