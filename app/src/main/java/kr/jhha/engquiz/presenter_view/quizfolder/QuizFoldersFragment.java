package kr.jhha.engquiz.presenter_view.quizfolder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.FolderScriptsFragment;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.MyLog;
import kr.jhha.engquiz.util.ui.click_detector.ClickDetector;
import kr.jhha.engquiz.util.ui.click_detector.ListViewClickDetector;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.NEW_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SCRIPTS_IN_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_QUIZFOLDERS;
import static kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersPresenter.ERR_NET;
import static kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersPresenter.ERR_NEWBUTTON;
import static kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersPresenter.ERR_NOEXSITED_FOLDER;
import static kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersPresenter.ERR_NOEXSITED_SCRIPT;
import static kr.jhha.engquiz.presenter_view.quizfolder.QuizFoldersPresenter.NOALLOWED_DELETE_PLAYING;

/**
 * Created by jhha on 2016-12-16.
 */

public class QuizFoldersFragment extends Fragment implements  QuizFoldersContract.View, ClickDetector.Callback
{
    private QuizFoldersContract.ActionsListener mActionListener;
    QuizFolderAdapter mAdapter;

    // 리스트 뷰 UI
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private QuizFolder mListviewSelectedItem = null;

    // 클릭 or 더블클릭 감지자. 안드로이드 더블클릭 감지해 알려주는 지원없어 직접 만듬.
    private ClickDetector mClickDetector = null;

    public QuizFoldersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mActionListener = new QuizFoldersPresenter( this, QuizFolderRepository.getInstance() );
        mClickDetector = new ListViewClickDetector( this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpToolBar();

        View view = inflater.inflate(R.layout.content_quizfolder, null);
        mItemListView = (ListView) view.findViewById(R.id.quizfolderview);
        // 클릭 이벤트 핸들러 정의: 원클릭-내 퀴즈 디테일 보기, 더블클릭-게임용으로 퀴즈폴더설정
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 내 퀴즈 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        mActionListener.getQuizFolderList();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(SHOW_QUIZFOLDERS);
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            QuizFolder item = (QuizFolder) parent.getItemAtPosition(position) ;
            mListviewSelectedItem = item;
            // 클릭과 더블클릭을 구분해 그에따른 동작.
            mClickDetector.onClick( position );
        }
    };

    // 한번 클릭 (리스트뷰 아이템) : 퀴즈폴더 상세보기
    @Override
    public void onSingleClicked() {
        mActionListener.listViewItemClicked( mListviewSelectedItem );
    }

    // 더블 클릭 (리스트뷰 아이템) : 이 퀴즈폴더를 게임플레이용으로 설정
    @Override
    public void onDoubleClicked() {
        mActionListener.listViewItemDoubleClicked( mListviewSelectedItem );
    }

    // 롱 클릭 (리스트뷰 아이템) : 퀴즈폴더 삭제 다이알로그 보이기
    private AdapterView.OnItemLongClickListener mListItemLongClickListener
            = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
        {

            final QuizFolder item = (QuizFolder) parent.getItemAtPosition(position) ;

            final MyDialog dialog = new MyDialog(getActivity());
            dialog.setTitle(getString(R.string.del_folder__title));
            dialog.setMessage( item.getTitle() + getString(R.string.common__del_comfirm) );
            dialog.setPositiveButton( new View.OnClickListener() {
                public void onClick(View arg0)
                {
                    // 퀴즈폴더삭제
                    mActionListener.delQuizFolder( item );
                    dialog.dismiss();
                }});
            dialog.setNegativeButton();
            dialog.showUp();
            return true;
        }
    };

    @Override
    public void onSuccessGetQuizFolderList(List<QuizFolder> quizFolders) {
        MyLog.d(quizFolders.toString());
        mAdapter = new QuizFolderAdapter( QuizFolderRepository.getInstance(), quizFolders );
        mItemListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onFailGetQuizFolderList() {
        Toast.makeText(getActivity(),
                getString(R.string.show_folders__get_folders__fail_defaut),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelQuizFolder(List<QuizFolder> updatedQuizFolders) {
        mAdapter.updateItems( updatedQuizFolders );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.del_folder__success),
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailDelQuizFolder(int reasonCode ) {
        int stringID = 0;
        switch (reasonCode){
            case ERR_NEWBUTTON:
                stringID = R.string.del_folder__fail_new_btn;
                break;
            case NOALLOWED_DELETE_PLAYING:
                stringID = R.string.del_folder__fail_playing_folder;
                break;
            case ERR_NOEXSITED_FOLDER:
                stringID = R.string.del_folder__fail_noexist_folder;
                break;
            default:
                stringID = R.string.del_folder__fail;
                break;
        }

        Toast.makeText(getActivity(), getString(stringID), Toast.LENGTH_SHORT).show();
    }

    @Override
    // 플레이용 퀴즈 변경 확인 다이알로그
    public void showDialogChangePlayingQuizFolder(final QuizFolder listviewSelectedItem ) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.show_folders__change_folder_for_playing__title));
        dialog.setMessage( "'"+listviewSelectedItem.getTitle() + "'"
                        + getString(R.string.show_folders__change_folder_for_playing__confirm));
        dialog.setPositiveButton( new View.OnClickListener() {
                                        public void onClick(View arg0)
                                        {
                                            mActionListener.changePlayingQuizFolder( listviewSelectedItem );
                                            dialog.dismiss();
                                        }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void onSucessChangePlayingQuizFolder(List<QuizFolder> uiSortedQuizFolders) {
        mAdapter.updateItems( uiSortedQuizFolders );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.show_folders__change_folder_for_playing__success),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailChangePlayingQuizFolder(int reason) {
        int stringID = 0;
        switch (reason){
            case ERR_NEWBUTTON:
                stringID = R.string.show_folders__change_folder_for_playing__fail_new_folder;
                break;
            case ERR_NOEXSITED_SCRIPT:
                stringID = R.string.show_folders__change_folder_for_playing__fail_no_script;
                break;
            case ERR_NET:
                stringID = R.string.common__network_err;
                break;
            default:
                stringID = R.string.show_folders__change_folder_for_playing__fail_default;
                break;
        }

        Toast.makeText(getActivity(), getString(stringID), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeFragmetNew(){
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.changeViewFragment(NEW_QUIZFOLDER);
    }

    @Override
    public void onChangeFragmetFolderDetail( Integer quizFolderId, String quizFolderTitle ) {
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = SHOW_SCRIPTS_IN_QUIZFOLDER;

        // 퀴즈폴더 디테일보기 프래그먼트는 인자값을 넘겨야함.
        FolderScriptsFragment detailFragment = (FolderScriptsFragment) fragmentHandler.getFragment(fragmentID);
        detailFragment.setSelectedQuizGroupId(quizFolderId, quizFolderTitle);
        fragmentHandler.changeViewFragment( fragmentID );
    }
}

