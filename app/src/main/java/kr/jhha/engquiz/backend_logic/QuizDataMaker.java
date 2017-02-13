package kr.jhha.engquiz.backend_logic;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizDataMaker
{

    public static final String QuizUnitSeperator = "@@@";

    public static Map<Integer, Script> parse(List<String> textfiles)
    {
        if(textfiles == null || textfiles.isEmpty()) {
            Log.e("QuizDataMaker.parse", "textFiles is null.");
            return Collections.emptyMap();
        }

        Map<Integer, Script> parsedMap = new HashMap<Integer, Script>();
        for(String text : textfiles) {
            Script script = parse(text);
            if(null == script) {
                System.out.println("[ERROR] parse failed. ");
                continue;
            }
            parsedMap.put(script.index, script);
        }
        Log.d("QuizDataMaker.parse", "COUNT (textScript:"+ textfiles.size()
                        + ", parsedScript:"+parsedMap.size() +")");

        return parsedMap;
    }

    public static Script parse(String textFile)
    {
        if(isNull(textFile)) {
            Log.e("QuizDataMaker", "textFile is null");
            return null;
        }

        String rows[] = textFile.split(QuizUnitSeperator);
        if(rows.length <= 3) {
            Log.e("QuizDataMaker", "Cound not split with. text["+textFile+"]");
            return null;
        }

        for(String r : rows) {
            Log.d("[test]",r);
        }

        Script script = new Script();
        script.index = Integer.parseInt( rows[0] );
        script.revision = Integer.parseInt( rows[1] );
        script.title = rows[2];
        for(int i=3; i<rows.length; ++i)
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

            Sentence unit = new Sentence();
            unit.korean = new StringBuffer(dividedRow[0].trim());
            unit.english = new StringBuffer(dividedRow[1].trim());
            script.sentences.add(unit);
        }
        Log.d("QuizDataMaker", "COUNT (quizSetLen:"+ script.sentences.size() +")");
        return script;
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