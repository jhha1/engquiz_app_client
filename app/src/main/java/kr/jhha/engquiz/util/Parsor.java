package kr.jhha.engquiz.util;

import java.util.LinkedList;
import java.util.List;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by jhha on 2016-10-14.
 */

public class Parsor
{
    public static final String MainSeperator = "@@@";
    public static final String TabSeperator = "\t";
    public static final String VeticalSeperator = "||";
    public static final String EqualSeperator = "=";

    public static List<Sentence> parse(Integer scriptId, String textFile)
    {
        if(StringHelper.isNull(textFile)) {
            MyLog.e("textFile is null");
            return null;
        }

        String rows[] = textFile.split(MainSeperator);
        if(rows == null) {
            MyLog.e("Cound not split with. text["+textFile+"]");
            return null;
        }

        List<Sentence> sentences = new LinkedList<>();
        for(int i=0; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty()) {
                MyLog.w("row is empty");
                continue;
            }

            String[] dividedRow = row.split(TabSeperator);
            if(dividedRow.length != 3) {
                MyLog.w("split 'TAP' row count is :" + dividedRow.length);
                continue;
            }

            Sentence unit = new Sentence();
            unit.scriptId = scriptId;
            unit.sentenceId = Integer.parseInt(dividedRow[0].trim());
            unit.textKo = dividedRow[1].trim();
            unit.textEn = dividedRow[2].trim();
            sentences.add(unit);
        }
        MyLog.d("Sentences COUNT ("+ sentences.size() +")");
        return sentences;
    }

    /*
          ID@@@Title
          ex) 16@@@Unit 5 ....
     */
    public static String[] splitParsedScriptTitleAndId( String before ) {
        String[] divideStr = before.split( Parsor.MainSeperator);
        if( divideStr == null || divideStr.length != 2 ) {
            MyLog.e("Failed scplit with ParseSciprt's Title and Id (" + before + ")");
            return null;
        }
        divideStr[1] = removeExtensionFromScriptTitle(divideStr[1], ".txt");
        return divideStr;
    }

    public static String removeExtensionFromScriptTitle( String title, String extension ) {
        String[] divideStr = title.split(extension);
        if( divideStr == null) {
            MyLog.e("Failed remove extension from title. " +
                    "split result is null. " +
                    "Title (" + title + ")");
            return null;
        }
        return divideStr[0];
    }
}
