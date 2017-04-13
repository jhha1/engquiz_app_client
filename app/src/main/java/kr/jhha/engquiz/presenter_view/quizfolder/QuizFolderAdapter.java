package kr.jhha.engquiz.presenter_view.quizfolder;

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
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;

/**
 * Created by thyone on 2017-03-30.
 */

public class QuizFolderAdapter extends BaseAdapter {

    private Context mContext;
    private QuizFolderRepository mQuizFolderModel;
    private List<QuizFolder> mListView;

    /*
        인자값 quizFolders :  QuizFolderRepository.mQuizFolders의 포인터임.
        mListView = quizFolders;  // 포인터복사로, mListView의 변경값이 QuizFolderRepository.mQuizFolders에 반영.
        난, mListView = new ArrayList<>(quizFolders); 로 값 복사후, "New.." button 추가.
        ("New.." button을 listview에 추가하는건 UI쪽이니 이 아답터에서만 들고있길 원하므로)
     */
    public QuizFolderAdapter( QuizFolderRepository quizFolderRepository, List<QuizFolder> quizFolders) {
        mQuizFolderModel = quizFolderRepository;
        mListView = new ArrayList<>(quizFolders);
        mListView.add( makeNewButton() );

        Log.e("AppContent", "new QuizFolderAdapter() mListView:"+ mListView.toString());
    }

    private QuizFolder makeNewButton()
    {
        QuizFolder newbutton = new QuizFolder();
        newbutton.setTitle(QuizFolder.TEXT_NEW_FOLDER);
        newbutton.setState(QuizFolder.STATE_NEWBUTTON);
        return newbutton;
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
        QuizFolder item = mListView.get(position);

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
        titleTextView.setText(item.getTitle());
        Log.i("AppContent", "QuizFolderAdapter.getView() " +
                "quizFolderItem:"+ item.toString()
        +", titleTextView:"+titleTextView.getText());


        return convertView;
    }

    private Drawable getIcon(Integer state){
        int resourceID = R.drawable.ic_content__folder_normal_gray;
        switch (state){
            case QuizFolder.STATE_NEW:
                resourceID = R.drawable.ic_content__new_red;
                break;
            case QuizFolder.STATE_PLAYING:
                resourceID = R.drawable.ic_content__folder_playing_gray;
                break;
            case QuizFolder.STATE_OTHER:
                resourceID = R.drawable.ic_content__folder_normal_gray;
                break;
            case QuizFolder.STATE_NEWBUTTON:
                resourceID = R.drawable.ic_content__folder_add_gray;
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

    public void updateItems( List<QuizFolder> quizFolders ){
        mListView = quizFolders;
        mListView.add( makeNewButton() );
    }

}
