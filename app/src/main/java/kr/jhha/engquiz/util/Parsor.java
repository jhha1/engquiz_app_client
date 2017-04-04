package kr.jhha.engquiz.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.local.Script;
import kr.jhha.engquiz.data.local.Sentence;

/**
 * Created by jhha on 2016-10-14.
 */

public class Parsor
{

    public static final String MainSeperator = "@@@";
    public static final String VeticalSeperator = "||";
    public static final String EqualSeperator = "=";

    public static List<Sentence> parse(String textFile)
    {
        if(isNull(textFile)) {
            Log.e("Parsor", "textFile is null");
            return null;
        }

        String rows[] = textFile.split(MainSeperator);
        if(rows == null) {
            Log.e("Parsor", "Cound not split with. text["+textFile+"]");
            return null;
        }

        // test log...
        for(String r : rows) {
            Log.d("[test]",r);
        }

        List<Sentence> sentences = new LinkedList<>();
        for(int i=0; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty()) {
                Log.w("Parsor", "row is empty");
                continue;
            }

            String[] dividedRow = row.split("\t");
            if(dividedRow.length != 2) {
                Log.w("Parsor", "split 'TAP' row count is :" + dividedRow.length);
                continue;
            }

            Sentence unit = new Sentence();
            unit.textKo = dividedRow[0].trim();
            unit.textEn = dividedRow[1].trim();
            sentences.add(unit);
        }
        Log.d("Parsor", "Sentences COUNT ("+ sentences.size() +")");
        return sentences;
    }

    /*
          ID@@@Title
          ex) 16@@@Unit 5 ....
     */
    public static String[] splitParsedScriptTitleAndId( String before ) {
        String[] divideStr = before.split( Parsor.MainSeperator);
        if( divideStr == null || divideStr.length != 2 ) {
            Log.e("###########", "Failed scplit with ParsedSciprt's Title and Id (" + before + ")");
            return null;
        }
        return divideStr;
    }

    public static Map<String, String> parseUserInfo( String textdump ){
        if(StringHelper.isNullString(textdump)) {
            throw new IllegalArgumentException("textdump is null (" + textdump + ")");
        }

        String[] splitRows = textdump.split(VeticalSeperator);
        if( splitRows == null  ) {
            throw new IllegalArgumentException("Invalied UserInfo. Split Rows are null  (" + textdump + ")");
        }

        Map<String, String> userInfo = new HashMap<>();
        for( String row : splitRows ){
            String[] splitStr = textdump.split(EqualSeperator);
            if( splitStr == null || splitStr.length < 2 ) {
                throw new IllegalArgumentException("Invalied UserInfo. Split Row. " +
                        "row count(" + ((splitStr!=null)?splitStr.length:null) + ")");
            }
            String key = splitStr[0];
            String value = splitStr[1];
            if( StringHelper.isNullString(key) || StringHelper.isNullString(value) ){
                throw new IllegalArgumentException("Invalied UserInfo. null key or value. "+
                        "key("+key+"),value("+value+")");
            }
            userInfo.put(key, value);
        }
        return userInfo;
    }


    private static Boolean isNull(String row) {
        return (row == null || row.isEmpty())? true : false;
    }
}

/*
        String textKo;
        String textEn;
        for(int i = 0; i < 100; ++i) {
            textKo = "["+ i + "]질문 질문 질문 질문 질문 질문 질문 질문?";
            textEn = "["+i+"] textEn strawberry banana caffelatte water is good for your health. lol";
            testdata.add(new Sentence(textKo, textEn));
        }*/