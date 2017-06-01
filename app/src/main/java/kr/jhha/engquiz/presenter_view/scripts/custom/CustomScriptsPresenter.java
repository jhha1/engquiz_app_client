package kr.jhha.engquiz.presenter_view.scripts.custom;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.model.local.Script.STATE_DESCRIPTION;
import static kr.jhha.engquiz.model.local.Script.STATE_NONE;
import static kr.jhha.engquiz.model.local.Script.STATE_ADDED_SCRIPT;
import static kr.jhha.engquiz.model.local.Script.STATE_QUIZPLAYING_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

/**
 * Created by thyone on 2017-03-15.
 */

public class CustomScriptsPresenter implements CustomScriptsContract.ActionsListener {

    private final Context mContext;
    private final CustomScriptsContract.View mView;
    private final ScriptRepository mModel;

    public CustomScriptsPresenter(Context context, CustomScriptsContract.View view, ScriptRepository model ) {
        mModel = model;
        mView = view;
        mContext = context;
    }

    // toolbar option menu
    @Override
    public void helpBtnClicked() {
        FragmentHandler handler = FragmentHandler.getInstance();
        WebViewFragment fragment = (WebViewFragment)handler.getFragment(WEB_VIEW);
        fragment.setHelpWhat(FragmentHandler.EFRAGMENT.CUSTOM_SCRIPTS);
        handler.changeViewFragment(WEB_VIEW);
    }

    public void getScripts()
    {
        Integer[] scriptIds = mModel.getScriptIdAll();
        List<Integer> userMadeScriptIds = new ArrayList<>();
        if( scriptIds == null ) {
            // 여기를 탈 수 있는 케이스.
            // case 1.  앱 깔고 처음엔, 파싱 되었거나, 유저가 만든 스크립트가 없다 : 정상케이스
            // case 2.  유저가 파싱된 스크립트/직접만든스크립트를 정상적으로 앱에서 모두 삭제하면 스크립트가 없다. :정상
            // case 3.  모바일에서 앱 폴더(스크립트가 저장된)를 임의로 삭제했을때, 스크립트가 없다 : 비정상.
            // 3번 케이스는 무시해 에러메세지를 내보내지 않는다.
            // 1,2번만 고려해, 빈 스크립트 리스트로 보여준다.
            //
        } else {
            for(Integer id : scriptIds){
                if( mModel.isUserMadeScript(id) ){
                    userMadeScriptIds.add(id);
                }
            }
        }

        mView.onSuccessGetScrpits(userMadeScriptIds);
    }

    @Override
    public void listViewItemClicked(CustomScriptsAdapter.ScriptSummary item) {
        if( item == null ){
            return;
        }
        MyLog.d("title:" + item.scriptTitle);

        switch (item.state){
            case Script.STATE_NEWBUTTON:
                createScript();
                break;
            case STATE_QUIZPLAYING_SCRIPT:
            case STATE_ADDED_SCRIPT:
                // 스크립트 디테일 보기 화면전환
                mView.onShowSentences(item.scriptId, item.scriptTitle);
                break;
        }
    }

    @Override
    public void listViewItemLongClicked(CustomScriptsAdapter.ScriptSummary item) {
        if( item == null ){
            return;
        }
        MyLog.d("title:" + item.scriptTitle);

        switch (item.state){
            case Script.STATE_NEWBUTTON:
            case STATE_DESCRIPTION:
            case STATE_NONE:
                // nothing.  가이드 설명이 적힌 item 등.
                // 삭제 할 수 없는 아이템들이다.
                break;
            case STATE_QUIZPLAYING_SCRIPT:
            case STATE_ADDED_SCRIPT:
                mView.onShowOptionDialog(item);
                break;
        }
    }

    private void createScript(){
        // 스크립트추가
        new ScriptFactory(mContext)
                .create( new ScriptFactory.CreateScriptCallback()
                {
                    @Override
                    public void onSuccess(Script newScript) {
                        mView.onSuccessCreateScript(newScript);
                    }

                    @Override
                    public void onFail(ScriptFactory.CreateScriptResult result) {
                        mView.showErrorDialog(R.string.create_script__fail);
                    }}
                );

    }

    @Override
    public void deleteScript(CustomScriptsAdapter.ScriptSummary item)
    {
        if( item == null ) {
            mView.showErrorDialog( R.string.del_script__fail_no_exist_script );
            return;
        }
        if( STATE_QUIZPLAYING_SCRIPT == item.state ) {
            mView.showErrorDialog(R.string.del_script__fail_palying_state);
            return;
        }
        if( STATE_ADDED_SCRIPT != item.state ) {
            mView.showErrorDialog(R.string.cannot_remove_item);
            return;
        }

        // 유저가 만든 스크립트는 로컬파일에서 제거.
        final ScriptRepository scriptRepository = ScriptRepository.getInstance();
        boolean bOK = scriptRepository.removeScript(item.scriptId);
        if( ! bOK ){
            // 제거 실패
            mView.showErrorDialog(R.string.del_script__fail );
            return;
        } else {
            // 해당 스크립트가 소속되었던 폴더에서 스크립트 삭제
            final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
            quizFolderRepo.detachScriptFromAllFolder( item.scriptId );
            mView.onSuccessDelScript(item);
        }
    }

    @Override
    public void addScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item ){
        if( item == null ){
            mView.showErrorDialog(R.string.script_add_playing__fail_default);
            return;
        }

        final QuizPlayRepository quizPlayRepository = QuizPlayRepository.getInstance();
        boolean bOK = quizPlayRepository.addScript(item.scriptId);
        if( !bOK ){
            mView.showErrorDialog(R.string.script_add_playing__fail_default);
        } else {
            mView.onSuccessAddScriptIntoPlayList(item, R.string.script_add_playing__success);
        }
    }

    @Override
    public void delScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item ){
        if( item == null ){
            mView.showErrorDialog(R.string.script_del_playing__fail_default);
            return;
        }

        final QuizPlayRepository quizPlayRepository = QuizPlayRepository.getInstance();
        boolean bOK = quizPlayRepository.delScript(item.scriptId);
        if( !bOK ){
            mView.showErrorDialog(R.string.script_del_playing__fail_default);
        } else {
            mView.onSuccessDelScriptIntoPlayList(item, R.string.script_del_playing__success);
        }
    }
}
