package kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class SentencePresenter implements SentenceContract.ActionsListener {

    private final SentenceContract.View mView;
    private final ScriptRepository mScriptModel;

    private Integer mScriptId;
    private Sentence mSelectedSentence;

    public SentencePresenter(SentenceContract.View view, ScriptRepository model ) {
        mScriptModel = model;
        mView = view;
    }

    @Override
    public void initToolbarTitle(Integer scriptID) {
        String title = mScriptModel.getScriptTitleById(scriptID);
        if(StringHelper.isNull(title)){
            title = "Sentences";
        }
        mView.showTitle(title);
    }

    @Override
    public void getSentences(Integer scriptId) {
        MyLog.d( "scriptId:"+scriptId);
        mScriptId = scriptId;
        mScriptModel.getSentencesByScriptId(scriptId, onGetSentence());
    }

    private ScriptRepository.GetSentenceListCallback onGetSentence() {
        return new ScriptRepository.GetSentenceListCallback(){

            @Override
            public void onSuccess(List<Sentence> sentences) {

                if( mScriptModel.isUserMadeScript(mScriptId) ){
                    mView.onSuccessGetSentences(sentences);
                } else {
                    mView.onSuccessGetSentences(getView(sentences));
                }
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

    @Override
    public void sentenceDoubleClicked(Sentence item) {
        mSelectedSentence = item;
        mView.showModifyDialog(item);
    }

    @Override
    public void sentenceLongClicked(Sentence item) {
        mSelectedSentence = item;
        mView.showDeleteDialog();
    }

    @Override
    public void modifySentence(String ko, String en) {
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();

        Sentence sentence = new Sentence();
        sentence.scriptId = mSelectedSentence.scriptId;
        sentence.sentenceId = mSelectedSentence.sentenceId;
        sentence.src = mSelectedSentence.src;
        sentence.textKo = ko;
        sentence.textEn = en;
        scriptRepo.updateSentence(sentence, onUpdateSentence());
    }

    private ScriptRepository.UpdateSenteceCallback onUpdateSentence() {
        return new ScriptRepository.UpdateSenteceCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessUpdateSentence();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailUpdateSentence();
            }
        };
    }

    @Override
    public void deleteSentence() {
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        scriptRepo.deleteSentence(mSelectedSentence, onDeleteSentence());
    }

    private ScriptRepository.DeleteSenteceCallback onDeleteSentence() {
        return new ScriptRepository.DeleteSenteceCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessDeleteSentence();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailDeleteSentence();
            }
        };
    }
}
