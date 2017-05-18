package kr.jhha.engquiz.presenter_view.scripts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;

import static kr.jhha.engquiz.model.local.Script.STATE_DESCRIPTION;
import static kr.jhha.engquiz.model.local.Script.STATE_NEWBUTTON;
import static kr.jhha.engquiz.model.local.Script.STATE_NONE;
import static kr.jhha.engquiz.model.local.Script.STATE_NON_PARSED_SCRIPT;
import static kr.jhha.engquiz.model.local.Script.STATE_NORMAL_SCRIPT;

/**
 * Created by thyone on 2017-03-30.
 */

public class ScriptsAdapter extends BaseAdapter {

    private Context mContext;
    private ScriptRepository mScriptModel;

    List<Integer> mParsedScriptIds;
    List<Integer> mUserMadeScriptIds;
    List<String> mNotAddedPDFScrpitIds;

    public class ScriptSummary {
        Integer scriptId;
        String scriptTitle;
        Integer state;
    }
    private List<ScriptSummary> mListView;

    public ScriptsAdapter(Context context, ScriptRepository scriptRepository, List<Integer> parsedScriptIds, List<Integer> userMadeScriptIds, List<String> notAddedPDFScrpitIds) {
        mContext = context;
        mScriptModel = scriptRepository;
        mParsedScriptIds = parsedScriptIds;
        mUserMadeScriptIds = userMadeScriptIds;
        mNotAddedPDFScrpitIds = notAddedPDFScrpitIds;
        initListView(parsedScriptIds, userMadeScriptIds, notAddedPDFScrpitIds);
    }

    private void initListView(List<Integer> parsedScriptIds, List<Integer> userMadeScriptIds, List<String> notAddedPDFScrpitIds)
    {
        mListView = new ArrayList<>();

        // custom scripts
        int stringID = R.string.show_scripts__custom_script_desc;
        int itemCnt = userMadeScriptIds.size();
        mListView.add( makeDescription(stringID, itemCnt) );
        List<ScriptSummary> list = convertFormat(userMadeScriptIds);
        if( list.isEmpty() )
            mListView.add( makeDescription(R.string.show_scripts__empty_scripts) );
        else
            mListView.addAll(convertFormat(userMadeScriptIds));

        // parsed scripts
        stringID = R.string.show_scripts__parsed_script_desc;
        itemCnt = parsedScriptIds.size();
        mListView.add( makeDescription(stringID, itemCnt) );
        list = convertFormat(parsedScriptIds);
        if( list.isEmpty() )
            mListView.add( makeDescription(R.string.show_scripts__empty_scripts) );
        else
            mListView.addAll(convertFormat(parsedScriptIds));

        // non parsed scripts
        stringID = R.string.show_scripts__nonparsed_script_desc;
        itemCnt = notAddedPDFScrpitIds.size();
        mListView.add( makeDescription(stringID, itemCnt) );
        mListView.add( makeNewButton() ); // 새 스크립트 추가 버튼
        list = convertFormatFile(notAddedPDFScrpitIds);
        if( list.isEmpty() )
            mListView.add( makeDescription(R.string.show_scripts__empty_scripts) );
        else
            mListView.addAll(convertFormatFile(notAddedPDFScrpitIds));
    }

    /*
        Adapter에 사용되는 데이터의 개수를 리턴
    */
    @Override
    public int getCount() {
        return mListView.size();
        //return mQuizFolderModel.getQuizFolderScriptsCount( mQuizFolderID );
    }

    /*
        position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( mContext == null )
            mContext = parent.getContext();

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ScriptSummary script = mListView.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_quizfolder_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.quizfolder_listview_icon);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.quizfolder_listview_text);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(getIcon(script.state, script.scriptTitle));
        titleTextView.setText(script.scriptTitle);
        decoTitle(titleTextView, script.state);
        return convertView;
    }

    private Drawable getIcon(Integer state, String title){
        int resourceID;
        switch (state){
            case STATE_NONE:
                resourceID = R.drawable.presence_invisible;
                break;
            case STATE_NORMAL_SCRIPT:
                resourceID = R.drawable.presence_online;
                break;
            case STATE_NEWBUTTON:
                resourceID = R.drawable.ic_script__add_green;
                break;
            case STATE_NON_PARSED_SCRIPT:
                resourceID = R.drawable.presence_invisible;
                break;
            case STATE_DESCRIPTION:
                resourceID = R.drawable.ic_script__subject_orange;
                break;
            default:
                resourceID = R.drawable.presence_invisible;
                break;
        }
        Drawable icon = ContextCompat.getDrawable(mContext, resourceID);

        return icon;
    }

    private void decoTitle(TextView titleTextView, Integer state){
        final Context context = mContext.getApplicationContext();
        if( state == STATE_DESCRIPTION){
            int color = ContextCompat.getColor(context, R.color.colorAccent);
            titleTextView.setTextColor(color);
            titleTextView.setText(Html.fromHtml(titleTextView.getText().toString()));
        } else if( state == STATE_NEWBUTTON) {
            int color = ContextCompat.getColor(context, R.color.PlayQuizGreen);
            titleTextView.setTextColor(color);
        } else {
            int color = ContextCompat.getColor(context, R.color.black_alpha_70);
            titleTextView.setTextColor(color);
        }
    }

    /*
       지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
   */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
       지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
   */
    @Override
    public Object getItem(int position) {
        // model의 summary list 배열 순서대로 UI listveiw에 뿌려지게 된다.
        // so, listview의 position은 arraylist의 index다.
        return mListView.get(position);
    }

