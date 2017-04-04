package kr.jhha.engquiz.quizfolder;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.UserModel;
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
        Log.i("AppContent", "ShowQuizFoldersPresenter getQuizFolders() called");
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
        Log.d("%%%%%%%%%%%%%%%", "ShowQuizFoldersFragment.listViewItemClicked. title:" + title);

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
        Log.i("AppContent", "ShowQuizFoldersPresenter changePlayingQuizFolder() called. item:"+((item!=null)?item.toString():null));
        if( QuizFolder.isNull(item)){
            mView.onFailChangePlayingQuizFolder(1);
            return;
        }

        if( item.getState() == STATE_NEWBUTTON ){
            mView.onFailChangePlayingQuizFolder(2);
            return;
        }

        // 1.퀴즈폴더를 플레이용으로 변경위해서는,
        //  퀴즈폴더 내 script id 들이 필요하다.
        // 2. script id들은 퀴즈폴더 상세보기를 클릭했을때 load 되도록 설계되어있다.
        // 만약, 유저가 퀴즈폴더 상세보기를 안하고 플레이용으로 변경시도한다면 1,2의 조건에 위배.
        // 이 부분 커버위해, script Id가 없으면 load.
        List<Integer> scriptIDs = item.getScriptIds();
        boolean bQuizFolderDetailNotLoaded = (scriptIDs == null || scriptIDs.isEmpty());
        if( bQuizFolderDetailNotLoaded ){
            loadQuizFolderScriptIdsAndChangePlayingQuizFolder(item);
        } else {
            final QuizPlayModel quizPlayModel = QuizPlayModel.getInstance();
            quizPlayModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder() );
        }
    }

    private QuizPlayModel.ChangePlayingQuizFolderCallback onChangePlayingQuizFolder() {
        return new QuizPlayModel.ChangePlayingQuizFolderCallback(){

            @Override
            public void onSuccess() {
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
        final Integer userId = UserModel.getInstance().getUserID();
        mQuizFolderModel.getQuizFolderDetail(
                userId,
                item.getId(),
                new QuizFolderRepository.GetQuizFolderDetailCallback() {
                        @Override
                        public void onSuccess(List<Integer> quizFolderScripts) {
                            final QuizPlayModel quizPlayModel = QuizPlayModel.getInstance();
                            quizPlayModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder() );
                        }

                        @Override
                        public void onFail(EResultCode resultCode) {
                            mView.onFailChangePlayingQuizFolder(3);
                        }
                });
    }

    @Override
    public void delQuizFolder( QuizFolder item ){
        Log.i("AppContent", "ShowQuizFoldersPresenter delQuizFolderScript() called");

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
                mView.onSuccessDelQuizFolder(quizFolders);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg = "퀴즈폴더 삭제에 실패했습니다";
                mView.onFailDelQuizFolder(msg);
            }
        };
    }
}
