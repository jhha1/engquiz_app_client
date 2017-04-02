package kr.jhha.engquiz.quizfolder.detail;

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
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-30.
 */

public class QuizFolderDetailAdapter extends BaseAdapter {

    private Context mContext;
    private QuizFolderRepository mQuizFolderModel;
    private ScriptRepository mScriptModel;

    public class ScriptSummary {
        Integer quizFolderId;
        Integer scriptId;
        String scriptTitle;
        Integer state;
    }
    private List<ScriptSummary> mListView;
    private Integer mQuizFolderID;

    public QuizFolderDetailAdapter(ScriptRepository scriptRepository,
                                   Integer quizFolderID, List<Integer> quizFolderScriptIds) {
        mScriptModel = scriptRepository;
        mListView = convertFormat(quizFolderScriptIds);
        mListView.add( makeNewButton() );
        mQuizFolderID = quizFolderID;
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
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

        // 아이템 내 각 위젯에 데이터 반영
        int resourceID = R.drawable.ic_format_align_left_grey600_48dp;
        Drawable icon = ContextCompat.getDrawable(mContext, resourceID);
        iconImageView.setImageDrawable(icon);
        titleTextView.setText(script.scriptTitle);
        // set description
        if ("New..".equals(script.scriptTitle)) {
            descTextView.setText("클릭하여 스크립트 추가하기");
        } else {
            descTextView.setText("");
        }
        return convertView;
    }

    public View getView_legacy(int position, View convertView, ViewGroup parent) {
        if( mContext == null )
            mContext = parent.getContext();

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        int uiOrderNumber = position + 1;
        String scriptTitle = mQuizFolderModel.getQuizFolderScriptTitleByUIOrder(mQuizFolderID, uiOrderNumber);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_quizfolder_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

        // 아이템 내 각 위젯에 데이터 반영
        int resourceID = R.drawable.ic_format_align_left_grey600_48dp;
        Drawable icon = ContextCompat.getDrawable(mContext, resourceID);
        iconImageView.setImageDrawable(icon);
        // set title
        if(StringHelper.isNullString(scriptTitle)){
            titleTextView.setText("스크립트 제목을 읽어올 수 없습니다..");
        } else {
            titleTextView.setText(scriptTitle);
        }
        // set description
        if ("New..".equals(scriptTitle)) {
            descTextView.setText("클릭하여 스크립트 추가하기");
        } else {
            descTextView.setText("");
        }
        return convertView;
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

    public Object getItem_legacy(int position) {
        // model의 summary list 배열 순서대로 UI listveiw에 뿌려지게 된다.
        // so, listview의 position은 arraylist의 index다.
        return mQuizFolderModel.getQuizFolderScriptTitleByUIOrder(mQuizFolderID, position);
    }

    public void updateItems( List<Integer> quizFolderScriptIds ){
        mListView = convertFormat(quizFolderScriptIds);
        mListView.add( makeNewButton() );
    }

    private List<ScriptSummary> convertFormat(List<Integer> quizFolderScriptIds)
    {
        List<ScriptSummary> scripts = new ArrayList<>();
        for(Integer id: quizFolderScriptIds){
            String title = mScriptModel.getParsedScriptTitleAsId(id);

            ScriptSummary script = new ScriptSummary();
            script.quizFolderId = mQuizFolderID;
            script.scriptId = id;
            script.scriptTitle = title;
            if( StringHelper.isNullString(title) ){
                script.scriptTitle = "스크립트 제목을 가져올 수 없습니다";
            }
            script.state = QuizFolder.STATE_OTHER;
            scripts.add(script);
        }
        return scripts;
    }

    private ScriptSummary makeNewButton()
    {
        ScriptSummary newbutton = new ScriptSummary();
        newbutton.scriptId = 0;
        newbutton.scriptTitle = QuizFolder.TEXT_NEW;
        newbutton.state = QuizFolder.STATE_NEWBUTTON;
        return newbutton;
    }


}
