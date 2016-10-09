package ua.kyslytsia.tct.database;

import android.provider.BaseColumns;

public class Contract {

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    //private static final String FOREIGN_KEY = " FOREIGN KEY ";
    private static final String REFERENCES = " REFERENCES ";
    private static final String SPACE_BRACKET = " (";
    private static final String COMMA_SPACE = ", ";
    private static final String BRACKET_SEMICOLON ="); ";
    private static final String INSERT_INTO = "INSERT INTO ";


    public static final class GenderEntry implements BaseColumns {
        public static final String TABLE_NAME = "gender";
        public static final String COLUMN_GENDER = "gender";
    }

    public static final class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_GENDER_ID = "gender_id";
        public static final String COLUMN_BIRTHDAY = "birthday";
    }

    // for different type of tourism as bike, hike, mountain etc.
    public static final class TypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "type";
        public static final String COLUMN_NAME = "name";
    }

    public static final class DistanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "distance";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_DISTANCE_NAME = "name";
    }

    public static final class CompetitionEntry implements BaseColumns {
        public static final String TABLE_NAME = "competition";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PLACE = "place";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_DISTANCE_ID = "distance_id";
        public static final String COLUMN_RANK = "rank";
        public static final String COLUMN_PENALTY_TIME = "penalty_time";
        public static final String COLUMN_IS_CLOSED = "is_closed";
    }
    public static final int COMPETITION_OPENED = 0;
    public static final int COMPETITION_CLOSED = 1;

    public static final class StageEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage";
        public static final String COLUMN_DISTANCE_ID = "distance_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION_MOUNT = "description_mount";
        public static final String COLUMN_DESCRIPTION_PENALTY = "description_penalty";
        public static final String COLUMN_BITMAP = "bitmap";
    }

    public static final class JudgeEntry implements BaseColumns {
        public static final String TABLE_NAME = "judge";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_JUDGE_RANK = "judge_rank";
        public static final String COLUMN_POSITION = "position";
    }

    public static final class MemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "member";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_SPORT_RANK = "sport_rank";
        public static final String COLUMN_TEAM = "team";
        public static final String COLUMN_BIKE = "bike";
    }

    public static final class AttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "attempt";
        public static final String COLUMN_MEMBERS_ID = "members_id";
        public static final String COLUMN_TRY_NUMBER = "try_number";
        public static final String COLUMN_PENALTY_TOTAL = "penalty_total";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_IS_CLOSED = "is_closed";
    }

    public static final class StageOnCompetitionEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage_on_competition";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_STAGE_ID = "stage_id";
    }

    public static final class StageOnAttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage_on_attempt";
        public static final String COLUMN_STAGE_ON_COMPETITION_ID = "stage_on_competition_id";
        public static final String COLUMN_ATTEMPT_ID = "attempt_id";
        public static final String COLUMN_PENALTY = "penalty";
    }

    public static final String SQL_CREATE_GENDER_TABLE = CREATE_TABLE + GenderEntry.TABLE_NAME + SPACE_BRACKET +
            GenderEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            GenderEntry.COLUMN_GENDER + TEXT + NOT_NULL + BRACKET_SEMICOLON;
//            INSERT_INTO + GenderEntry.TABLE_NAME + " (" + GenderEntry.COLUMN_GENDER + ")" + " VALUES ('Муж'); " +
//            INSERT_INTO + GenderEntry.TABLE_NAME + " (" + GenderEntry.COLUMN_GENDER + ")" + " VALUES ('Жен'); ";

    public static final String SQL_CREATE_TYPE_TABLE = CREATE_TABLE + TypeEntry.TABLE_NAME + SPACE_BRACKET +
            TypeEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            TypeEntry.COLUMN_NAME + TEXT + NOT_NULL + BRACKET_SEMICOLON;
//            INSERT_INTO + TypeEntry.TABLE_NAME + " (" + TypeEntry.COLUMN_NAME + ")" + " VALUES ('Велосипедный');";

    public static final String SQL_CREATE_DISTANCE_TABLE = CREATE_TABLE + DistanceEntry.TABLE_NAME + SPACE_BRACKET +
            DistanceEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            DistanceEntry.COLUMN_TYPE_ID + INTEGER + NOT_NULL + REFERENCES + TypeEntry.TABLE_NAME + "(" + TypeEntry._ID + ")" + COMMA_SPACE + //FK OK
            DistanceEntry.COLUMN_DISTANCE_NAME + TEXT + NOT_NULL + BRACKET_SEMICOLON;
//            INSERT_INTO + DistanceEntry.TABLE_NAME + " (" + DistanceEntry.COLUMN_TYPE_ID + "," + DistanceEntry.COLUMN_DISTANCE_NAME + ")" + " VALUES (1, 'Фигурка');";

    public static final String SQL_CREATE_STAGE_TABLE = CREATE_TABLE + StageEntry.TABLE_NAME + SPACE_BRACKET +
            StageEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            StageEntry.COLUMN_DISTANCE_ID + INTEGER + NOT_NULL + REFERENCES + DistanceEntry.TABLE_NAME + "(" + DistanceEntry._ID + ")" + COMMA_SPACE +
            StageEntry.COLUMN_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            StageEntry.COLUMN_DESCRIPTION_MOUNT + TEXT + COMMA_SPACE +
            StageEntry.COLUMN_DESCRIPTION_PENALTY + TEXT +
            StageEntry.COLUMN_BITMAP + TEXT + BRACKET_SEMICOLON;
