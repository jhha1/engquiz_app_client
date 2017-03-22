package kr.jhha.engquiz.addscript;

import android.util.Log;

import java.util.Map;

import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.Script;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.quizplay.QuizPlayContract;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptPresenter implements AddScriptContract.ActionsListener {

    private final AddScriptContract.View mView;
    private final ScriptRepository mModel;

    public AddScriptPresenter(AddScriptContract.View view, ScriptRepository model ) {
        mModel = model;
        mView = view;
    }

    @Override
    public void addScript(String pdfFilePath, String pdfFileName ) {
        String msgForLog = "pdfFilePath:"+pdfFilePath+ ", pdfFileName:"+pdfFileName;
        mModel.addScript( pdfFilePath, pdfFileName, onAddScriptCallback(msgForLog) );
    }

    private ScriptRepository.ParseScriptCallback onAddScriptCallback(final String msgForLog) {
        return new ScriptRepository.ParseScriptCallback(){

            @Override
            public void onSuccess( Script script ) {
                Log.i("AppContent", "addScript onSuccess()  user: " + msgForLog);
                //mView.onSuccess();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "addScript onFail() UnkownERROR. user: " + msgForLog);
                //mView.onFail();
                // TODO 실패 메세지
            }
        };
    }
}
