package kr.jhha.engquiz.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.controller.AddScript;
import kr.jhha.engquiz.view.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AppIntro extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.app_intro, container, false);

        // 3. 확인 버튼 클릭 이벤트 셋팅
        Button mOKButton = (Button) view.findViewById(R.id.intro_game_play_btn);
        mOKButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                switch (v.getId()) {
                    case R.id.intro_game_play_btn:
                        //   v.setVisibility(View.INVISIBLE);
                        ((MainActivity) getActivity()).startAppContent();
                        break;
                }
            }
        });

        return view;
    }

    // 확인, 취소 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.intro_game_play_btn:
                 //   v.setVisibility(View.INVISIBLE);
                    ((MainActivity) getActivity()).startAppContent();
                    break;
            }
        }
    };

}
