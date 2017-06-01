package kr.jhha.engquiz.presenter_view.sentences;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Sentence;

/**
 * Created by thyone on 2017-03-30.
 */

public class SentenceAdapter extends BaseAdapter {

    private Context mContext;
    private List<Sentence> mListView;

    public SentenceAdapter(boolean bCustomSentences, List<Sentence> sentences)
    {
        // 문장이 없을 수 있다. 스크립트 첨 만들거나 문장삭제하면.
        if( sentences == null || sentences.isEmpty() )
            mListView = new ArrayList<>();
        else
            mListView = new ArrayList<>(sentences);

        if( bCustomSentences)
            mListView.add( makeNewButton() ); // 새 문장 추가 버튼
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
        Sentence item = mListView.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_sentence_edit_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView numView = (TextView) convertView.findViewById(R.id.sentece_edit_numberView);
        TextView textViewKo = (TextView) convertView.findViewById(R.id.sentece_edit_textView1);
        TextView textViewEn = (TextView) convertView.findViewById(R.id.sentece_edit_textView2);

        // 아이템 내 각 위젯에 데이터 반영
        setIconText(numView, item.type, position);
        setText(textViewKo, textViewEn, item);
        return convertView;
    }

    private void setIconText(TextView numView, Sentence.TYPE type, Integer position){
        if( type.equals(Sentence.TYPE.NEW_BUTTON)){
            numView.setText(mContext.getString(R.string.sentence__create_listview_text_icon));
            int color = ContextCompat.getColor(mContext, R.color.holo_blue);
            numView.setTextColor(color);
        } else {
            //int color = ContextCompat.getColor(mContext, R.color.black_alpha_70);
            int color = ContextCompat.getColor(mContext, R.color.black_alpha_30);
            numView.setTextColor(color);
            numView.setText(Integer.toString(position + 1));
        }
    }

    private void setText( TextView textViewKo, TextView textViewEn, Sentence item ){
       if( item.type.equals(Sentence.TYPE.NEW_BUTTON) )
       {
           textViewKo.setText(mContext.getString(R.string.sentence__create_btn));
           int color = ContextCompat.getColor(mContext, R.color.holo_blue);
           textViewKo.setTextColor(color);
           textViewKo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
           textViewEn.setVisibility(View.GONE);
       }
       else
       {
           textViewKo.setText(item.textKo);
           int color = ContextCompat.getColor(mContext, R.color.black_alpha_40);
           textViewKo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
           textViewKo.setTextColor(color);
           textViewEn.setVisibility(View.VISIBLE);
           textViewEn.setText(item.textEn);
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
        return mListView.get(position);
    }

    private Sentence makeNewButton()
    {
        Sentence newbutton = new Sentence();
        newbutton.type = Sentence.TYPE.NEW_BUTTON;
        return newbutton;
    }
}
