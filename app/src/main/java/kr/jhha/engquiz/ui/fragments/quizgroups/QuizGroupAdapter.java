package kr.jhha.engquiz.ui.fragments.quizgroups;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;

public class QuizGroupAdapter extends BaseAdapter
{
    private static final QuizGroupAdapter singletonInstance = new QuizGroupAdapter();

    // quiz group 리스트뷰 data
    private ArrayList<QuizGroupItem> listViewItemList = new ArrayList<QuizGroupItem>();

    private QuizGroupAdapter() {}
    public static QuizGroupAdapter getInstance() {
        return singletonInstance;
    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {

        return listViewItemList.size();
    }

    /*
    // position에 위치한 데이터를 화면에 출력하는데
        사용될 View를 리턴. 필수 구현
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();
        int viewType = getItemViewType(position);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        QuizGroupItem quizGroupItem = listViewItemList.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_playlist_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(quizGroupItem.getIcon());
        titleTextView.setText(quizGroupItem.getTitle());
        Integer scriptCount = quizGroupItem.getScriptIndexes().size();
        descTextView.setText(scriptCount + quizGroupItem.getDesc());

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
        return listViewItemList.get(position) ;
    }

    public Object[] getPlayListTitles() {
        List titles = new ArrayList();
        for(QuizGroupItem item : listViewItemList) {
            titles.add(item.getTitle());
        }
        return titles.toArray();
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addNewQuizGroup(Drawable icon, String title, String desc) {
        QuizGroupItem item = new QuizGroupItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        if(listViewItemList.isEmpty()) {

        }
        // 첫번째 인덱스에 요소를 삽입함으로써,
        // 내부적으로 새 공간 확보 후, list 전체 데이터를 새 공간에 복사한다.
        // 내 퀴즈 추가가 자주 안일어날 걸로 예상하고,
        // 성능보다 구현편의성 선택.
        listViewItemList.add(0, item);
    }

    public boolean deleteQuizGroup(Activity activity, int itemIndex )
    {
        QuizGroupItem item = null;
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
    }

    public void addScriptIntoDefaultQuizGroup( Integer scriptIndex )
    {
        Log.d("%%%%%%%%%%%%%%%", "addScriptIntoDefaultQuizGroup() called. scriptIndex:" + scriptIndex );
        for( QuizGroupItem item : listViewItemList ){
            if( item == null ) {
                continue;
            }
            if( "Default".equals( item.getTitle() )) {
                item.addScriptIndex( scriptIndex );
                Log.d("%%%%%%%%%%%%%%%", "addScriptIntoDefaultQuizGroup() " +
                        "itemAdded. Index:" +scriptIndex +", title:" + item.getTitle() );
            }
        }
    }
}

