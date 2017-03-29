package kr.jhha.engquiz.quizgroup;

import android.util.Log;

import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.QuizGroupDetail;
import kr.jhha.engquiz.data.local.QuizGroupModel;
import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.quizgroup.detail.QuizGroupDetailAdapter;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizGroupPresenter implements ShowQuizGroupsContract.UserActionsListener {

    private final ShowQuizGroupsContract.View mView;
    private final QuizGroupModel mModel;        // remote model
    private QuizGroupAdapter mAdapter;          // local memory model

    public static final String Text_New = "New..";

    public ShowQuizGroupPresenter(ShowQuizGroupsContract.View view, QuizGroupModel model ) {
        mModel = model;
        mView = view;
        mAdapter = new QuizGroupAdapter( mModel );
    }


    @Override
    public void quizGroupItemClicked(QuizGroupSummary item) {

        String title = item.getTitle();
        Log.d("%%%%%%%%%%%%%%%", "ShowQuizGroupsFragment.quizGroupItemClicked. title:" + title);

        if( Text_New.equals(title) )
            mView.onChangeViewFragmet( MainActivity.EFRAGMENT.QUIZQROUP_NEW );  // 새 커스텀 퀴즈 리스트 만들기 화면 전환
        else
            mView.onChangeViewFragmet( MainActivity.EFRAGMENT.QUIZGROUP_DETAIL_SHOW );  // 퀴즈그룹 디테일 보기 호ㅏ면전환
    }

    public void getQuizGroupSummaryList() {
        Log.i("AppContent", "ShowQuizGroupPresenter getQuizGroupSummaryList() called");
        Integer userId = UserModel.getInstance().getUserID();
        mModel.getQuizGroupSummaryList( userId, onGetQuizGroupSummaryList() );
    }

    private QuizGroupModel.GetQuizGroupCallback onGetQuizGroupSummaryList() {
        return new QuizGroupModel.GetQuizGroupCallback(){

            @Override
            public void onSuccess(List<QuizGroupSummary> quizGroupSummaryList) {
                mView.onGetQuizGroupSummaryListSuccess();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onGetQuizGroupSummaryListFail();
            }
        };
    }

    public void delQuizGroup( Integer position ){
        Log.i("AppContent", "ShowQuizGroupPresenter delQuizGroup() called");

        int quizGroupCount = mModel.getQuizGroupSummaryCount();
        if( quizGroupCount <= 0 ) {
            String msg = "삭제할 퀴즈그룹이 없습니다";
            mView.onDelQuizGroupFail( msg );
            return;
        }

        Integer userId = UserModel.getInstance().getUserID();
        QuizGroupSummary summary = mModel.getQuizGroupSummary( position );
        Integer quizGroupId = summary.getQuizGroupId();

        mModel.delQuizGroup( userId, quizGroupId, onDelQuizGroup() );
    }

    private QuizGroupModel.DelQuizGroupCallback onDelQuizGroup() {
        return new QuizGroupModel.DelQuizGroupCallback(){

            @Override
            public void onSuccess(List<QuizGroupSummary> quizGroupSummaryList) {
                mView.onDelQuizGroupSuccess();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg = "퀴즈그룹 삭제에 실패했습니다";
                mView.onDelQuizGroupFail(msg);
            }
        };
    }

    @Override
    public void changePlayingQuizGroup(QuizGroupSummary mListviewSelectedItem) {
        QuizGroupDetail quizGroupDetail = new QuizGroupDetail();
        quizGroupDetail.setSummary( mListviewSelectedItem );
        // TODO set script list
        QuizPlayModel.getInstance().changePlayingQuizGroup( quizGroupDetail );
        mView.onShowChangePlayingQuizGroupSuccess();
    }
}
