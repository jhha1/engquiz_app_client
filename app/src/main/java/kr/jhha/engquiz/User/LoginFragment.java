package kr.jhha.engquiz.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class LoginFragment extends Fragment implements LoginContract.View
{
    private LoginContract.UserActionsListener mActionListener;

    private LinearLayout mLogInLayout = null;
    private EditText mLogInNickname = null;
    private Button mLogInConfirmBtn = null;

    private static boolean bInitailized = false;

    public LoginFragment() {
        mActionListener = new LoginPresenter( this, UserModel.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("$$$$$$$$$$$$$$$$$","LoginFragment called");

        initUser();

        View view = inflater.inflate(R.layout.content_login, container, false);

        mLogInLayout = (LinearLayout) view.findViewById(R.id.login_layout);
        mLogInNickname = (EditText) view.findViewById(R.id.login_nickname);
        mLogInConfirmBtn = (Button) view.findViewById(R.id.login_btn);
        mLogInConfirmBtn.setOnClickListener(mClickListener);

        return view;
    }

    // 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn:
                    String nickname = mLogInNickname.getText().toString();
                    logIn( nickname );
                    break;
            }
        }
    };

    public void logIn( String nickname ){
        mActionListener.login( nickname );
    }

    public void logIn( Integer userId ){
        mActionListener.login( userId );
    }

    public void initUser(){
        if( bInitailized == false ) {
            bInitailized = true;
            mActionListener.initUser();
        }
    }

    @Override
    public void onChangeViewToSignIn() {
        ((MainActivity)getActivity()).changeViewFragment( MainActivity.EFRAGMENT.SIGNIN );
    }

    @Override
    public void onLoginFail() {
        String msg =  "로그인에 실패했습니다. \nID(학원에서 사용하는 영어이름)를 다시 입력해주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginSuccess() {
        String msg =  "Welcome !";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

        ((MainActivity)getActivity()).changeViewFragment( MainActivity.EFRAGMENT.PLAYQUIZ );
    }
}
