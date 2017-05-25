package kr.jhha.engquiz.presenter_view.sentences;

import android.content.Context;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.presenter_view.scripts.custom.ScriptFactory;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentencePresenter implements AddSentenceContract.ActionsListener {

    private Context mContext;

    private final AddSentenceContract.View mView;
    private final ScriptRepository mScriptModel;

    private String mSentenceKo;
    private String mSentenceEn;

    public AddSentencePresenter(Context context, AddSentenceContract.View view, ScriptRepository model ) {
        mView = view;
        mScriptModel = model;
        mContext = context;
    }

    // toolbar option menu
    @Override
    public void helpBtnClicked() {
        FragmentHandler handler = FragmentHandler.getInstance();
        WebViewFragment fragment = (WebViewFragment)handler.getFragment(WEB_VIEW);
        fragment.setHelpWhat(FragmentHandler.EFRAGMENT.ADD_SENTENCE);
        handler.changeViewFragment(WEB_VIEW);
    }

    @Override
    public void sentencesInputted( boolean bHasParentScript, Integer parentScriptId,
                                   String ko, String en )
    {
        if( ! checkSentences(ko, en) ) {
            mView.showErrorDialog(7);
            return;
        }

        mSentenceKo = ko;
        mSentenceEn = en;

        if( bHasParentScript )
        {
            Script script = checkHasParentScript( parentScriptId );
            if( Script.isNull(script) ) {
                mView.showErrorDialog(2);
                return;
            }
            createSentence( script );
        }
        else
        {
            List<String> scriptTitleAll = mScriptModel.getUserMadeScriptTitleAll();
            if( scriptTitleAll == null || scriptTitleAll.isEmpty() ){
                makeNewScriptBtnClicked();
            } else {
                String[] titleArray = scriptTitleAll.toArray(new String[scriptTitleAll.size()]);
                mView.showDialogSelectScript(titleArray);
            }
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

    private Script checkHasParentScript( Integer parentScriptId )
    {
        if( false == Script.checkScriptID(parentScriptId) ){
            MyLog.e("Invalid Script. id:"+parentScriptId);
            return null;
        }
        return mScriptModel.getScript( parentScriptId );
    }

    @Override
    public void makeNewScriptBtnClicked()
    {
        new ScriptFactory(mContext).create( new ScriptFactory.CreateScriptCallback() {
                @Override
                public void onSuccess(Script newScript) {
                    createSentence( newScript );
                }

                @Override
                public void onFail(ScriptFactory.CreateScriptResult result) {
                    mView.showErrorDialog(R.string.create_script__fail);
            }});
    }

    @Override
    public void scriptSelected(String scriptName){
        Integer scriptId = mScriptModel.getScriptIdByTitle(scriptName);
        Script script = mScriptModel.getScript(scriptId);
        createSentence( script );
    }

    private void createSentence( Script script )
    {
        // Make Sentence
        Sentence newSentence = new Sentence();
        newSentence.textEn = mSentenceEn;
        newSentence.textKo = mSentenceKo;
        newSentence.state = Sentence.STATE_MADE_BY_USER;

        newSentence.scriptId = script.scriptId;
        newSentence.sentenceId = Sentence.makeSenetenceId( script.scriptId, script.sentences );
        script.sentences.add( newSentence );

        mView.showAddSentenceSuccessDialog( script.title );
    }
}
