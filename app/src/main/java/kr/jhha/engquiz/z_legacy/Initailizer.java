package kr.jhha.engquiz.z_legacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.local.Script;
import kr.jhha.engquiz.quizgroup.QuizGroupAdapter;
import kr.jhha.engquiz.quizgroup.QuizGroupSummary;

/**
 * Created by thyone on 2017-02-10.
 *
 // 퀴즈스크립트 정책:
 //      1. 퀴즈게임용 데이터는 '내 퀴즈' 카테고리의 폴더 중 하나여야 한다.
 //      2. '내 퀴즈' 카테고리 안에 최소 하나 이상의 폴더가 있어야 한다.
 //      3. 폴더 안에 최소 하나 이상의 스크립트가 있어야 한다.
 //      4. 최대 생성 가능한 폴더는 20개. 스크립트는 일단 무제한.

 // 첫 접속시 퀴즈로 사용할 데이터:
 // 0. 첫 접속 여부는 클라 myapp_properties.xml 파일에 적힌 lastestPlayMyQuizIndex가 -1 이면.
 // 1. 서버와 클라의 스크립트 이름을 비교해, 일치하는 것만 내려받는다(=파싱된 스크립트)
 // 비교를 위해 검사하는 클라 내의 폴더는 카카오톡다운로드 폴더.
 // 2. 내 퀴즈 카테고리에 생성한 default my quiz 폴더에 내려받은 스크립트를 넣는다.
 // 3. 퀴즈게임에서는 default my quiz 폴더를 타겟폴더로 설정하고, 타겟 폴더 내의 스크립트들을 대상으로 퀴즈를 만들어 플레이시킨다.
 // * 만약, 클라의 카톡 폴더내에 서버와 일치하는 스크립트가 없다면, 스크립트가 저장된 폴더 경로를 입력받아 1번부터 수행한다.
 // * 클라에게 스크립트가 하나도 없다면, 임의의 예제 스크립트를 서버로부터 내려받아 2번부터 수행한다.

 // 스크립트는 전체 로드하지말고, 현재 플레이용만.
 */

public class Initailizer
{
    private static final Initailizer instance = new Initailizer();
    private Context mContext = null;

    private Initailizer() {}

    public static Initailizer getInstance() {
        return instance;
    }

    // SharedPreferences : 간단한 설정 값들을 xml형태로 저장 및 로드하도록 돕는 클래스.
    //                      선언은 Activity에서 해야함.
    public boolean initBackend( Context context, SharedPreferences preferences  )
    {
        Log.i("!!!!!!!!!!!!!!","Init Backend..");
        mContext = context;

        return true;
    }

/*

    // 내 퀴즈 카테고리의 내 퀴즈 리스트 초기화
    private void initQuizGroups(Context context, Map<Integer, Script> parsedScripts  ) {
        List <QuizGroupSummary> groups = new DBHelper(context).selectQuizGroups();
        if( groups.size() <= 0 )
        {
            QuizGroupSummary item = new QuizGroupSummary();
            item.setTitle( "New.." );
            item.setDesc( "원하는 스크립트를 선택해, 나만의 퀴즈를 만듭니다." );
            QuizGroupAdapter.getInstance().addQuizGroup(item);

            // 임시적인 셋팅법.
            // TODO 퀴즈그룹 정보를 오프라인에 저장후, 읽어와 거기에 잇는 script 정보를 보고 셋팅.
            List<Integer> parsedScriptIndexes = new ArrayList<>();
            int i = 0;
            for( Map.Entry<Integer, Script> e : parsedScripts.entrySet() ) {
                Integer scriptId = e.getKey();
                parsedScriptIndexes.add(scriptId) ;
            }
            item = new QuizGroupSummary();
            item.setTitle( "Default" );
            item.setDesc( "개의 스크립트가 들어있습니다." );
            item.setScriptIndexes(parsedScriptIndexes);
            QuizGroupAdapter.getInstance().addQuizGroup(item);
            return;
        }

        for( QuizGroupSummary item : groups ) {
            QuizGroupAdapter.getInstance().addQuizGroup( item );
        }
    }
*/
}
