package kr.jhha.engquiz.model.local;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalArgumentException;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.util.Parsor.MainSeperator;


/**
 * Created by jhha on 2016-10-24.
 */

public class Script
{
    public Integer scriptId = 0;
    public String title = "";
    public List<Sentence> sentences = new LinkedList<Sentence>();

    public static final int STATE_NONE = 0;
    public static final int STATE_QUIZPLAYING_SCRIPT = 1;
    public static final int STATE_ADDED_SCRIPT = 2;
    public static final int STATE_NON_ADDED_SCRIPT = 3;
    public static final int STATE_NEWBUTTON = 4;
    public static final int STATE_DESCRIPTION = 5;

    public final static String Field_SCRIPT_ID = "SCRIPT_ID";
    public final static String Field_SCRIPT_TITLE = "SCRIPT_TITLE";
    public final static String Field_SENTENCES = "SENTENCES";

    public static final String TEXT_ADD_SCRIPT = "Add Script";

    public final static int CUSTOM_SCRIPT_ID_MIN = 10000;

    public Script() {}
    public Script( String filename, Integer index,
                  List<Sentence> sentences )
    {
        this.title = filename;
        this.scriptId = index;
        this.sentences = sentences;
    }

    public Script( String textScript )
    {
        if( textScript == null || textScript.isEmpty() ) {
            MyLog.e("invalied param. param script is null");
            return;
        }

        String rows[] = textScript.split(MainSeperator);
        if(rows.length <= 2) {
            MyLog.e("invalied param. invalid param format: "+textScript);
            return;
        }

        String title = rows[1];
        int index = Integer.parseInt( rows[0] );
        LinkedList<Sentence> sentences = new LinkedList<Sentence>();
        for(int i=2; i<rows.length; ++i)
        {
            String row = rows[i];
            if(row.isEmpty())
                continue;

            String[] dividedRow = row.split("\t");
            if(dividedRow.length != 2)
                continue;

            Sentence unit = new Sentence();
            unit.textKo = dividedRow[0].trim();
            unit.textEn = dividedRow[1].trim();
            sentences.add(unit);
        }

        this.title = title;
        this.scriptId = index;
        this.sentences = sentences;
    }



    public static boolean isNull( Script script ){
        if( script == null ){
            return true;
        }

        if( StringHelper.isNull(script.title)
                && script.scriptId == 0
                && (script.sentences == null
                    || script.sentences.isEmpty()))
            return true;

        return false;
    }

    public static boolean checkScriptID( Object scriptID ) {
        if( scriptID instanceof Integer ) {
            return checkScriptID( (Integer)scriptID );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptID Type is not Integer. sentenceId:"+scriptID );
        }
    }

    public static boolean checkScriptID( Integer scriptID ) {
        if( scriptID <= 0){
            return false;
        }
        return true;
    }

    public static Integer createCustomScriptID()
    {
        Integer lastCustomScriptID = CUSTOM_SCRIPT_ID_MIN;
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        Integer[] scriptIds = scriptRepo.getScriptIdAll();
        if( scriptIds != null ) {
            for (Integer id : scriptIds) {
                lastCustomScriptID = (lastCustomScriptID < id) ? id : lastCustomScriptID;
            }
        }
        return lastCustomScriptID + 1;
    }

    public static String fileName2ScriptTitle( String filename ) {
        String objectScriptName = StringHelper.EMPTY_STRING;
        if( filename.contains(".pdf"))
            objectScriptName = Parsor.removeExtensionFromScriptTitle(filename, ".pdf");
        if( filename.contains(".txt"))
            objectScriptName = Parsor.removeExtensionFromScriptTitle(filename, ".txt");
        return objectScriptName;
    }

    public static String scriptTitle2FileName( String scriptTitle ) {
        return scriptTitle2FileName(scriptTitle, ".pdf");
    }

    public static String scriptTitle2FileName( String scriptTitle, String extension ) {
        if( StringHelper.isNull(scriptTitle)) {
            return StringHelper.EMPTY_STRING;
        }

        if( StringHelper.isNull(extension)){
            return scriptTitle + ".pdf";
        } else {
            return scriptTitle + extension;
        }
    }

