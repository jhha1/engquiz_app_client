package kr.jhha.engquiz.backend_logic;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jhha on 2016-10-14.
 */

public class FileManager
{
    private static final FileManager singletonInstance = new FileManager();

    public static final String ParsedFile_AndroidPath = "/MyEnglishQuizGame";
    public static final String KaKaoDownloadFolder_AndroidPath = "/KakaoTalkDownload";
    public static final String TOP_ROOT_DIR = "/";

    private FileManager() {}

    public static FileManager getInstance() {
        return singletonInstance;
    }

    public List<String> uploadParsedTextFiles()
    {
        String path = getAndroidAbsolutePath( ParsedFile_AndroidPath );
        File files[] = getFileList( ParsedFile_AndroidPath  );
        if( files == null ) {
            Log.e("Tag", "uploadParsedTextFiles is failed. Directory is null. path: " + path);
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
            Log.e("Tag", "directory is null. path: " + path);
            return null;
        }
        // 디렉토리안에 파일이 없을때,
        // directory.listFiles() 에서 ".txt"라는 파일이 하나 있다고 리턴함. <- 버그인듯.
        // 하여, 빈 디렉토리인지는 아래와 같이 체크.
        if((filelist.length == 1 && ".txt".equals(filelist[0].getName())) ) {
            Log.e("Tag", "directory is empty. path: " + path);
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
        if( TOP_ROOT_DIR.equals(parentDir) )
            return getRootDirectory().getAbsolutePath();

        return FileManager.getInstance().getAndroidAbsolutePath(parentDir);
    }

    // roundFactor : 소수점 n째 자리에서 반올림
    // (ex. roundFactor == 2 <- 둘째자리에서 반올림)
    public float getFileMegaSize( String filepath, String filename, int roundFactor ) {
        long bytesize = getFileByteSize(filepath, filename);
        if( bytesize <= 0)
            return 0;

        float megaSize = ((float)bytesize)/1000000;
        float n = 1f;
        for(int i=0; i<roundFactor; ++i) {
            n *= 10f;
        }
        return Math.round(megaSize * n) / n;
    }

    public Long getFileByteSize( String filepath, String filename ) {
        if( isEmptyString( filepath ) || isEmptyString( filename ) ) {
            Log.e("Tag", "path or name is null. path["+ filepath +"], name["+filename+"]");
            return 0L;
        }

        File file = new File( filepath + "/" + filename );
        if(file == null) {
            Log.e("Tag", "no exist file. path["+ filepath +"], name["+filename+"]");
            return 0L;
        }
        return file.length();
    }

    public String readFile(String path, String name)
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

    public byte[] readBinary( String filepath, String filename )
    {
        if (isEmptyString(filepath) || isEmptyString(filename)) {
            Log.e("Tag", "path or name is null. path[" + filepath + "], name[" + filename + "]");
            return null;
        }

        String fileFullPath = filepath + "/" + filename;
        BufferedInputStream bs = null;
        try
        {

            File file = new File(fileFullPath);
            FileInputStream fis = new FileInputStream(fileFullPath);
            int filesize = (int)file.length();
            //bs = new BufferedInputStream(new FileInputStream( fileFullPath ));
            byte [] b = new byte [filesize]; //임시로 읽는데 쓰는 공간
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + fileFullPath+", "+b.length);
            fis.read(b);
            //while( bs.read(b) != -1) {}

           // System.out.println(new String(b)); //필요에 따라 스트링객체로 변환

            return b;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            try
            {
                bs.close(); //반드시 닫는다.
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        return null;
    }

    // 파일이 존재하면 덮어씀
    public boolean overwrite(String filePath, String fileName, String text)
    {
        String filefullpath = getAndroidAbsolutePath(filePath + "/" + fileName) + ".txt";
        System.out.println("[DEBUG] WRITE TextFile. path["+filefullpath+"] .");

        File file = new File(filefullpath);
        if(file.exists()) {
            Boolean bDeleted = file.delete();
            if(false == bDeleted) {
                System.out.println("["+filefullpath+"] " + "delete failed.");
                return false;
            }
        }

        return write(filefullpath, text);
    }

    private boolean write(String filepath, String text)
    {
        try{

            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
            BufferedWriter fw = new BufferedWriter(new FileWriter(filepath, true));

            //System.out.println("wirte["+fileName+"] " + text);
            // 파일안에 문자열 쓰기
            fw.write(text);
            fw.flush();

            // 객체 닫기
            fw.close();

            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}