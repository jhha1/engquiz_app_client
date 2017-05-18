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
    public static Integer extractScriptId( String fileName ){
        if( StringHelper.isNull(fileName)){
            MyLog.e("Failed extractScriptId. fileName is null");
            return -1;
        }

        String[] divideStr = fileName.split(Parsor.MainSeperator);
        if( divideStr == null || divideStr.length != 2 ) {
            MyLog.e("Failed extractScriptId. invalid filename (" + fileName + ")");
            return -1;
        }
        String scriptIdString = divideStr[0];
        if( StringHelper.isNull(scriptIdString)){
            MyLog.e("Failed extractScriptId. scriptIdString is null. filename("+fileName+")");
            return -1;
        }
        return Integer.parseInt(scriptIdString);
    }

    /*
          ID@@@Title
          ex) 16@@@Unit 5 ....
     */
    public static String extractScriptTitle( String fileName ){
        if( StringHelper.isNull(fileName)){
            MyLog.e("Failed extractScriptTitle. fileName is null");
            return null;
        }

        String[] divideStr = fileName.split(Parsor.MainSeperator);
        if( divideStr == null || divideStr.length != 2 ) {
            MyLog.e("Failed extractScriptTitle. invalid filename (" + fileName + ")");
            return null;
        }

        String scriptTitle = divideStr[1];
        if( StringHelper.isNull(scriptTitle)){
            MyLog.e("Failed extractScriptTitle. scriptTitle is null. filename("+fileName+")");
            return null;
        }
        return removeExtensionFromScriptTitle(scriptTitle, ".txt");
    }

    public static String removeExtensionFromScriptTitle( String title, String extension ) {
        if( false == title.contains(extension) )
            return title;

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