    /*
        Serialize / Deserialize
     */
    // make file header for Memory -> Client file
    public String serializeFileDataHeader() {
        if( StringHelper.isNull(this.title) ){
            MyLog.e("cannot make script file name. script title is null");
            return null;
        }

        String title_pdf_removed = Parsor.removeExtensionFromScriptTitle(title, ".pdf");
        return serializeFileDataHeader(this.scriptId, title_pdf_removed);
    }

    public static String serializeFileDataHeader(Integer scriptId, String scriptTitle) {
        if( scriptId <= 0 || StringHelper.isNull(scriptTitle)){
            return new String();
        }

        StringBuffer filename = new StringBuffer();
        filename.append(scriptId + MainSeperator);
        filename.append(scriptTitle+ ".txt");
        return filename.toString();
    }

    // make file body for Memory -> Client file
    public String serializeFileBody() {
        StringBuffer fileText = new StringBuffer();
        for(Sentence unit : sentences)
        {
            fileText.append( Sentence.serializeFileData(unit) );
        }
        return fileText.toString();
    }

    // parse file body for Client file -> Memory
    public static Script deserializeFileDataBody( Integer scriptId, String scriptTitle, String fileText )
    {
        // create script object
        Script script = new Script();
        script.scriptId = scriptId;
        script.title = scriptTitle;

        boolean bEmptyScript = StringHelper.isNull(fileText);
        if( bEmptyScript ) {
            script.sentences = new ArrayList<Sentence>();
        } else {
            String rows[] = fileText.split(MainSeperator);
            if(rows == null) {
                MyLog.e("Cound not split with. text["+fileText+"]");
                return null;
            }

            for(int i=0; i<rows.length; ++i)
            {
                String row = rows[i];
                Sentence sentence = Sentence.deserializeFileData(scriptId, row);
                if(Sentence.isNull(sentence)) {
                    MyLog.w("row is null. but ignore it and continue. scriptId:"+scriptId);
                    continue;
                }
                script.sentences.add(sentence);
            }
        }

        MyLog.d("Sentences COUNT ("+ script.sentences.size() +")");

        return script;
    }

    /*
        Deserialize a AddScript Server CreateScriptResult
     */
    public static Script deserializeServerData(Map<String , Object> map )
    {
        Script script = new Script();
        try {
            int id = (Integer) map.get(Field_SCRIPT_ID);
            String title = (String) map.get(Field_SCRIPT_TITLE);
            List<Map<String, Object>> sentencesMap = (List<Map<String, Object>>) map.get(Field_SENTENCES);

            if( id < 0 )
                throw new Exception("invalid scriptId:"+id);
            if( title == null || title.isEmpty() )
                throw new Exception("invalid title:"+title);
            if( sentencesMap == null || sentencesMap.size() == 0 )
                throw new Exception("invalid sentencesMap:"+sentencesMap);

            // unserialize Sentence Objects
            List<Sentence> unserializedSentences = new LinkedList<Sentence>();
            for( Map<String, Object> sentence : sentencesMap ) {
                Sentence unserializedSentence = Sentence.deserializeServerData(sentence);
                if( Sentence.isNull(unserializedSentence) ) {
                    MyLog.e("Failed To UnSerialize a Sentence. " +
                            "this sentence is not adding into the Script.");
                    continue;
                }
                unserializedSentences.add( unserializedSentence );
            }
            if( unserializedSentences == null || unserializedSentences.size() == 0 )
                throw new Exception("invalid parsedSentences:"+unserializedSentences);

            script.scriptId = id;
            script.title = title;
            script.sentences = unserializedSentences;
            return script;

        } catch ( Exception e ) {
            MyLog.e("ERROR invalid param. " +
                    "map[" + script.toString() + "]");
            e.printStackTrace();
            return null;
        }
    }

    // just for logging
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{[scriptId: "+ scriptId +"], " +
                "[title: "+ title +"], " +
                "[sentence count("+ sentences.size() +").. ");
        for(Object o : sentences) {
            Sentence s = (Sentence) o;
            buf.append("\n[" + s.toString() + "]");
        }
        buf.append("]}");
        return buf.toString();
    }
}
