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

import kr.jhha.engquiz.Intro.IntroActivity;
import kr.jhha.engquiz.R;

/**
 * Created by thyone on 2017-03-16.
 */

public class SignInFragment extends Fragment implements SignInContract.View
{
    private SignInContract.UserActionsListener mActionListener;

    private LinearLayout mSignInLayout = null;
    private EditText mSignInNickname = null;
    private Button mChangeLoginState = null;
    private Button mSignInConfirmBtn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("$$$$$$$$$$$$$$$$$","SignInFragment called");

        View view = inflater.inflate(R.layout.content_signin, container, false);

        mActionListener = new SignInPresenter( this );

        mSignInLayout = (LinearLayout) view.findViewById(R.id.signin_layout);
        mSignInNickname = (EditText) view.findViewById(R.id.signin_nickname);
        mChangeLoginState = (Button) view.findViewById(R.id.login_i_have_id_btn);
        mChangeLoginState.setOnClickListener(mClickListener);
        mSignInConfirmBtn = (Button) view.findViewById(R.id.signin_btn);
        mSignInConfirmBtn.setOnClickListener(mClickListener);

        return view;
    }

    // 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_i_have_id_btn:
                    ((IntroActivity)getActivity()).changeViewFragment( IntroActivity.FRAGMENT.LOGIN );
                    break;
                case R.id.signin_btn:
                    String nickname = mSignInNickname.getText().toString();
                    mActionListener.signIn( nickname );
                    break;
            }
        }
    };

}