    // 정규 스크립트 추가 (스크립트 파싱)
    public void addItem( ScriptsAdapter.ScriptSummary selectedItem, Script newScript )
    {
        // 유저가 만든 스크립트가 아니어야 한다
        if( ! mScriptModel.isUserMadeScript( selectedItem.scriptId ) )
        {
            // 파싱된 pdf 스크립트 리스트에 추가
            // 1. 새로 만들어진 스크립트에 서버로부터 부여받은 스크립트ID가 있다.
            // 2. 첫번째 인덱스에 넣어 리프레쉬 후 유저에게 바로 인지되도록 한다.
            //      : 이 후  화면 나갔다 돌아오면 서버에서 sorting한 리스트로 다시 뿌린다.
            mParsedScriptIds.add( 0, newScript.scriptId );
            // 미 파싱된 pdf 스크립트 리스트에서 제거
            String addedFileName = selectedItem.scriptTitle;
            for(String pdfName : mNotAddedPDFScrpitIds){
                if( pdfName.equals( addedFileName ) ){
                    mNotAddedPDFScrpitIds.remove(pdfName);
                    break;
                }
            }
        }

        initListView(mParsedScriptIds, mUserMadeScriptIds, mNotAddedPDFScrpitIds);
    }

    // 스크립트 삭제.
    public void delItem(ScriptsAdapter.ScriptSummary item)
    {
        int delScriptId = item.scriptId;
        // 유저가 만든 스크립트면,
        if( mScriptModel.isUserMadeScript(delScriptId) )
        {
            for(Integer id : mUserMadeScriptIds){
                if( id == delScriptId ) {
                    // 유저가 만든 스크립트 리스트에서 제거
                    mUserMadeScriptIds.remove(id);
                    break;
                }
            }
        }
        // 정규 스크립트면,
        else
        {
            for(Integer id : mParsedScriptIds){
                if( id == delScriptId ){
                    // 파싱된 스크립트 리스트에서 제거
                    mParsedScriptIds.remove(id);
                    // 미 파싱된 pdf 스크립트 리스트에 추가
                    // 첫번째 인덱스에 넣어 리프레쉬 후 유저에게 바로 인지되도록 한다.
                    String filename = Script.scriptTitle2FileName(item.scriptTitle, ".pdf");
                    mNotAddedPDFScrpitIds.add( 0, filename );
                    break;
                }
            }
        }

        initListView(mParsedScriptIds, mUserMadeScriptIds, mNotAddedPDFScrpitIds);
    }

    private List<ScriptSummary> convertFormat(List<Integer> scriptIds)
    {
        List<ScriptSummary> scripts = new ArrayList<>();
        for(Integer id: scriptIds){
            ScriptSummary script = new ScriptSummary();
            script.scriptId = id;
            script.scriptTitle = mScriptModel.getScriptTitleById(id);
            if( StringHelper.isNull(script.scriptTitle) ){
                script.scriptTitle = "스크립트 제목을 가져올 수 없습니다";
            }
            script.state = Script.STATE_NORMAL_SCRIPT;
            scripts.add(script);
        }

        return scripts;
    }

    private List<ScriptSummary> convertFormatFile(List<String> fileNames)
    {
        List<ScriptSummary> scripts = new ArrayList<>();
        for(String filename : fileNames){
            ScriptSummary script = new ScriptSummary();
            script.scriptId = 0;
            script.scriptTitle = filename;
            if( StringHelper.isNull(script.scriptTitle) ){
                script.scriptTitle = "파일 이름을 가져올 수 없습니다";
            }
            script.state = STATE_NON_PARSED_SCRIPT;
            scripts.add(script);
        }

        return scripts;
    }

    private ScriptSummary makeNewButton()
    {
        ScriptSummary newbutton = new ScriptSummary();
        newbutton.scriptId = 0;
        newbutton.scriptTitle = "다른 디렉토리에서 스크립트 추가하기 \n (현재 디렉토리: 카카오톡 다운로드)";
        newbutton.state = Script.STATE_NEWBUTTON;
        return newbutton;
    }

    private ScriptSummary makeDescription(int stringId) {
        return makeDescription(stringId, 0);
    }

    private ScriptSummary makeDescription(int stringId, int itemCnt)
    {
        String title = mContext.getString(stringId);
        if( itemCnt > 0 ) {
            title += " ("+itemCnt+" 개)";
        }
        int state = STATE_DESCRIPTION;
        if( R.string.show_scripts__empty_scripts == stringId ){
            state = STATE_NONE;
        }
        ScriptSummary fakeObject = new ScriptSummary();
        fakeObject.scriptId = 0;
        fakeObject.scriptTitle = title;
        fakeObject.state = state;
        return fakeObject;
    }

}
