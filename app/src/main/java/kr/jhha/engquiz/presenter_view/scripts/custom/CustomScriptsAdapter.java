package kr.jhha.engquiz.presenter_view.scripts.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;

import static kr.jhha.engquiz.model.local.Script.STATE_NEWBUTTON;
import static kr.jhha.engquiz.model.local.Script.STATE_NONE;
import static kr.jhha.engquiz.model.local.Script.STATE_ADDED_SCRIPT;
import static kr.jhha.engquiz.model.local.Script.STATE_QUIZPLAYING_SCRIPT;

/**
 * Created by thyone on 2017-03-30.
 */

public class CustomScriptsAdapter extends BaseAdapter {

    private Context mContext;
    private ScriptRepository mScriptModel;

    List<Integer> mUserMadeScriptIds;

    public class ScriptSummary {
        Integer scriptId;
        String scriptTitle;
        Integer state;
    }
    private List<ScriptSummary> mListView;

    public CustomScriptsAdapter(Context context, ScriptRepository scriptRepository, List<Integer> userMadeScriptIds) {
        mContext = context;
        mScriptModel = scriptRepository;
        mUserMadeScriptIds = userMadeScriptIds;
        initListView(userMadeScriptIds);
    }

    private void initListView(List<Integer> userMadeScriptIds)
    {
        mListView = new ArrayList<>();

        List<ScriptSummary> list = convertFormat(userMadeScriptIds);
        if( false == list.isEmpty() )
            mListView.addAll(list);

        mListView.add( makeNewButton() ); // 새 스크립트 추가 버튼
    }

    /*
        Adapter에 사용되는 데이터의 개수를 리턴
    */
    @Override
    public int getCount() {
        return mListView.size();
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
            convertView = inflater.inflate(R.layout.content_script_listview_item, parent, false);
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
            case STATE_ADDED_SCRIPT:
                resourceID = R.drawable.img_circle_yellow;
                break;
            case STATE_QUIZPLAYING_SCRIPT:
                resourceID = R.drawable.img_play_orange;
                break;
            case STATE_NEWBUTTON:
                resourceID = R.drawable.img_script_add_blue;
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
        if( state == STATE_NEWBUTTON) {
            int color = ContextCompat.getColor(context, R.color.holo_blue);
            titleTextView.setTextColor(color);
           // titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        } else {
            int color = ContextCompat.getColor(context, R.color.black_alpha_70);
            titleTextView.setTextColor(color);
           // titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
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

    // 스크립트 추가
    public void addItem( Script newScript )
    {
        if( Script.isNull(newScript)){
            return;
        }

        if( false == mScriptModel.isUserMadeScript(newScript.scriptId) ){
            return;
        }

        mUserMadeScriptIds.add(0, newScript.scriptId);
        initListView(mUserMadeScriptIds);
    }

    // 스크립트 삭제.
    public void delItem(CustomScriptsAdapter.ScriptSummary item)
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
        initListView(mUserMadeScriptIds);
    }

    public void addItemIntoQuizPlaying(CustomScriptsAdapter.ScriptSummary item) {
        item.state = STATE_QUIZPLAYING_SCRIPT;

    }

    public void removeItemFromQuizPlaying(CustomScriptsAdapter.ScriptSummary item) {
        item.state = STATE_ADDED_SCRIPT;
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
            boolean bQuizPlayingScript = QuizPlayRepository.getInstance().hasScript(id);
            if( bQuizPlayingScript )
                script.state = Script.STATE_QUIZPLAYING_SCRIPT;
            else
                script.state = Script.STATE_ADDED_SCRIPT;

            scripts.add(script);
        }

        return scripts;
    }

    private ScriptSummary makeDescription(int stringId) {
        return makeDescription(stringId, 0);
    }

    private ScriptSummary makeDescription(int stringId, int itemCnt)
    {
        ScriptSummary fakeObject = new ScriptSummary();
        fakeObject.scriptId = 0;
        fakeObject.scriptTitle = mContext.getString(stringId);
        fakeObject.state = STATE_NONE;
        return fakeObject;
    }

    private ScriptSummary makeNewButton()
    {
        ScriptSummary newbutton = new ScriptSummary();
        newbutton.scriptId = 0;
        newbutton.scriptTitle = "새 스크립트 만들기";
        newbutton.state = Script.STATE_NEWBUTTON;
        return newbutton;
    }
}
