package kr.jhha.engquiz.view.fragments.playlist;

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

public class PlayListAdapter extends BaseAdapter
{
    private static final PlayListAdapter singletonInstance = new PlayListAdapter();

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PlayListItem> listViewItemList = new ArrayList<PlayListItem>();

    private PlayListAdapter() {}

    public static PlayListAdapter getInstance() {
        return singletonInstance;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_playlist_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        PlayListItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

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
        for(PlayListItem item : listViewItemList) {
            titles.add(item.getTitle());
        }
        return titles.toArray();
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String title, String desc) {
        PlayListItem item = new PlayListItem();

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

    public boolean deleteItem(int itemIndex)
    {
        if(listViewItemList.isEmpty()) {
            return false;
        }

        boolean isInvalidIndex = (listViewItemList.size() <= itemIndex) ? true : false;
        if( isInvalidIndex ) {
            return false;
        }

        listViewItemList.remove(itemIndex);

        return true;
    }
}

