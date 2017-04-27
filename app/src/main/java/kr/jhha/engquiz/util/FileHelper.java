package kr.jhha.engquiz.util;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by jhha on 2016-10-14.
 */

public class FileHelper
{
    private final static String TAG = "FileHelper";

    private static final FileHelper singletonInstance = new FileHelper();

    public static final String TOP_ROOT_DIR = "/";
    public static final String KaKaoDownloadFolder_AndroidPath = "/KakaoTalkDownload/";
    public static final String AppRoot_AndroidPath = "/SoynaClassEnglishGame/";
    public static final String ParsedFile_AndroidPath = AppRoot_AndroidPath + "parsed/";
    public static final String UserInfoFolderPath = AppRoot_AndroidPath + "User/";
    public static final String UserInfoFileName = "userInfo.txt";

    private FileHelper() {}

    public static FileHelper getInstance() {
        return singletonInstance;
    }

    public List<String> uploadParsedTextFiles() throws IOException {
        String path = getAndroidAbsolutePath( ParsedFile_AndroidPath );
        File files[] = listFiles( ParsedFile_AndroidPath  );
        if( files == null ) {
           MyLog.e( "uploadParsedTextFiles is failed. Directory is null. path: " + path);
            return null;
        }
        MyLog.d("Dir("+path+"), accessedFileCount("+files.length+")");

        List<String> scriptTextList = new LinkedList<String>();
            for( File file : files ) {
            String scriptText = readFile(ParsedFile_AndroidPath, file.getName());
            if( scriptText.isEmpty() ) {
                MyLog.e("scriptText is empty. file["+path+"/"+file.getName()+"]");
                continue;
            }
            scriptTextList.add( scriptText );
        }
        return scriptTextList;
    }

    public Boolean isRootDirectory( String dirPath )
    {
        File root = getRootDirectory();
        if(root == null) {
            MyLog.e("Root Dir is null");
            return null;
        }

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
           MyLog.e("root dir is null. " +
                    "sdcard mounted? " + sdcard.equals(Environment.MEDIA_MOUNTED));

        return file;
    }

    public Boolean isAbsoluteDirectory( String dirPath )
    {
        File root = getRootDirectory();
        if(root == null) {
            MyLog.e("Root Dir is null");
            return false;
        }

        String absoluteRootPath = root.getAbsolutePath();
        if( StringHelper.isNull(absoluteRootPath) ) {
            MyLog.e("absoluteRootPath is null");
            return false;
        }

        return dirPath.contains(absoluteRootPath);
    }

    public String getAndroidAbsolutePath(String path)
    {
        if( StringHelper.isNull( path ) ) {
            MyLog.e("File Path is null. path:["+ path +"]");
            return null;
        }
        if( isAbsoluteDirectory( path ) )
            return path;

        File root = getRootDirectory();
        if( root == null ) {
            MyLog.e("RootDirectory is null");
            return null;
        }

        String absolutePath = root.getAbsolutePath() + path;
        File file = new File( absolutePath );
        if(false == file.exists()) {
            file.mkdirs();
        }
        return absolutePath;
    }

    public List listFileNames( String dirPath ) {
        File[] files = listFiles( dirPath );
        if( files == null ) {
            MyLog.e("listFiles is null");
            return Collections.EMPTY_LIST;
        }

        List<String> fileNames = new LinkedList<>();
        for( File file : files ) {
            fileNames.add( file.getName() );
        }
        return fileNames;
    }

    public File[] listFiles( String dirPath )
    {
        if( StringHelper.isNull( dirPath ) ) {
            MyLog.e("File Path is null. path:["+ dirPath +"]");
            return null;
        }

        dirPath = getAndroidAbsolutePath( dirPath );
        File directory = new File( dirPath );
        if( directory == null ) {
            MyLog.e("Directory is null. path: " + dirPath);
            return null;
        }

        File filelist[] = directory.listFiles();
        if( filelist == null ) {
            MyLog.e("directory is null. path: " + dirPath);
            return null;
        }
        // 디렉토리안에 파일이 없을때,
        // directory.listFiles() 에서 ".txt"라는 파일이 하나 있다고 리턴함. <- 버그인듯.
        // 하여, 빈 디렉토리인지는 아래와 같이 체크.
        if((filelist.length == 1 && ".txt".equals(filelist[0].getName())) ) {
            MyLog.e("directory is empty. path: " + dirPath);
            return null;
        }
        return filelist;
    }

    public String getParentDirectoryName( String childDirPath )
    {
        if( StringHelper.isNull( childDirPath ) ) {
            MyLog.e("childDirPath is null");
            return new String();
        }

        File file = new File(childDirPath);
        if( file == null ) {
            MyLog.e("childDirPath - File is null");
            return new String();
        }

        String parentDir = file.getParent();
        if( parentDir == null ) {
            MyLog.e("parentDir is null");
            return new String();
        }

        // 원하는건 물리적 최상위 root경로(/)가 아니라,
        // 가상 vm의 root경로( storage/emulate/0 ) 이다.
        if( TOP_ROOT_DIR.equals(parentDir) )
            return getRootDirectory().getAbsolutePath();

        return FileHelper.getInstance().getAndroidAbsolutePath(parentDir);
    }

