package kr.jhha.engquiz.model.remote;

/**
 * Created by thyone on 2017-03-17.
 */

public class EProtocol2 {


    public enum PID {

        CheckExistUser(1001),
        SIGNIN(1002),
        LOGIN(1003),

        ParseSciprt(1100),
        DeleteScript(1201),

        AddUserQuizFolder(2001),
        DelUserQuizFolder(2002),
        GetUserQuizFolders(2003),
        GetUserQuizFolderDetail(2101),
        AddUserQuizFolderDetail(2102),
        DelUserQuizFolderDetail(2103),

        ChangePlayingQuizFolder(2301),


        SYNC(3001),
        Sync_SendResult(3002),

        Report_GetList(5001),
        Report_Send(5002),
        Report_Modify(5003),

        NONE(9999);

        private Integer value;
        PID( Integer value )
        {
            this.value = value;
        }
        public int toInt() {return value.intValue(); }
    }
}
