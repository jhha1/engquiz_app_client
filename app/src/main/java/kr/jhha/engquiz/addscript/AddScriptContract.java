package kr.jhha.engquiz.addscript;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptContract {

    interface View {
    }

    interface ActionsListener {
        void addScript( String pdfFilepath, String pdfFilename );
    }
}
