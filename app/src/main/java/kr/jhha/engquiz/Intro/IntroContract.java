package kr.jhha.engquiz.Intro;

/**
 * Created by jhha on 2017-03-15.
 * This specifies the contract between the view and the presenter.
 */

public interface IntroContract {

    interface View {

        void setProgressIndicator(boolean active);

        void changeViewFragment(IntroActivity.FRAGMENT fragment);
    }

    interface UserActionsListener {
        void initailizeData();
    }
}
