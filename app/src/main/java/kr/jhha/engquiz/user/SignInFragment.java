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
import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.UserModel;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new SignInPresenter( this, UserModel.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("$$$$$$$$$$$$$$$$$","SignInFragment called");

        View view = inflater.inflate(R.layout.content_signin, container, false);

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
                    changeViewToLogIn();
                    break;
                case R.id.signin_btn:
                    String nickname = mSignInNickname.getText().toString();
                    signIn( nickname );
                    break;
            }
        }
    };

    private void signIn( String nickname ){
        mActionListener.signIn( nickname );
    }

    @Override
    public void onSignInSuccess(Integer userId) {
        // redirect to login logic
        final Fragment fragment = ((MainActivity)getActivity()).getFragment( MainActivity.EFRAGMENT.LOGIN );
        ((LoginFragment) fragment).logIn( userId );
    }

    // change view to login
    private void changeViewToLogIn() {
        ((MainActivity)getActivity()).changeViewFragment( MainActivity.EFRAGMENT.LOGIN );
    }
}
