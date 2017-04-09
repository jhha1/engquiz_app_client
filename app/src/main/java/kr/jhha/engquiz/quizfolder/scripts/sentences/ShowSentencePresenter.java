package kr.jhha.engquiz.quizfolder.scripts.sentences;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.quizfolder.scripts.QuizFolderScriptsAdapter;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowSentencePresenter implements ShowSentenceContract.ActionsListener {

    private final ShowSentenceContract.View mView;
    private final ScriptRepository mScriptModel;

    public ShowSentencePresenter(ShowSentenceContract.View view, ScriptRepository model ) {
        mScriptModel = model;
        mView = view;
    }

    @Override
    public void getSentences(Integer scriptId) {
        Log.i("AppContent", "ShowSentencePresenter getSentences() called. scriptId:"+scriptId);
        mScriptModel.getSentencesByScriptId(scriptId, onGetSentence());
    }

    private ScriptRepository.GetSentenceListCallback onGetSentence() {
        return new ScriptRepository.GetSentenceListCallback(){

            @Override
            public void onSuccess(List<Sentence> sentences) {
                mView.onSuccessGetSentences(getView(sentences));
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetSentences();
            }
        };
    }

    private String getView(List<Sentence> sentences){
        StringBuilder sb = new StringBuilder();

        for(Sentence sentence : sentences){
            sb.append("\n");
            sb.append(sentence.textKo +"\n");
            sb.append(sentence.textEn +"\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }
}
