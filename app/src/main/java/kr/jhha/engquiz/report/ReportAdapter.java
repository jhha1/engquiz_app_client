package kr.jhha.engquiz.report;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import kr.jhha.engquiz.data.local.Report;

/**
 * Created by thyone on 2017-03-30.
 */

public class ReportAdapter extends BaseAdapter {

    private Context mContext;
    private List<Report> mListView;

    public ReportAdapter(List<Report> reports) {
        mListView = new ArrayList<>(reports);
        Log.e("AppContent", "new QuizFolderAdapter() mListView:"+ mListView.toString());
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
        Report item = mListView.get(position);

        Log.i("####################", item.toString());

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_quizfolder_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(getIcon(item.getState()));
        titleTextView.setText(item.getTextKo());
        Log.i("AppContent", "ReportAdapter.getView() " +
                "Item:"+ item.toString()
        +", titleTextView:"+titleTextView.getText());
        return convertView;
    }

    private Drawable getIcon(Integer state){
        int resourceID = R.drawable.ic_playlist_play_black_36dp;
        switch (state) {
            case Report.STATE_REPORTED:
                resourceID = R.drawable.ic_playlist_play_black_36dp;
                break;
            case Report.STATE_MODIFILED:
                resourceID = R.drawable.ic_playlist_add_check_black_36dp;
                break;
        }
        Drawable icon = ContextCompat.getDrawable(mContext, resourceID);
        return icon;
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

    public void updateIcon(int position, int state){
        Report report = (Report)getItem(position);
        if( report != null ){
            report.setState(state);
        }
    }
}
