package kr.jhha.engquiz.quizgroup.detail;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizGroupDetail;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;

public class QuizGroupDetailAdapter extends BaseAdapter
{
    private static final QuizGroupDetailAdapter singletonInstance = new QuizGroupDetailAdapter();

    // quiz group 리스트뷰 data
    //private ArrayList<QuizGroupDetailItem> listViewItemList = new ArrayList<QuizGroupDetailItem>();
    private Map<String, ArrayList> mQuizGroupDetailIMap = new HashMap<>();

    private QuizGroupDetail mCurrentQuizGroup = null;

    private QuizGroupDetailAdapter() {}
    public static QuizGroupDetailAdapter getInstance() {
        return singletonInstance;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        if( mCurrentQuizGroup != null && mCurrentQuizGroup.getScriptIds() != null)
            return mCurrentQuizGroup.getScriptIds().size();
        return 0;
    }

    /*
    // position에 위치한 데이터를 화면에 출력하는데
        사용될 View를 리턴. 필수 구현
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Context context = parent.getContext();
        QuizGroupDetailItem item = getCurrentQuizGroupDetailList().get( position );

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content_quizgroupdetail_listview_item, parent, false);
        }
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);

        iconImageView.setImageDrawable(item.getIcon());
        titleTextView.setText(item.getTitle());

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
        return getCurrentQuizGroupDetailList().get( position ) ;
    }

    private List<QuizGroupDetailItem> getCurrentQuizGroupDetailList() {
        return mQuizGroupDetailIMap.get( mCurrentQuizGroup.getTitle() );
    }

    public void setCurrentQuizGroup( QuizGroupDetail quizGroup )
    {
        String quizGroupTitle = quizGroup.getTitle();
        if( StringHelper.isNullString(quizGroupTitle) ) {
            Log.e("AppContent", "QuizGroupDetailAdapter.setCurrentQuizGroup() quizGroupTitle is null");
            return;
        }
        if( false == mQuizGroupDetailIMap.containsKey( quizGroupTitle ) ) {
            ArrayList<QuizGroupDetailItem> detailList = new ArrayList<>();
            Log.d("%%%%%%%%%%%%%%%", "detailadapter. setCurrentQuizGroup. " +
                    "quizGroup.getScriptIds().size():" + quizGroup.getScriptIds().size());
            for( Integer index : quizGroup.getScriptIds() ) {
                String scriptTitle = ScriptRepository.getInstance().getScriptTitleAsId( index );
                QuizGroupDetailItem detail = new QuizGroupDetailItem();
                detail.setTitle( scriptTitle );
                detailList.add( detail );
            }

            mQuizGroupDetailIMap.put( quizGroupTitle,  detailList );
        }
        this.mCurrentQuizGroup = quizGroup;

        printMap();
    }

    private void printMap() {
        for(Map.Entry<String, ArrayList> e : this.mQuizGroupDetailIMap.entrySet()) {
            String groupTitle = e.getKey();
            ArrayList<QuizGroupDetailItem> list = e.getValue();
            Log.e("%%%%%%%%%%%","detailAdapterMap. --------- groupTitle --------: " + groupTitle);
            for( QuizGroupDetailItem item : list) {
                Log.e("%%%%%%%%%%%","detailAdapterMap. " + item.getTitle());
            }
        }
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addScript(Drawable icon, String title, String desc) {
        QuizGroupDetailItem item = new QuizGroupDetailItem();

        item.setIcon(icon);
        item.setTitle(title);

        if(getCurrentQuizGroupDetailList().isEmpty()) {

        }
        // 첫번째 인덱스에 요소를 삽입함으로써,
        // 내부적으로 새 공간 확보 후, list 전체 데이터를 새 공간에 복사한다.
        // 내 퀴즈 추가가 자주 안일어날 걸로 예상하고,
        // 성능보다 구현편의성 선택.
        getCurrentQuizGroupDetailList().add(0, item);
    }

    public boolean deleteScript(Activity activity, int itemIndex )
    {
        QuizGroupDetailItem item = null;
        try {
            item = getCurrentQuizGroupDetailList().get( itemIndex );
        } catch ( IndexOutOfBoundsException e ) {
            Toast.makeText( activity,
                    "일시적인 오류. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String parentTitle = mCurrentQuizGroup.getTitle();
        if( "Default".equals(parentTitle) && getCurrentQuizGroupDetailList().size() <= 1 ) {
            Toast.makeText( activity,
                    "Default 폴더에는 최소 1개 이상의 스크립트가 있어야 합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        getCurrentQuizGroupDetailList().remove(itemIndex);
        return true;
    }
}

