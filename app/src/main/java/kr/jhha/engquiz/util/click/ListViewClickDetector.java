package kr.jhha.engquiz.util.click;

import android.os.Handler;

/**
 * Created by thyone on 2017-03-30.
 */

public class ListViewClickDetector implements ClickDetector {

    private long clickedTime = 0;
    private long resetTime = 500; // 리셋 타임 설정 - 0.5초

    private int mListviewPosition;  // 클릭한 리스트뷰 아이템 위치

    ClickDetector.Callback mCallback;
    Handler mPostDelayActor = new Handler();

    public ListViewClickDetector( ClickDetector.Callback callback ) {
        mCallback = callback;
    }

    @Override
    public void onClick(int listviewPosition ) {
        if( isFirstClick() ) {
            processFirstClick( listviewPosition );
            return;
        }

        if( isDoubleClick( listviewPosition ) ) {
            processDoubleClick();
        }
    }

    private boolean isFirstClick(){
        boolean bClickedInTime = System.currentTimeMillis() <= clickedTime + resetTime;
        return ! bClickedInTime;
    }

    private boolean isDoubleClick( int listviewPosition ){
        boolean bClickedInTime = System.currentTimeMillis() <= clickedTime + resetTime;
        boolean bSameItemCliecked = (mListviewPosition == listviewPosition);
        return bClickedInTime && bSameItemCliecked;
    }

    private void processFirstClick( int listviewPosition ){
        // 현재 클릭 시간 저장. 이 후 클릭이 더블클릭인지 구분위해.
        clickedTime = System.currentTimeMillis();

        // 싱글클릭 이벤트 실행을 위한 Timer 설정
        // 첫 클릭 후 0.6초동안 재 클릭이 없으면 싱글클릭 이벤트 실행.
        // (더블클릭 기준이 0.5 이내 재 클릭이다)
        mPostDelayActor.postDelayed( runnable, resetTime +100 );

        // 클릭 위치 저장.
        // 이후 클릭한 위치와 같은지 비교위해.
        // 클릭시간차는 더블클릭이라도, 다른 위치를 클릭하면 싱글클릭이므로.
        mListviewPosition = listviewPosition;
    }

    private void processDoubleClick(){
        // 싱글클릭 이벤트 Timer 콜백 삭제.
        mPostDelayActor.removeCallbacks(runnable);

        // 더블클릭 이벤트 실행.
        mCallback.onDoubleClicked();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 싱글클릭 이벤트 실행
            mCallback.onSingleClicked();
        }
    };

}

