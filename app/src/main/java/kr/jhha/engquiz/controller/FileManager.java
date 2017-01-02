package kr.jhha.engquiz.controller;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.model.Const;

/**
 * Created by jhha on 2016-10-14.
 */

public class FileManager
{
    private static final FileManager singletonInstance = new FileManager();


    private FileManager() {}

    public static FileManager getInstance() {
        return singletonInstance;
    }

    public List<String> uploadParsedFiles()
    {
        String path = getAndroidAbsolutePath( Const.ParsedFile_AndroidPath );
        File files[] = getFileList( Const.ParsedFile_AndroidPath  );
        if( files == null ) {
            Log.e("Tag", "uploadParsedFiles is failed. Directory is null. path: " + path);
            return null;
        }
        System.out.println("[DEBUG] Dir("+path+"), accessedFileCount("+files.length+")");

        List<String> scriptTextList = new LinkedList<String>();
            for( File file : files ) {
            String scriptText = readFile(path, file.getName());
            if( scriptText.isEmpty() ) {
                System.out.println("[ERROR] scriptText is empty. file["+path+"/"+file.getName()+"]");
                continue;
            }
            scriptTextList.add( scriptText );
        }
        return scriptTextList;
    }

    public Boolean isRootDirectory( String dirPath )
    {
        File root = getRootDirectory();
        if(root == null)
            return null;

        return root.getAbsolutePath().equals(dirPath);
    }

    public File getRootDirectory()
    {
        File file = null;
        String sdcard = Environment.getExternalStorageState();
        if(false == sdcard.equals(Environment.MEDIA_MOUNTED)) {
            // sd카드가 마운트 안되있음
            file = Environment.getRootDirectory();
        } else {
            // sd카드가 마운트되있음
            file = Environment.getExternalStorageDirectory();
        }

        if(file == null)
            Log.e("Tag","root dir is null. " +
                    "sdcard mounted? " + sdcard.equals(Environment.MEDIA_MOUNTED));

        return file;
    }

    public Boolean isAbsoluteDirectory( String dirPath )
    {
        File root = getRootDirectory();
        if(root == null)
            return false;

        String absoluteRootPath = root.getAbsolutePath();
        if( isEmptyString(absoluteRootPath) )
            return false;

        return dirPath.contains(absoluteRootPath);
    }

    public String getAndroidAbsolutePath(String path)
    {
        if( isEmptyString( path ) ) {
            Log.e("Tag", "File Path is null. path:["+ path +"]");
            return null;
        }
        if( isAbsoluteDirectory( path ) )
            return path;

        File root = getRootDirectory();
        if( root == null )
            return null;

        String absolutePath = root.getAbsolutePath() + path;
        File file = new File( absolutePath );
        if(false == file.exists()) {
            file.mkdirs();
        }
        return absolutePath;
    }

    private boolean isEmptyString( String str ) {
        return ( str == null || str.isEmpty() );
    }

    public File[] getFileList( String path )
    {
        if( isEmptyString( path ) ) {
            Log.e("Tag", "File Path is null. path:["+ path +"]");
            return null;
        }

        path = getAndroidAbsolutePath( path );
        File directory = new File( path );
        if( directory == null ) {
            Log.e("Tag", "Directory is null. path: " + path);
            return null;
        }

        File filelist[] = directory.listFiles();
        if( filelist == null ) {
            Log.e("Tag", "filelist in directory is null. path: " + path);
            return null;
        }
        return filelist;
    }

    public String getParentDirectoryName( String childDirPath )
    {
        if( isEmptyString( childDirPath ) )
            return new String();

        File file = new File(childDirPath);
        if( file == null )
            return new String();

        String parentDir = file.getParent();
        if( parentDir == null )
            return new String();

        // 원하는건 물리적 최상위 root경로(/)가 아니라,
        // 가상 vm의 root경로( storage/emulate/0 ) 이다.
        if( Const.TOP_ROOT_DIR.equals(parentDir) )
            return getRootDirectory().getAbsolutePath();

        return FileManager.getInstance().getAndroidAbsolutePath(parentDir);
    }

    private String readFile(String path, String name)
    {
        if( isEmptyString( path ) || isEmptyString( name ) ) {
            Log.e("Tag", "path or name is null. path["+ path +"], name["+name+"]");
            return new String();
        }

        System.out.println("[DEBUG] fileName("+ path +"/" + name +")");
        try {
            StringBuffer buffer= new StringBuffer();
            File file= new File(path, name);
            FileReader in= new FileReader(file);
            BufferedReader reader= new BufferedReader(in);

            String str= reader.readLine();
            while( str!=null ){
                buffer.append(str);
                str= reader.readLine(); //한 줄씩 읽어오기
            }

            //text.setText(buffer.toString());
            reader.close();
            //System.out.println("[buffer] " + buffer.toString());
            return buffer.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String();
    }


}