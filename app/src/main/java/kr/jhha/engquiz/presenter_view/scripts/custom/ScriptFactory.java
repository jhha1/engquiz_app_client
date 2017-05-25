package kr.jhha.engquiz.presenter_view.scripts.custom;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.scripts.custom.ScriptFactory.CreateScriptResult.Duplicated;
import static kr.jhha.engquiz.presenter_view.scripts.custom.ScriptFactory.CreateScriptResult.StringLength;
import static kr.jhha.engquiz.presenter_view.scripts.custom.ScriptFactory.CreateScriptResult.Success;


/**
 * Created by thyone on 2017-05-20.
 */

public class ScriptFactory {

    public interface CreateScriptCallback {
        void onSuccess( Script newScript );
        void onFail(CreateScriptResult result);
    }

    private Script mNewScript;

    private Context mContext;

    private static String mTitle = "새 스크립트의 제목을 입력해주세요.";
    private static String mDuplicatedMsg = "이미 똑같은 이름의 스크립트가 있어요. 다른 이름을 지어주세요.";
    private static String mNameLengthMsg = "최소 1자 이상 , 30자 이하로 입력하세요.";

     public enum CreateScriptResult {
        Success, Duplicated, StringLength
    }
    public static int MAX_SCRIPT_NAME_LEN = 30;

    public ScriptFactory(Context context){
        mContext = context;
    }

    public void create(final CreateScriptCallback callback){
        create(mTitle, callback);
    }

    public void create(String title, final CreateScriptCallback callback){
        showDialog(title, new CreateScriptCallback() {
            @Override
            public void onSuccess(Script newScript) {
                callback.onSuccess(newScript);
            }

            @Override
            public void onFail(CreateScriptResult result) {
                // 중복 / 미 입력이면 재입력 받도록 다시 다이알로그를 띄움
                if( result == Duplicated ){
                    create(mDuplicatedMsg, callback );
                } else if ( result == StringLength){
                    create(mNameLengthMsg, callback );
                } else {
                    callback.onFail(result);
                }
            }
        });
    }

    private void showDialog(final String title, final CreateScriptCallback callback)
    {
        final EditText input = Etc.makeEditText(mContext);
        final MyDialog dialog = new MyDialog(mContext);
        dialog.setTitle(title);
        dialog.setEditText(input);
        dialog.setPositiveButton(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss();

                String inputScriptName = input.getText().toString();
                CreateScriptResult result = createScriptImpl(inputScriptName);
                if( result != Success ){
                    callback.onFail(result);
                } else {
                    callback.onSuccess(mNewScript);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.showUp();
    }

    private CreateScriptResult createScriptImpl(String scriptName)
    {
        if(StringHelper.isNull(scriptName)
                || scriptName.length() > MAX_SCRIPT_NAME_LEN){
            return StringLength;
        }

        final ScriptRepository scriptRepository = ScriptRepository.getInstance();
        Integer scriptId = scriptRepository.getScriptIdByTitle(scriptName);
        boolean bExistScript = (scriptId > 0);
        if (bExistScript) {
            return Duplicated;
        }

        mNewScript = new Script();
        mNewScript.title = scriptName;
        mNewScript.scriptId = Script.createCustomScriptID();

        scriptRepository.addUserCustomScript(mNewScript);

        return Success;

    }
}
