package kr.jhha.engquiz.controller;

/**
 * Created by thyone on 2016-12-28.
 */

public class AddScript {

    public AddScript() {}

    public boolean checkFileFormat( String filename ) {
        // 일단 pdf파일인지만 체크
        String PDF = ".pdf";
        boolean bPDFFile = PDF.contains(filename);

        //TODO
        return true;
    }

    public String getAbsoluteFilePath( String path ) {
        return FileManager.getInstance().getAndroidAbsolutePath(path);
    }

    public void addScript( String filepath )
    {

    }
}