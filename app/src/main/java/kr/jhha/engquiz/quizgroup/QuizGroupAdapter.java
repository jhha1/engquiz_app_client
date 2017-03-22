package kr.jhha.engquiz.quizgroup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizGroupDetail;
import kr.jhha.engquiz.data.local.QuizGroupModel;


public class QuizGroupAdapter extends BaseAdapter
{
    //private static final QuizGroupAdapter singletonInstance = new QuizGroupAdapter();

    // quiz group 리스트뷰 data
    //private List<QuizGroupSummary> listViewItemList = new ArrayList<QuizGroupSummary>();

    private Context mContext = null;
    private static Drawable mDefaultItemIcon = null;

    private QuizGroupModel mModel = null;
    public QuizGroupAdapter(QuizGroupModel model) {
        mModel = model;
    }
    //public static QuizGroupAdapter getInstance() {
   //     return singletonInstance;
    //}

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mModel.getQuizGroupSummaryCount();
    }

    /*
    // position에 위치한 데이터를 화면에 출력하는데
        사용될 View를 리턴. 필수 구현
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if( mContext == null ) {
            mContext = parent.getContext();
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        QuizGroupSummary quizGroupItem = mModel.getQuizGroupSummary(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_playlist_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable( getDefaultIcon() );
        titleTextView.setText(quizGroupItem.getTitle());
        if( "New..".equals(quizGroupItem.getTitle()) ) {
            descTextView.setText("클릭하여 새로운 퀴즈그룹 만들기");
        }
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return mModel.getQuizGroupSummary(position) ;
    }

    private Drawable getDefaultIcon() {
        if( mDefaultItemIcon == null && mContext != null ) {
            int resourceID = R.drawable.ic_format_align_left_grey600_48dp;
            mDefaultItemIcon = ContextCompat.getDrawable( mContext, resourceID );
        }
        return mDefaultItemIcon;
    }
/*
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addQuizGroup( String title, List<Integer> scriptIds )
    {
        QuizGroupDetail item = new QuizGroupDetail();

        item.setState( QuizGroupSummary.STATE_NEW );
        item.setTitle(title);
        item.setScriptIds( scriptIds );

        // 1. save into memory.
        .add(item);

        // 2. No save into file.
        //     -> download summaries from a server when a quizgroup menu first clicked.
    }

    public boolean deleteQuizGroup(Activity activity, int itemIndex )
    {
        QuizGroupSummary item = null;
        try {
            item = listViewItemList.get( itemIndex );
        } catch ( IndexOutOfBoundsException e ) {
            Toast.makeText( activity,
                    "일시적인 오류. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String title = item.getTitle();
        if( "Default".equals(title) || "New..".equals(title) ) {
            Toast.makeText( activity,
                    "필수 폴더는 삭제 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        listViewItemList.remove(itemIndex);
        return true;
    }*/
}

