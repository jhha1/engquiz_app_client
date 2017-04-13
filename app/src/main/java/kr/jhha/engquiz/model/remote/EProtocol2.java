package kr.jhha.engquiz.model.remote;

/**
 * Created by thyone on 2017-03-17.
 */

public class EProtocol2 {


    public enum PID {

        CheckExistUser(1001),
        SIGNIN(1002),
        LOGIN(1003),
        ParseSciprt(1004),

        AddUserQuizFolder(1006),
        DelUserQuizFolder(1007),
        GetUserQuizFolders(1008),
        AddUserQuizFolderDetail(1009),
        DelUserQuizFolderDetail(1010),
        GetUserQuizFolderDetail(1011),
        ChangePlayingQuizFolder(1012),
        Report_GetList(1013),
        Report_Send(1014),
        Report_Modify(1015),
        SYNC(1020),
        Sync_SendResult(1021),

        NONE(9999);

        private Integer value;
        PID( Integer value )
        {
            this.value = value;
        }
        public int toInt() {return value.intValue(); }
    }
}
