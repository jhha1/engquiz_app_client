package kr.jhha.engquiz.z_legacy.quizfolder;

import java.util.List;

import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.util.exception.EResultCode;

import static kr.jhha.engquiz.model.local.QuizFolder.STATE_NEWBUTTON;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizFoldersPresenter implements QuizFoldersContract.ActionsListener {

    private final QuizFoldersContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;

    public final static int ERR_DEAULT = 1;
    public final static int ERR_NEWBUTTON = 2;
    public final static int ERR_NOEXSITED_SCRIPT = 3;
    public final static int ERR_NOEXSITED_FOLDER = 4;
    public final static int NOALLOWED_DELETE_PLAYING = 5;
    public final static int ERR_NET = 6;

    public QuizFoldersPresenter(QuizFoldersContract.View view, QuizFolderRepository model ) {
        mQuizFolderModel = model;
        mView = view;
    }

    public void getQuizFolderList() {
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
        if( QuizFolder.isNull(item)){
            mView.onFailChangePlayingQuizFolder(ERR_DEAULT);
            return;
        }

        if( item.getState() == STATE_NEWBUTTON ){
            mView.onFailChangePlayingQuizFolder(ERR_NEWBUTTON);
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
            mQuizFolderModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder(item) );
        }
    }

    private QuizFolderRepository.ChangePlayingQuizFolderCallback onChangePlayingQuizFolder(final QuizFolder playingQuizFolder) {
        return new QuizFolderRepository.ChangePlayingQuizFolderCallback(){

            @Override
            public void onSuccess( List<QuizFolder> uiSortedQuizFolders ) {
                // change folder of Memory
                /*
                final QuizPlayRepository quizPlayRepo = QuizPlayRepository.getInstance();
                EResultCode code = quizPlayRepo.initialize( playingQuizFolder.getId(),
                                                                            playingQuizFolder.getTitle(),
                                                                            playingQuizFolder.getScriptIds() );
                if(code != EResultCode.SUCCESS){
                    mView.onFailChangePlayingQuizFolder(ERR_DEAULT);
                    return;
                }
                mView.onSucessChangePlayingQuizFolder(uiSortedQuizFolders);*/
            }

            @Override
            public void onFail(EResultCode resultCode) {
                switch (resultCode){
                    case QUIZFOLDER__NOEXIST_SCRIPTS:
                        mView.onFailChangePlayingQuizFolder(ERR_NOEXSITED_SCRIPT);
                        break;
                    case QUIZFOLDER__NOEXIST_QUIZFOLDERID:
                    default:
                        mView.onFailChangePlayingQuizFolder(ERR_DEAULT);
                        break;
                }
            }
        };
    }

    private void loadQuizFolderScriptIdsAndChangePlayingQuizFolder(final QuizFolder item )
    {
        mQuizFolderModel.getScriptsInFolder(
                item.getId(),
                new QuizFolderRepository.GetQuizFolderScriptListCallback() {
                        @Override
                        public void onSuccess(List<Integer> quizFolderScripts) {
                            mQuizFolderModel.changePlayingQuizFolder( item, onChangePlayingQuizFolder(item) );
                        }

                        @Override
                        public void onFail(EResultCode resultCode) {
                            mView.onFailChangePlayingQuizFolder(ERR_NOEXSITED_SCRIPT);
                        }
                });
    }

    @Override
    public void delQuizFolder( QuizFolder item ){
        if( item == null ) {
            mView.onFailDelQuizFolder( ERR_NOEXSITED_FOLDER );
            return;
        }

        if( item.getState() == STATE_NEWBUTTON ){
            mView.onFailDelQuizFolder(ERR_NEWBUTTON);
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
                switch (resultCode){
                    case QUIZFOLDER__NOALLOWED_DELETE_NEWBUTTON:
                        mView.onFailDelQuizFolder(ERR_NEWBUTTON);
                        break;
                    case quizFolder__NOALLOWED_DELETE_PLAYING:
                        mView.onFailDelQuizFolder(NOALLOWED_DELETE_PLAYING);
                        break;
                    case NETWORK_ERR:
                        mView.onFailDelQuizFolder(ERR_NET);
                        break;
                    default:
                        mView.onFailDelQuizFolder(ERR_DEAULT);
                        break;
                }
            }
        };
    }
}
