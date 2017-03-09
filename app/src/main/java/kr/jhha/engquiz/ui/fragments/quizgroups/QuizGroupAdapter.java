package kr.jhha.engquiz.ui.fragments.quizgroups;

import android.app.Activity;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.backend_logic.DBHelper;
import kr.jhha.engquiz.backend_logic.Utils;

import static kr.jhha.engquiz.ui.fragments.quizgroups.QuizGroupItem.TAG.*;


public class QuizGroupAdapter extends BaseAdapter
{
    private static final QuizGroupAdapter singletonInstance = new QuizGroupAdapter();

    // quiz group 리스트뷰 data
    private List<QuizGroupItem> listViewItemList = new ArrayList<QuizGroupItem>();

    private Context mContext = null;
    private static Drawable mDefaultItemIcon = null;
    private Integer MAX_QUIZ_GROUP_LIST_COUNT = 30;

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
        if( mContext == null ) {
            mContext = parent.getContext();
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        QuizGroupItem quizGroupItem = listViewItemList.get(position);

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
            descTextView.setText(quizGroupItem.getDesc());
        } else {
            Integer scriptCount = quizGroupItem.getScriptIndexes().size();
            descTextView.setText(scriptCount + quizGroupItem.getDesc());
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
        return listViewItemList.get(position) ;
    }

    public Drawable getDefaultIcon() {
        if( mDefaultItemIcon == null && mContext != null ) {
            int resourceID = R.drawable.ic_format_align_left_grey600_48dp;
            mDefaultItemIcon = ContextCompat.getDrawable( mContext, resourceID );
        }
        return mDefaultItemIcon;
    }

    public Object[] getPlayListTitles() {
        List titles = new ArrayList();
        for(QuizGroupItem item : listViewItemList) {
            titles.add(item.getTitle());
        }
        return titles.toArray();
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addNewQuizGroup( String title, String desc, List<Integer> scriptIndexes )
    {
        QuizGroupItem item = new QuizGroupItem();

        item.setTag( NEW );
        item.setTitle(title);
        item.setDesc(desc);
        if( scriptIndexes != null ) {
            for (Integer index : scriptIndexes) {
                item.addScriptIndex(index);
            }
        }
        long now = System.currentTimeMillis();
        item.setCreatedDateTime(now);

        addNewQuizGroup(item);
    }

    public void addNewQuizGroup( QuizGroupItem item )
    {
        listViewItemList.add(item);

        DBHelper db = new DBHelper( mContext );
        db.insertNewQuizGroup( item );
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


    /*
        # sort 순서
        현재 겜플레이용 폴더 > 유저가 만든 폴더 > default 폴더 > 새로만들기 버튼 역할 폴더
        ( playing > others > default > button make new )
        # 만약 default 폴더가 playing중이면, 아래처럼 sort.
        playing(default) > others > buttone make new
        # others는 나중에 만든 것이 가장 위로
     */
    public void sort()
    {
        QuizGroupItem playing = null;
        QuizGroupItem defaultQuizGroup = null;
        QuizGroupItem newQuizGroup = null;
        QuizGroupItem btnMakeNew = null;
        List<QuizGroupItem> otherItems = new ArrayList<>(); // 유저가 만든 폴더들.

        for( QuizGroupItem item : listViewItemList ) {
            final int tag = item.getTag();
            switch ( tag ) {
                case PLAYING:
                case PLAYING | DEFAULT: // default 폴더가 playing 중이면, playing Item으로 셋팅됨
                case PLAYING | NEW:
                case PLAYING | OTHERS:
                    playing = item; break;
                case NEW:
                    newQuizGroup = item; break;
                case DEFAULT:
                    defaultQuizGroup = item; break;
                case BUTTON_MAKE_NEW:
                    btnMakeNew = item;  break;
                default:
                    otherItems.add(item); break;
            }
        }

        // 유저가 만든 폴더들 소팅. 나중에 생성된게 위로.
        Comparator<QuizGroupItem> condition = new Comparator<QuizGroupItem>() {
            public int compare(QuizGroupItem item1, QuizGroupItem item2) {
                int ret = 0;
                if (item1.getCreatedDateTime() < item2.getCreatedDateTime())
                    ret = 1;
                else if (item1.getCreatedDateTime() == item2.getCreatedDateTime())
                    ret = 0;
                else
                    ret = -1;
                return ret ;
                // return (item2.getCreatedDateTime() - item1.getCreatedDateTime()); long자료형 비교는 이 코드 안먹힘
            }
        };
        Collections.sort(otherItems, condition);

        // 꼭 있어야 하는 폴더가 없으면, 생성
        if( btnMakeNew == null ) {
            btnMakeNew = new QuizGroupItem();
            btnMakeNew.setTitle( "New.." );
            btnMakeNew.setDesc( "원하는 스크립트를 선택해, 나만의 퀴즈를 만듭니다." );
        }

        // ui에 보여져야 할 순서대로 삽입.
        List<QuizGroupItem> sorted = new ArrayList<>();
        sorted.add(0, playing);
        if( newQuizGroup != null )
            sorted.add( newQuizGroup );
        if( otherItems != null && otherItems.size() > 0 )
            sorted.addAll( otherItems );
        if( defaultQuizGroup != null )
            sorted.add( defaultQuizGroup );
        sorted.add( btnMakeNew );
        this.listViewItemList = sorted;
    }
}