    // ROUND_FOCTOR : 소수점 n째 자리에서 반올림
    // (ex. ROUND_FOCTOR == 2 <- 둘째자리에서 반올림)
    static final int ROUND_FOCTOR = 1;
    public float getFileMegaSize( String filepath, String filename )
    {
        long bytesize = getFileByteSize(filepath, filename);
        if( bytesize <= 0) {
            MyLog.e("bytesize <= 0");
            return 0;
        }

        float megaSize = ((float)bytesize)/1000000;
        float n = 1f;
        for(int i = 0; i< ROUND_FOCTOR; ++i) {
            n *= 10f;
        }
        return Math.round(megaSize * n) / n;
    }

    public Long getFileByteSize( String filepath, String filename ) {
        if( StringHelper.isNull( filepath ) || StringHelper.isNull( filename ) ) {
            MyLog.e("path or name is null. path["+ filepath +"], name["+filename+"]");
            return 0L;
        }

        File file = new File( filepath + "/" + filename );
        if(file == null) {
            MyLog.e("no exist file. path["+ filepath +"], name["+filename+"]");
            return 0L;
        }
        return file.length();
    }

    public String readFile(String dir, String name) {
        dir = getAndroidAbsolutePath( dir );
        if( StringHelper.isNull( dir ) || StringHelper.isNull( name ) ) {
            MyLog.e("invalid filePath or fileName. dir["+ dir +"], name["+name+"]");
            return StringHelper.EMPTY_STRING;
        }
        String path = dir + name;
        return readFile( path );
    }

    public String readFile( String path )
    {
        MyLog.d("filepath("+ path +")");

        StringBuffer buffer= new StringBuffer();
        File file= new File(path);
        if( ! file.exists() ) {
            MyLog.e("file no exist. path:"+path);
            throw new MyIllegalStateException(EResultCode.NOEXSIT);
        }

        try
        {
            FileReader in = new FileReader(file);
            BufferedReader reader = new BufferedReader(in);

            String str = reader.readLine();
            while (str != null) {
                buffer.append(str);
                str = reader.readLine(); //한 줄씩 읽어오기
            }

            reader.close();
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return StringHelper.EMPTY_STRING;
        }
    }

    public byte[] readBinary( String filepath, String filename )
    {
        if (StringHelper.isNull(filepath) || StringHelper.isNull(filename)) {
           MyLog.e("path or name is null. path[" + filepath + "], name[" + filename + "]");
            return null;
        }

        String fileFullPath = filepath + "/" + filename;
        BufferedInputStream bs = null;
        try
        {
            File file = new File(fileFullPath);
            FileInputStream fis = new FileInputStream(fileFullPath);
            int fileSize = (int)file.length();
            byte [] b = new byte [fileSize]; //임시로 읽는데 쓰는 공간
            MyLog.d("filePath("+ fileFullPath+"), fileLen("+b.length+")");
            fis.read(b);

            // System.out.println(new String(b)); //필요에 따라 스트링객체로 변환
            return b;
        } catch (Exception e) {
            MyLog.e("Failed readBinary. path("+ filepath +") name("+filename+")");
            e.printStackTrace();
        } finally {
            try {
                bs.close(); //반드시 닫는다.
            } catch (Exception e) {
                MyLog.e("Failed to close file IO. path("+ filepath +") name("+filename+")");
                e.printStackTrace();
            }
        }
        return null;
    }

    // 파일이 존재하면 덮어씀
    public boolean overwrite(String dirPath, String fileName, String text)
    {
        boolean bDeleted = deleteFile(dirPath, fileName);
        if( ! bDeleted ) {
            MyLog.e("Failed deleteFile IO. path("+ dirPath +") name("+fileName+")");
            return false;
        }

        return write( dirPath, fileName, text );
    }

    private boolean write(String dirPath, String fileName, String text)
    {
        boolean bMade = makeDirectoryIfNotExist(dirPath);
        if( bMade == false ){
            MyLog.e("Failed to write. Make Directory is failed. dir["+dirPath+"]");
            return false;
        }

        try{
            String absoluteDirPath = getAndroidAbsolutePath(dirPath);
            String filePath = absoluteDirPath + fileName;

            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
            BufferedWriter fw = new BufferedWriter(new FileWriter(filePath, true));

            MyLog.d("WRITE TextFile. filePath["+filePath+"] .");
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

    public boolean makeDirectoryIfNotExist( String directoryPath ){
        String absoluteDirectoryPath = getAndroidAbsolutePath(directoryPath);
        File file = new File( absoluteDirectoryPath );
        if( false == file.exists() ) {
            boolean bMadeDirectory = file.mkdirs();
            if( false  == bMadeDirectory ) {
                MyLog.e("Failed make directory. absoluteDirPath["+absoluteDirectoryPath+"]");
                return false;
            }
        }
        return true;
    }

    public boolean createFile( String directoryPath, String fileName ){
        String absoluteDirectoryPath = getAndroidAbsolutePath(directoryPath);
        return write(absoluteDirectoryPath, fileName, StringHelper.EMPTY_STRING);
    }

    public boolean deleteFile(String dirPath, String fileName) {
        String absolutePath = getAndroidAbsolutePath(dirPath);
        String filePath = absolutePath + fileName;
        File file = new File( filePath );
        if(file.exists()) {
            Boolean bDeleted = file.delete();
            if(false == bDeleted) {
                MyLog.d("["+filePath+"] " + "delete failed.");
                return false;
            }
        }
        return true;
    }

    public boolean isExist(String releativeDirPath, String fileName){
        String absolutePath = getAndroidAbsolutePath(releativeDirPath);
        String filePath = absolutePath + fileName;
        File file = new File( filePath );
        return file.exists();
    }
}