package kr.jhha.engquiz.presenter_view.scripts.regular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.SyncRepository;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.model.local.Script.STATE_DESCRIPTION;
import static kr.jhha.engquiz.model.local.Script.STATE_NONE;
import static kr.jhha.engquiz.model.local.Script.STATE_NON_ADDED_SCRIPT;
import static kr.jhha.engquiz.model.local.Script.STATE_ADDED_SCRIPT;
import static kr.jhha.engquiz.model.local.Script.STATE_QUIZPLAYING_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

/**
 * Created by thyone on 2017-03-15.
 */

public class RegularScriptsPresenter implements RegularScriptsContract.ActionsListener {

    private final RegularScriptsContract.View mView;
    private final ScriptRepository mModel;

    private final String mDefaultPDFDir;
    public static float DEFAUT_FILE_UPDOWN_SIZE = 0.2f;

    public RegularScriptsPresenter(RegularScriptsContract.View view, ScriptRepository model ) {
        mModel = model;
        mView = view;
        mDefaultPDFDir = mModel.getAbsoluteFilePath(FileHelper.KaKaoDownloadFolder_AndroidPath);
    }

    public void init()
    {
        // Sync Floating button
        boolean bSyncNeed = ( SyncRepository.getInstance().getSyncNeededCount() > 0 );
        if( bSyncNeed ){
            mView.showSyncFloatingBtn();
        } else {
            mView.hideSyncFloatingBtn();
        }

        Integer[] scriptIds = mModel.getScriptIdAll();
        List<Integer> parsedScriptIds = new ArrayList<>();
        List<String> notAddedPDFScrpitIds = new ArrayList<>();
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
                if( false == mModel.isUserMadeScript(id) ){
                    parsedScriptIds.add(id);
                }
            }
        }

        String extension = ".pdf";
        final FileHelper fileHelper = FileHelper.getInstance();
        Map<String, List> files = fileHelper.loadFileListInDirectory(FileHelper.KaKaoDownloadFolder_AndroidPath, extension);
        if( files.isEmpty() ){

        } else {
            String btnGoParentDir = "../";
            List<String> pdfFileNames = files.containsKey("file")? files.get("file") : Collections.EMPTY_LIST;
            for( String filename : pdfFileNames ){
                String scriptName = Script.fileName2ScriptTitle(filename);
                boolean bParsedScript = mModel.hasParsedScript(scriptName);
                boolean isBtnGoParentDir = filename.equals(btnGoParentDir);
                if( false == bParsedScript && false == isBtnGoParentDir ){
                    notAddedPDFScrpitIds.add( filename );
                }
            }
        }

        mView.onSuccessGetScrpits(parsedScriptIds, notAddedPDFScrpitIds);
    }

    // toolbar option menu
    @Override
    public void helpBtnClicked() {
        FragmentHandler handler = FragmentHandler.getInstance();
        WebViewFragment fragment = (WebViewFragment)handler.getFragment(WEB_VIEW);
        fragment.setHelpWhat(FragmentHandler.EFRAGMENT.REGULAR_SCRIPTS);
        handler.changeViewFragment(WEB_VIEW);
    }

    @Override
    public void listViewItemClicked(RegularScriptsAdapter.ScriptSummary item) {
        if( item == null ){
            return;
        }
        MyLog.d("title:" + item.scriptTitle);

        switch (item.state){
            case Script.STATE_NEWBUTTON:
                // 스크립트추가 화면 전환
                mView.onShow_ParseScrpitFragment();
                break;
            case STATE_DESCRIPTION:
            case STATE_NONE:
            case STATE_NON_ADDED_SCRIPT:
                // nothing.  가이드 설명이 적힌 item, 파싱안된 pdf 파일 리스트 등.
                break;
            case STATE_QUIZPLAYING_SCRIPT:
            case STATE_ADDED_SCRIPT:
                // 스크립트 디테일 보기 화면전환
                mView.onShow_ShowSentencesFragment(item.scriptId, item.scriptTitle);
                break;
        }
    }

    @Override
    public void listViewItemLongClicked(RegularScriptsAdapter.ScriptSummary item) {
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
            case STATE_NON_ADDED_SCRIPT:
                addScriptProcess(item);
                break;
            case STATE_QUIZPLAYING_SCRIPT:
            case STATE_ADDED_SCRIPT:
                mView.onShowOptionDialog(item);
                break;
        }
    }

    private void addScriptProcess(RegularScriptsAdapter.ScriptSummary item){
        MyLog.e("item.scriptTitlel:"+ item.scriptTitle);
        final String filename = item.scriptTitle;
        if( false == mModel.checkFileFormat(filename) ) { // 파일 형식 체크
            mView.showErrorDialog(R.string.add_pdf_script__fail_only_allow_pdf_format);
            return;
        }

        if( mModel.checkDoubleAdd(filename) ){
            String reAddedTitle = item.scriptTitle;
            String addedTitle  = mModel.getFileNameRemovedDoubleDownloadTagAndPDFExtension(filename);
            String errmsg = "이미 추가된 스크립트에요.\n\n\n"
                        + "# 추가 되어있는 스크립트 : \n"
                        + addedTitle +"\n\n"
                        + "# 현재 추가하려는 스크립트 :\n"
                        + reAddedTitle;
            mView.showErrorDialog(errmsg);
        } else {
            showFileSizeConfirmDialog(item);
        }
    }

    private void showFileSizeConfirmDialog(RegularScriptsAdapter.ScriptSummary item){
        String fileName = item.scriptTitle;
        final FileHelper file = FileHelper.getInstance();
        float filesize = file.getFileMegaSize( mDefaultPDFDir, fileName ) * 2;  // *2 == upload and download
        mView.showAddScriptConfirmDialog(fileName, filesize, item);
    }

    @Override
    public void addScript( String pdfFileName, RegularScriptsAdapter.ScriptSummary item)
    {
        String msgForLog = "pdfFileName:"+pdfFileName;

        mView.showLoadingDialog();
        Integer userId = UserRepository.getInstance().getUserID();
        mModel.addRegularScript( userId,
                mDefaultPDFDir,
                pdfFileName,
                onAddScriptCallback( msgForLog, item ) );
        mView.closeLoadingDialog();
    }

    private ScriptRepository.ParseScriptCallback onAddScriptCallback(
                                                    final String msgForLog,
                                                    final RegularScriptsAdapter.ScriptSummary item)
    {
        return new ScriptRepository.ParseScriptCallback(){

            @Override
            public void onSuccess( Script script ) {
                MyLog.d("addRegularScript onSuccess()  user: " + msgForLog);
                mView.showAddScriptSuccessDialog(item, script);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                MyLog.d("addRegularScript onFail() " +
                        "resultCode:"+resultCode.toString()+", user: " + msgForLog);
                switch (resultCode) {
                    case SCRIPT__NO_HAS_KR_OR_EN:
                        mView.showErrorDialog(R.string.add_pdf_script__fail_only_has_en_or_kn);
                        break;
                    default:
                        int msgId = EResultCode.commonMsgHandler(resultCode, R.string.common__fail_retry);
                        mView.showErrorDialog(msgId);
                        break;
                }
            }
        };
    }

    @Override
    public void deleteScript(RegularScriptsAdapter.ScriptSummary item)
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

        // 정규 스크립트는 서버에서 유저 having을 detach.
        // 서버에서 folder having도 detach 시켜줌.
        Integer scriptId = item.scriptId;
        mModel.deleteRegularScript(scriptId, onDelScript(item) );
    }

    private ScriptRepository.DeleteScriptCallback onDelScript(final RegularScriptsAdapter.ScriptSummary item) {
        return new ScriptRepository.DeleteScriptCallback(){

            @Override
            public void onSuccess() {
                // 해당 스크립트가 소속되었던 폴더에서 스크립트 삭제
                final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
                quizFolderRepo.detachScriptFromAllFolder( item.scriptId );
                mView.onSuccessDelScript(item);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                int msgId = EResultCode.commonMsgHandler(resultCode, R.string.del_script__fail);
                mView.showErrorDialog(msgId);
            }
        };
    }

    @Override
    public void addScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item ){
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
    public void delScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item ){
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
