package kr.jhha.engquiz.presenter_view.quizfolder;

import java.util.List;

import kr.jhha.engquiz.model.local.QuizFolder;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizFoldersContract {

    interface View {
        // 퀴즈폴더 리스트 가져오기 결과
        void onSuccessGetQuizFolderList(List<QuizFolder> quizFolders);
        void onFailGetQuizFolderList();

        // 퀴즈 폴더 추가 화면으로 전환
        void onChangeFragmetNew();
        // 퀴즈 폴더 디테일 보기 화면전환
        void onChangeFragmetFolderDetail( Integer QuizFolderId, String title );

        // 이 퀴즈폴더를 퀴즈플레이용으로 설정 관련
        void showDialogChangePlayingQuizFolder(QuizFolder mListviewSelectedItem );
        void onSucessChangePlayingQuizFolder();
        void onFailChangePlayingQuizFolder(int reason);

        // 퀴즈 폴더 삭제 결과
        void onSuccessDelQuizFolder(List<QuizFolder> updatedQuizFolders);
        void onFailDelQuizFolder(String msg );
    }

    interface ActionsListener {
        // 퀴즈폴더 리스트 가져오기
        void getQuizFolderList();

        // 퀴즈폴더 클릭에 따른 로직 타기
        void listViewItemClicked( QuizFolder item );
        void listViewItemDoubleClicked( QuizFolder item );

        // 이 퀴즈폴더를 퀴즈플레이용으로 설정
        void changePlayingQuizFolder( QuizFolder item );

        // 퀴즈 폴더 삭제
        void delQuizFolder( QuizFolder item );
    }
}
