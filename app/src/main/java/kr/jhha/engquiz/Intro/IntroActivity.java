package kr.jhha.engquiz.Intro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.backend_logic.Initailizer;
import kr.jhha.engquiz.ui.MainActivity;
import kr.jhha.engquiz.user.LoginContract;
import kr.jhha.engquiz.user.LoginFragment;
import kr.jhha.engquiz.user.SignInFragment;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class IntroActivity extends AppCompatActivity
{
    private final int mIntroAnimationSec = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_intro);

        // 액션 바 감추기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 1초 후 인트로 액티비티 제거
        Handler handler = new Handler();
        handler.postDelayed( runnable, mIntroAnimationSec );
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
           Intent intent = new Intent( IntroActivity.this, MainActivity.class );
            startActivity( intent );

            // 뒤로가기 버튼으로 인트로에 다시 못오게 finish()로 액티비티 완전 종료.
            finish();
        }
    };
}
