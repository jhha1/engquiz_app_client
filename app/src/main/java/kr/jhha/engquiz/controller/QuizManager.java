package kr.jhha.engquiz.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kr.jhha.engquiz.model.*;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizManager
{
    public static final QuizManager quizManager = new QuizManager();

    private Map<String, QuizList> allQuizs = new HashMap<String, QuizList>();
    private List<QuizUnit> selectedQuizs = new ArrayList<QuizUnit>();
    private Random random = new Random();

    private QuizManager() {
        init();
    }

    public static QuizManager getInstance() {
        return quizManager;
    }

    private void init()
    {
        List<String> quizFiles = FileManager.getInstance().uploadParsedFiles();
        // if ( 폰에 파싱된 파일이 없다면, 서버에서 전체받아옴)
        this.allQuizs = QuizDataMaker.parse( quizFiles );
        this.selectedQuizs = getLastPlayedQuizs();
    }

    List<QuizUnit> getLastPlayedQuizs()
    {
        List<QuizUnit> lastPlayedQuizs = new ArrayList<QuizUnit>();

        // file read. last played idx. in my quiz.
        // myquiz : myquizNum { script idx 들} , ... lastplay: myquiznum.

        // if( lastplay idx == 999 )
        // 전체 스크립트 리스트 가져옴.
        // show last played list
        lastPlayedQuizs = makeQuizList();
        // if (전체 스크립트 리스트가 없다면)
        // 개발자 문의

        return lastPlayedQuizs;
    }

    public List<QuizUnit> makeQuizList() {
        // 유저가 선택한 스크립트를 가지고 퀴즈 리스트를 만듬
        // 아직 미구현이므로, 전체 스크립트를 퀴즈 리스트로.
        List<QuizUnit> lastPlayedQuizs = new ArrayList<QuizUnit>();
        for(Map.Entry<String, QuizList> e : allQuizs.entrySet()) {
            String title = e.getKey();
            QuizList quizset = e.getValue();
            for(QuizUnit quizunit : quizset.quizList) {
                if(quizunit == null) {
                    Log.e("[ERROR]","sentence is null. scriptTitle("+title+")");
                }
                lastPlayedQuizs.add(quizunit);
            }
        }
        Log.d("makeQuizList", "Count(scriptMap:" + allQuizs.size()
                        + ", selectedQuizs:"+ selectedQuizs.size() +")");
        System.out.println("[DEBUG] !!!!!!!!!!!!" + toStringScriptMap());

        return lastPlayedQuizs;
    }

    private String toStringScriptMap() {
        StringBuffer buf = new StringBuffer();
        buf.append("////////////////// allQuizs("+ allQuizs.size()+") /////////////////////\n");
        for(Map.Entry<String, QuizList> e : allQuizs.entrySet()) {
            String title = e.getKey();
            QuizList script = e.getValue();
            buf.append(script.toString() + "\n");
        }
        buf.append("////////////////////////////////////////////////////");
        return buf.toString();
    }

    public QuizUnit getQuiz() {
        if(selectedQuizs.isEmpty())
            return null;

        int selectedIndex = randomSelectOne();
        return selectedQuizs.get(selectedIndex);
    }

    public Object[] getQuizTitleAll() {
        return this.allQuizs.keySet().toArray();
    }

    private int randomSelectOne() {
        if(selectedQuizs.isEmpty())
            return 0;

        int totalQuizCnt = selectedQuizs.size();
        int selectedIndex = random.nextInt(totalQuizCnt);
        return selectedIndex;
    }
}

