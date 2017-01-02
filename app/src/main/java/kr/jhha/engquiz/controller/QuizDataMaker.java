package kr.jhha.engquiz.controller;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.model.*;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizDataMaker
{

    public static Map<String, QuizList> parse(List<String> textfiles)
    {
        if(textfiles == null) {
            Log.e("QuizDataMaker.parse", "textFiles is null.");
            return Collections.emptyMap();
        }

        Map<String, QuizList> parsedMap = new HashMap<String, QuizList>();
        for(String text : textfiles) {
            QuizList script = parse(text);
            if(null == script) {
                System.out.println("[ERROR] parse failed. ");
                continue;
            }
            parsedMap.put(script.title, script);
        }
        Log.d("QuizDataMaker.parse", "COUNT (textScript:"+ textfiles.size()
                        + ", parsedScript:"+parsedMap.size() +")");

        return parsedMap;
    }

    public static QuizList parse(String textFile)
    {
        if(isNull(textFile)) {
            Log.e("QuizDataMaker", "textFile is null");
            return null;
        }

        String rows[] = textFile.split(Const.QuizUnitSeperator);
        if(rows.length <= 1) {
            Log.e("QuizDataMaker", "Cound not split with. text["+textFile+"]");
            return null;
        }

        for(String r : rows) {
            Log.d("[test]",r);
        }

        QuizList quizSet = new QuizList();
        quizSet.title = rows[0];
        for(int i=1; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty()) {
                Log.w("QuizDataMaker", "row is empty");
                continue;
            }

            String[] dividedRow = row.split("\t");
            if(dividedRow.length != 2) {
                Log.w("QuizDataMaker", "split 'TAP' row count is :" + dividedRow.length);
                continue;
            }

            QuizUnit unit = new QuizUnit();
            unit.korean = new StringBuffer(dividedRow[0].trim());
            unit.english = new StringBuffer(dividedRow[1].trim());
            quizSet.quizList.add(unit);
        }
        Log.d("QuizDataMaker", "COUNT (quizSetLen:"+ quizSet.quizList.size() +")");
        return quizSet;
    }

    private static Boolean isNull(String row) {
        return (row == null || row.isEmpty())? true : false;
    }
}

/*
        String korean;
        String english;
        for(int i = 0; i < 100; ++i) {
            korean = "["+ i + "]질문 질문 질문 질문 질문 질문 질문 질문?";
            english = "["+i+"] english strawberry banana caffelatte water is good for your health. lol";
            testdata.add(new Sentence(korean, english));
        }*/