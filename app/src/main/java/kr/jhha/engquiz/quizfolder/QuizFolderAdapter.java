package kr.jhha.engquiz.quizfolder;

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
import kr.jhha.engquiz.quizfolder.detail.QuizFolderDetailAdapter;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-30.
 */

public class QuizFolderAdapter extends BaseAdapter {

    private Context mContext;
    private QuizFolderRepository mQuizFolderModel;
    private List<QuizFolder> mListView;

    public QuizFolderAdapter( QuizFolderRepository quizFolderRepository, List<QuizFolder> quizFolders) {
        mQuizFolderModel = quizFolderRepository;
        mListView = addNewButton(quizFolders);
    }

    private List<QuizFolder> addNewButton(List<QuizFolder> quizFolders)
    {
        QuizFolder newbutton = new QuizFolder();
        newbutton.setTitle(QuizFolder.TEXT_NEW);
        newbutton.setState(QuizFolder.STATE_NEWBUTTON);
        quizFolders.add(newbutton);
        return quizFolders;
    }

    /*
        Adapter에 사용되는 데이터의 개수를 리턴
    */
    @Override
    public int getCount() {
        return mListView.size();
        //return mQuizFolderModel.getQuizFolderCount();
    }

    /*
        position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( mContext == null )
            mContext = parent.getContext();

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        QuizFolder quizFolderItem = mListView.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        titleTextView.setText(quizFolderItem.getTitle());
        if (QuizFolder.TEXT_NEW.equals(quizFolderItem.getTitle())) {
            descTextView.setText("클릭하여 새로운 퀴즈폴더 만들기");
        }
        return convertView;
    }

    public View getView_legacy(int position, View convertView, ViewGroup parent) {
        if( mContext == null )
            mContext = parent.getContext();

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        QuizFolder quizFolderItem = mQuizFolderModel.getQuizFolderByUIOrder(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        titleTextView.setText(quizFolderItem.getTitle());
        if ("New..".equals(quizFolderItem.getTitle())) {
            descTextView.setText("클릭하여 새로운 퀴즈폴더 만들기");
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
        return mListView.get(position);
    }

}
