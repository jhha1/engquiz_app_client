package kr.jhha.engquiz.quizfolder;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizFolderPresenter implements ShowQuizFoldersContract.ActionsListener {

    private final ShowQuizFoldersContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;

    public ShowQuizFolderPresenter(ShowQuizFoldersContract.View view, QuizFolderRepository model ) {
        mQuizFolderModel = model;
        mView = view;
    }

    public void getQuizFolderList() {
        Log.i("AppContent", "ShowQuizFolderPresenter getQuizFolders() called");
        Integer userId = UserModel.getInstance().getUserID();
        mQuizFolderModel.getQuizFolders( userId, onGetQuizFolderList() );
    }

    private QuizFolderRepository.GetQuizFolderCallback onGetQuizFolderList() {
        return new QuizFolderRepository.GetQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                mView.onSuccessGetQuizFolderList();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetQuizFolderList();
            }
        };
    }

    @Override
    public void listViewItemClicked(QuizFolder item) {
        String title = item.getTitle();
        Log.d("%%%%%%%%%%%%%%%", "ShowQuizFoldersFragment.listViewItemClicked. title:" + title);

        if( QuizFolder.TEXT_NEW.equals(title) )
            // 새 커스텀 퀴즈 리스트 만들기 화면 전환
            mView.onChangeViewFragmet( MainActivity.EFRAGMENT.QUIZQROUP_NEW );
        else
            // 퀴즈폴더 디테일 보기 화면전환
            mView.onChangeViewFragmet( MainActivity.EFRAGMENT.QUIZGROUP_DETAIL_SHOW );
    }

    @Override
    public void listViewItemDoubleClicked( QuizFolder listviewSelectedItem ) {
        // 이 퀴즈폴더를 게임플레이용을 설정할지 묻는 다이알로그 열기
        mView.showDialogChangePlayingQuizFolder( listviewSelectedItem );
    }

    @Override
    // 이 퀴즈폴더를 게임 플레이용으로 설정
    public void changePlayingQuizFolder(QuizFolder item) {
        Log.i("AppContent", "ShowQuizFolderPresenter changePlayingQuizFolder() called. item:"+((item!=null)?item.toString():null));
        if( item == null || QuizFolder.isNull(item) ){
            mView.onFailChangePlayingQuizFolder();
            return;
        }

        QuizPlayModel.getInstance().changePlayingQuizFolder( item );
        mView.onSucessChangePlayingQuizFolder();
    }

    @Override
    public void delQuizFolder( QuizFolder item ){
        Log.i("AppContent", "ShowQuizFolderPresenter delQuizFolder() called");

        if( item == null ) {
            String msg = "삭제할 퀴즈폴더가 없습니다";
            mView.onFailDelQuizFolder( msg );
            return;
        }

        Integer userId = UserModel.getInstance().getUserID();
        Integer quizFolderId = item.getId();
        mQuizFolderModel.delQuizFolder( userId, quizFolderId, onDelQuizFolder() );
    }

    private QuizFolderRepository.DelQuizFolderCallback onDelQuizFolder() {
        return new QuizFolderRepository.DelQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                mView.onSuccessDelQuizFolder();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg = "퀴즈폴더 삭제에 실패했습니다";
                mView.onFailDelQuizFolder(msg);
            }
        };
    }
}
