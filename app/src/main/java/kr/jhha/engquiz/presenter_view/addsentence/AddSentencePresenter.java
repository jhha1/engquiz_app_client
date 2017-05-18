package kr.jhha.engquiz.presenter_view.addsentence;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentencePresenter implements AddSentenceContract.ActionsListener {

    private final AddSentenceContract.View mView;
    private final ScriptRepository mScriptModel;

    private String mSentenceKo;
    private String mSentenceEn;

    private boolean mIsNewScript;
    private String mScriptName = null; // 선택된 파일 이름

    public AddSentencePresenter(AddSentenceContract.View view, ScriptRepository model ) {
        mView = view;
        mScriptModel = model;
    }

    @Override
    public void sentencesInputted(String ko, String en)
    {
        if( checkSentences(ko, en) ){
            mSentenceKo = ko;
            mSentenceEn = en;

            List<String> scriptTitleAll = mScriptModel.getUserMadeScriptTitleAll();
            if( scriptTitleAll == null || scriptTitleAll.isEmpty() ){
                mView.showNeedMakeScriptDialog();
            } else {
                String[] titleArray = scriptTitleAll.toArray(new String[scriptTitleAll.size()]);
                mView.showDialogSelectScript(titleArray);
            }

        } else {
            //mView.
            mView.showErrorDialog(7);
        }
    }

    private boolean checkSentences(String ko, String en){
        if(StringHelper.isNull(ko)) {
            return false;
        }
        if(StringHelper.isNull(en)) {
            return false;
        }
        return true;
    }


    @Override
    public void makeNewScriptBtnClicked(){
        mView.showDialogMakeNewScript();
    }

    @Override
    public void makeNewScript(String scriptName) {
        Integer scriptId = mScriptModel.getScriptIdByTitle(scriptName);
        boolean bExistScript = (scriptId > 0);
        if (bExistScript) {
            mView.showDialogMakeNewScript_ReInput();
            return;
        }

        mIsNewScript = true;
        mScriptName = scriptName;
        postProcess();
    }

    @Override
    public void scriptSelected(String scriptName){
        mIsNewScript = false;
        mScriptName = scriptName;
        postProcess();
    }

    private void postProcess()
    {
        // Make Sentence
        Sentence newSentence = new Sentence();
        newSentence.textEn = mSentenceEn;
        newSentence.textKo = mSentenceKo;
        newSentence.src = Sentence.SRC_USER;

        // Add Sentence Into Script
        Script script = null;
        if(mIsNewScript) {
            script = new Script();
            script.title = mScriptName;
            script.scriptId = Script.createCustomScriptID();
        } else {
            Integer scriptId = mScriptModel.getScriptIdByTitle(mScriptName);
            script = mScriptModel.getScript(scriptId);
        }

        newSentence.scriptId = script.scriptId;
        newSentence.sentenceId = Sentence.makeSenetenceId( script.scriptId, script.sentences );
        script.sentences.add( newSentence );
        mScriptModel.addUserCustomScript(script);

        mView.showAddSentenceSuccessDialog(mScriptName);
    }
}
