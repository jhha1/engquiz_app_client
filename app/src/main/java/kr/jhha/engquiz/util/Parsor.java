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
