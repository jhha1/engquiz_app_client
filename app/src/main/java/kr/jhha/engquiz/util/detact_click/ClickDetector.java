package kr.jhha.engquiz.util.detact_click;

/**
 * Created by jhha on 2017-03-30.
 * 아이템 클릭과 더블클릭 분별하는 클래스.
 */

public interface ClickDetector {
    interface Callback {
        void onSingleClicked();
        void onDoubleClicked();
    }

    void onClick(int arg );
}