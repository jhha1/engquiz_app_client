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

import kr.jhha.engquiz.R;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class LoginFragment extends Fragment implements LoginContract.View
{
    private LoginContract.UserActionsListener mActionListener;

    private LinearLayout mLogInLayout = null;
    private EditText mLogInNickname = null;
    private Button mLogInConfirmBtn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("$$$$$$$$$$$$$$$$$","LoginFragment called");

        View view = inflater.inflate(R.layout.content_login, container, false);

        mActionListener = new LoginPresenter( this );

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
                    mActionListener.logIn( nickname );
                    break;
            }
        }
    };
}
