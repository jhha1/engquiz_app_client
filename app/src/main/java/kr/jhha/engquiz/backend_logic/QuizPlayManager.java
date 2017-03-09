package kr.jhha.engquiz.backend_logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizPlayManager
{
    public static final QuizPlayManager quizManager = new QuizPlayManager();

    private Map<Integer, Script> scriptMap = new HashMap<Integer, Script>();
    private List<Sentence> selectedQuizs = new ArrayList<Sentence>();
    private Random random = new Random();
    private Integer playingQuizGroupId = 0;

    private QuizPlayManager() {
        init();
    }

    public static QuizPlayManager getInstance() {
        return quizManager;
    }

    private void init()
    {
        List<String> quizFiles = FileManager.getInstance().uploadParsedTextFiles();
        if( quizFiles == null || quizFiles.isEmpty() ) {
            return;
        }

        this.scriptMap = QuizDataMaker.parse( quizFiles );
        this.selectedQuizs = getLastPlayedQuizs();
    }

    public void changePlayingQuizGroup( QuizGroup quizGroup ) {
        if( quizGroup == null ){
            Log.e("CONTENT", "quiz group is null");
            return;
        }

        for(Integer scriptId : quizGroup.scriptIndexes) {
            // TODO 어떻게 읽어옴?
            FileManager.getInstance()
        }
    }

    List<Sentence> getLastPlayedQuizs()
    {
        List<Sentence> lastPlayedQuizs = new ArrayList<Sentence>();

        // file read. last played index. in my quiz.
        // myquiz : myquizNum { script index 들} , ... lastplay: myquiznum.

        // if( lastplay index == 999 )
        // 전체 스크립트 리스트 가져옴.
        // show last played list
        lastPlayedQuizs = makeQuizList();
        // if (전체 스크립트 리스트가 없다면)
        // 개발자 문의

        return lastPlayedQuizs;
    }

    public List<Sentence> makeQuizList() {
        // 유저가 선택한 스크립트를 가지고 퀴즈 리스트를 만듬
        // 아직 미구현이므로, 전체 스크립트를 퀴즈 리스트로.
        List<Sentence> lastPlayedQuizs = new ArrayList<Sentence>();
        for(Map.Entry<Integer, Script> e : scriptMap.entrySet()) {
            Integer index = e.getKey();
            Script quizset = e.getValue();
            for(Sentence quizunit : quizset.sentences) {
                if(quizunit == null) {
                    Log.e("[ERROR]","sentence is null. scriptIndex("+index+")");
                }
                lastPlayedQuizs.add(quizunit);
            }
        }
        Log.d("makeQuizList", "Count(scriptMap:" + scriptMap.size()
                        + ", selectedQuizs:"+ selectedQuizs.size() +")");
        System.out.println("[DEBUG] !!!!!!!!!!!!" + toStringScriptMap());

        return lastPlayedQuizs;
    }

    private String toStringScriptMap() {
        StringBuffer buf = new StringBuffer();
        buf.append("////////////////// scriptMap("+ scriptMap.size()+") /////////////////////\n");
        for(Map.Entry<Integer, Script> e : scriptMap.entrySet()) {
            Integer index = e.getKey();
            Script script = e.getValue();
            buf.append(script.toString() + "\n");
        }
        buf.append("////////////////////////////////////////////////////");
        return buf.toString();
    }

    public Sentence getQuiz() {
        if(selectedQuizs.isEmpty())
            return null;

        int selectedIndex = randomSelectOne();
        return selectedQuizs.get(selectedIndex);
    }

    public Object[] getQuizTitleAll() {
        return this.scriptMap.keySet().toArray();
    }

    private int randomSelectOne() {
        if(selectedQuizs.isEmpty())
            return 0;

        int totalQuizCnt = selectedQuizs.size();
        int selectedIndex = random.nextInt(totalQuizCnt);
        return selectedIndex;
    }
}