//            INSERT_INTO + StageEntry.TABLE_NAME + " (" + StageEntry.COLUMN_DISTANCE_ID + ", " + StageEntry.COLUMN_NAME + ")" + " VALUES (1, 'Восьмерка'); " +
//            INSERT_INTO + StageEntry.TABLE_NAME + " (" + StageEntry.COLUMN_DISTANCE_ID + ", " + StageEntry.COLUMN_NAME + ")" + " VALUES (1, 'Качеля'); " +
//            INSERT_INTO + StageEntry.TABLE_NAME + " (" + StageEntry.COLUMN_DISTANCE_ID + ", " + StageEntry.COLUMN_NAME + ")" + " VALUES (1, 'Колея'); " +
//            INSERT_INTO + StageEntry.TABLE_NAME + " (" + StageEntry.COLUMN_DISTANCE_ID + ", " + StageEntry.COLUMN_NAME + ")" + " VALUES (1, 'Стоп-линия'); ";

    public static final String SQL_CREATE_COMPETITION_TABLE = CREATE_TABLE + CompetitionEntry.TABLE_NAME + SPACE_BRACKET +
            CompetitionEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            CompetitionEntry.COLUMN_DATE + TEXT + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_PLACE + TEXT + COMMA_SPACE +
            CompetitionEntry.COLUMN_TYPE_ID + INTEGER + NOT_NULL + REFERENCES + TypeEntry.TABLE_NAME + "(" + TypeEntry._ID + ")" + COMMA_SPACE + //FK OK
            CompetitionEntry.COLUMN_DISTANCE_ID + INTEGER + NOT_NULL + REFERENCES + DistanceEntry.TABLE_NAME + "(" + DistanceEntry._ID + ")" + COMMA_SPACE + //FK OK
            CompetitionEntry.COLUMN_RANK + INTEGER + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_PENALTY_TIME + INTEGER + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_IS_CLOSED + INTEGER + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_PERSON_TABLE = CREATE_TABLE + PersonEntry.TABLE_NAME + SPACE_BRACKET +
            PersonEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            PersonEntry.COLUMN_SURNAME + TEXT + NOT_NULL + COMMA_SPACE +
            PersonEntry.COLUMN_FIRST_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            PersonEntry.COLUMN_MIDDLE_NAME + TEXT + COMMA_SPACE +
            PersonEntry.COLUMN_GENDER_ID + INTEGER + REFERENCES + GenderEntry.TABLE_NAME + "(" + GenderEntry._ID + ")" + COMMA_SPACE + //FK OK
            PersonEntry.COLUMN_BIRTHDAY + TEXT + BRACKET_SEMICOLON;

    protected static final String SQL_CREATE_JUDGES_TABLE = CREATE_TABLE + JudgeEntry.TABLE_NAME + SPACE_BRACKET +
            JudgeEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            JudgeEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE +  //FK OK
            JudgeEntry.COLUMN_PERSON_ID + INTEGER + NOT_NULL + REFERENCES + PersonEntry.TABLE_NAME + "(" + PersonEntry._ID + ")" + COMMA_SPACE + //FK OK
            JudgeEntry.COLUMN_JUDGE_RANK + TEXT + COMMA_SPACE +
            JudgeEntry.COLUMN_POSITION + TEXT + BRACKET_SEMICOLON;

    protected static final String SQL_CREATE_MEMBERS_TABLE = CREATE_TABLE + MemberEntry.TABLE_NAME + SPACE_BRACKET +
            MemberEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            MemberEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            MemberEntry.COLUMN_PERSON_ID + INTEGER + NOT_NULL + REFERENCES + PersonEntry.TABLE_NAME + "(" + PersonEntry._ID + ")" + COMMA_SPACE + //FK OK
            MemberEntry.COLUMN_SPORT_RANK + TEXT + COMMA_SPACE +
            MemberEntry.COLUMN_TEAM + TEXT + COMMA_SPACE +
            MemberEntry.COLUMN_BIKE + TEXT + BRACKET_SEMICOLON;

    protected static final String SQL_CREATE_ATTEMPT_TABLE = CREATE_TABLE + AttemptEntry.TABLE_NAME + SPACE_BRACKET +
            AttemptEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            AttemptEntry.COLUMN_MEMBERS_ID + INTEGER + NOT_NULL + REFERENCES + MemberEntry.TABLE_NAME + "(" + MemberEntry._ID + ")" + COMMA_SPACE + //FK OK
            AttemptEntry.COLUMN_TRY_NUMBER + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_PENALTY_TOTAL + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_TIME + TEXT + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_TOTAL + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_IS_CLOSED + INTEGER + NOT_NULL + BRACKET_SEMICOLON;

    protected static final String SQL_CREATE_STAGE_ON_COMPETITION_TABLE = CREATE_TABLE + StageOnCompetitionEntry.TABLE_NAME + SPACE_BRACKET +
            StageOnCompetitionEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            StageOnCompetitionEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnCompetitionEntry.COLUMN_STAGE_ID + INTEGER + NOT_NULL + REFERENCES + StageEntry.TABLE_NAME + "(" + StageEntry._ID + ")" + BRACKET_SEMICOLON; //FK OK

    protected static final String SQL_CREATE_STAGE_ON_ATTEMPT_TABLE = CREATE_TABLE + StageOnAttemptEntry.TABLE_NAME + SPACE_BRACKET +
            StageOnAttemptEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + NOT_NULL + COMMA_SPACE +
            StageOnAttemptEntry.COLUMN_STAGE_ON_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + StageOnCompetitionEntry.TABLE_NAME + "(" + StageOnCompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnAttemptEntry.COLUMN_ATTEMPT_ID + INTEGER + NOT_NULL + REFERENCES + AttemptEntry.TABLE_NAME + "(" + AttemptEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnAttemptEntry.COLUMN_PENALTY + INTEGER + NOT_NULL + BRACKET_SEMICOLON;
}