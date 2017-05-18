package kr.jhha.engquiz.presenter_view.sentences;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.model.local.Script;
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
                mView.onSuccessGetSentences(sentences);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetSentences();
            }
        };
    }

    private String getStringView(List<Sentence> sentences){
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
        // 유저가 작성한 문장은 수정 가능
        if( mScriptModel.isUserMadeScript(mScriptId) ){
            mView.showModifyDialog(item);
            return;
        } else {
            // 정규 스크립트는 개발자에게 문장수정요청
            mView.showSendReportDialog();
        }
    }

    @Override
    public void sentenceLongClicked(Sentence item) {
        mSelectedSentence = item;
        // 유저가 작성한 문장은 삭제 가능
        if( mScriptModel.isUserMadeScript(mScriptId) ) {
            Script script = mScriptModel.getScript(mScriptId);
            if( script == null )
                return;
            if( script.sentences == null || script.sentences.isEmpty() )
                return;

            if( script.sentences.size() <= 1 )
                mView.onFailDeleteSentence(R.string.sentence__del_fail_sentence_min_count);
             else
                mView.showDeleteDialog();
        }
    }


    // 정규 스크립트 문장 - 문장 수정 요청
    @Override
    public void sendReport() {
        if( mSelectedSentence.isMadeByUser() ) {
            // 유저가 직접 만든 문장은, 문장수정 요청을 개발자에게 보낼 수 없다.
            mView.onFailSendReport(R.string.report__send_fail_custom_sentence);
            return;
        }
        final ReportRepository reportRepo = ReportRepository.getInstance();
        reportRepo.sendReport(mSelectedSentence, onSendReportCallback());
    }

    private ReportRepository.ReportCallback onSendReportCallback() {
        return new ReportRepository.ReportCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessSendReport();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                MyLog.e("onFailSendReport() code: " + resultCode.stringCode());
                mView.onFailSendReport(R.string.report__send_fali);
            }
        };
    }

    // 유저가 만든 문장 - 직접 수정
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
        scriptRepo.deleteSentence(mSelectedSentence, onDeleteSentence(mSelectedSentence));
    }

    private ScriptRepository.DeleteSenteceCallback onDeleteSentence(final Sentence sentence) {
        return new ScriptRepository.DeleteSenteceCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessDeleteSentence();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailDeleteSentence(R.string.sentence__del_fail);
            }
        };
    }
}
