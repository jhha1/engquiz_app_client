package kr.jhha.engquiz.quizfolder;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.QuizPlayRepository;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

import static kr.jhha.engquiz.data.local.QuizFolder.STATE_NEWBUTTON;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizFoldersPresenter implements ShowQuizFoldersContract.ActionsListener {

    private final ShowQuizFoldersContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;

    public ShowQuizFoldersPresenter(ShowQuizFoldersContract.View view, QuizFolderRepository model ) {
        mQuizFolderModel = model;
        mView = view;
    }

    public void getQuizFolderList() {
        Log.i("AppContent", "ReportPresenter getQuizFolders() called");
        mQuizFolderModel.getQuizFolders( onGetQuizFolderList() );
    }

    private QuizFolderRepository.GetQuizFolderCallback onGetQuizFolderList() {
        return new QuizFolderRepository.GetQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                mView.onSuccessGetQuizFolderList(quizFolders);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetQuizFolderList();
            }
        };
    }

    @Override
    public void listViewItemClicked(QuizFolder quizFolder) {
        String title = quizFolder.getTitle();
        Log.d("%%%%%%%%%%%%%%%", "ReportFragment.listViewItemClicked. title:" + title);

        if( QuizFolder.TEXT_NEW_FOLDER.equals(title) ) {
            // 새 커스텀 퀴즈 리스트 만들기 화면 전환
            mView.onChangeFragmetNew();
        } else {
            // 퀴즈폴더 디테일 보기 화면전환
            mView.onChangeFragmetFolderDetail(quizFolder.getId(), title);
        }
    }

    @Override
    public void listViewItemDoubleClicked( QuizFolder listviewSelectedItem ) {
        // 이 퀴즈폴더를 게임플레이용을 설정할지 묻는 다이알로그 열기
        mView.showDialogChangePlayingQuizFolder( listviewSelectedItem );
    }

    @Override
    // 이 퀴즈폴더를 게임 플레이용으로 설정
    public void changePlayingQuizFolder(QuizFolder item) {
        Log.i("AppContent", "ReportPresenter changePlayingQuizFolder() called. item:"+((item!=null)?item.toString():null));
        if( QuizFolder.isNull(item)){
            mView.onFailChangePlayingQuizFolder(1);
            return;
        }

        if( item.getState() == STATE_NEWBUTTON ){
            mView.onFailChangePlayingQuizFolder(2);
            return;
        }

        // 1.퀴즈폴더를 플레이용으로 변경위해서는,
        //  퀴즈폴더 내 script sentenceId 들이 필요하다.
        // 2. script id들은 퀴즈폴더 상세보기를 클릭했을때 load 되도록 설계되어있다.
        // 만약, 유저가 퀴즈폴더 상세보기를 안하고 플레이용으로 변경시도한다면 1,2의 조건에 위배.
        // 이 부분 커버위해, script Id가 없으면 load.
        List<Integer> scriptIDs = item.getScriptIds();
        boolean bQuizFolderDetailNotLoaded = (scriptIDs == null || scriptIDs.isEmpty());
        if( bQuizFolderDetailNotLoaded ){
            loadQuizFolderScriptIdsAndChangePlayingQuizFolder(item);
        } else {
            mQuizFolderModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder() );
        }
    }

    private QuizFolderRepository.ChangePlayingQuizFolderCallback onChangePlayingQuizFolder() {
        return new QuizFolderRepository.ChangePlayingQuizFolderCallback(){

            @Override
            public void onSuccess(QuizFolder playingQuizFolder) {
                // change folder of Memory
                final QuizPlayRepository quizPlayRepo = QuizPlayRepository.getInstance();
                EResultCode code = quizPlayRepo.changePlayingQuizFolder( playingQuizFolder, playingQuizFolder.getScriptIds() );
                if(code != EResultCode.SUCCESS){
                    mView.onFailChangePlayingQuizFolder(1);
                    return;
                }
                mView.onSucessChangePlayingQuizFolder();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                switch (resultCode){
                    case NOEXSITED_SCRIPT:
                        mView.onFailChangePlayingQuizFolder(3);
                        break;
                    case QUIZFOLDER__NOEXIST_QUIZFOLDERID:
                    default:
                        mView.onFailChangePlayingQuizFolder(1);
                        break;
                }
            }
        };
    }

    private void loadQuizFolderScriptIdsAndChangePlayingQuizFolder(final QuizFolder item )
    {
        final Integer userId = UserRepository.getInstance().getUserID();
        mQuizFolderModel.getQuizFolderScriptList(
                userId,
                item.getId(),
                new QuizFolderRepository.GetQuizFolderScriptListCallback() {
                        @Override
                        public void onSuccess(List<Integer> quizFolderScripts) {
                            mQuizFolderModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder() );
                        }

                        @Override
                        public void onFail(EResultCode resultCode) {
                            mView.onFailChangePlayingQuizFolder(3);
                        }
                });
    }

    @Override
    public void delQuizFolder( QuizFolder item ){
        Log.i("AppContent", "ReportPresenter delQuizFolderScript() called");

        if( item == null ) {
            String msg = "삭제할 퀴즈폴더가 없습니다";
            mView.onFailDelQuizFolder( msg );
            return;
        }

        Integer userId = UserRepository.getInstance().getUserID();
        Integer quizFolderId = item.getId();
        mQuizFolderModel.delQuizFolder( userId, quizFolderId, onDelQuizFolder() );
    }

    private QuizFolderRepository.DelQuizFolderCallback onDelQuizFolder() {
        return new QuizFolderRepository.DelQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                mView.onSuccessDelQuizFolder(quizFolders);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg = "퀴즈폴더 삭제에 실패했습니다";
                switch (resultCode){
                    case QUIZFOLDER__NOALLOWED_DELETE_NEWBUTTON:
                        msg = "퀴즈폴더 만들기 버튼은 삭제할 수 없습니다.";
                        break;
                    case quizFolder__NOALLOWED_DELETE_PLAYING:
                        msg = "플레이중인 퀴즈폴더는 삭제할 수 없습니다. 다른 퀴즈폴더를 플레이용으로 지정한 후 삭제해주세요.";
                        break;
                }
                mView.onFailDelQuizFolder(msg);
            }
        };
    }
}
