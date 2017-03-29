package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizPlayModel
{
    private static final QuizPlayModel instance = new QuizPlayModel();
    private QuizPlayModel() {}
    public static QuizPlayModel getInstance() {
        return instance;
    }

    private List<Sentence> selectedQuizs = new ArrayList<Sentence>();
    private Random random = new Random();

    public void changePlayingQuizFolder( QuizFolder quizFolder ) {
        Log.i("AppContent", "QuizPlayModel changePlayingQuizFolder() called. quizFolder:"+((quizFolder!=null)?quizFolder.toString():null));
        if( quizFolder == null ){
            Log.e("CONTENT", "quiz folder is null");
            return;
        }

        this.selectedQuizs.clear();
        for(Integer scriptId : quizFolder.getScriptIds()) {
            Script script = ScriptRepository.getInstance().getScript( scriptId );
            if( script == null ) {
                Log.e("######", "Script is null. quizFolderId("+scriptId+")");
                continue;
            }
            Log.i("AppContent", "QuizPlayModel changePlayingQuizFolder() script title:"+script.title);
            for( Sentence sentence : script.sentences ) {
                this.selectedQuizs.add( sentence );
            }
        }
        Log.i("AppContent", "QuizPlayModel changePlayingQuizFolder() result selectedQuizs:"+selectedQuizs.toString());
    }

    public Sentence getQuiz() {
        if(selectedQuizs.isEmpty())
            return null;

        int selectedIndex = randomSelectOne();
        return selectedQuizs.get(selectedIndex);
    }

    private int randomSelectOne() {
        if(selectedQuizs.isEmpty())
            return 0;

        int totalQuizCnt = selectedQuizs.size();
        int selectedIndex = random.nextInt(totalQuizCnt);
        return selectedIndex;
    }
}

