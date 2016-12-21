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
    //private static final String INSERT_INTO = "INSERT INTO ";

    /* Loaders Id */
    public static final int COMPETITIONS_LOADER_ID = 1;
    public static final int STAGE_ON_COMPETITION_LOADER_ID = 2;
    public static final int ADD_STAGE_LOADER_ID = 3;
    public static final int MEMBERS_LOADER_ID = 4;
    public static final int ATTEMPT_LOADER_ID = 5;
    public static final int TYPES_LOADER_ID = 6;

    public static final class GenderEntry implements BaseColumns {
        public static final String TABLE_NAME = "gender";
        public static final String COLUMN_GENDER = "gender";
    }
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 0;

    public static final class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_GENDER_ID = "gender_id";
        public static final String COLUMN_BIRTHDAY = "birthday";
    }

    // for different type of tourism as bike, hike, mountain etc.
    public static final class TypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "type";
        public static final String COLUMN_NAME = "type_name";
    }

    public static final int TYPE_BIKE_ID = 0;

    public static final class DistanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "distance";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_NAME = "distance_name";
    }

    public static final class CompetitionEntry implements BaseColumns {
        public static final String TABLE_NAME = "competition";
        public static final String COLUMN_DATE = "comp_date";
        public static final String COLUMN_NAME = "comp_name";
        public static final String COLUMN_PLACE = "comp_place";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_DISTANCE_ID = "distance_id";
        public static final String COLUMN_RANK = "comp_rank";
        public static final String COLUMN_PENALTY_COST = "comp_penalty_cost";
        public static final String COLUMN_IS_CLOSED = "comp_is_closed";
    }
    public static final int COMPETITION_OPENED = 0;
    public static final int COMPETITION_CLOSED = 1;

    public static final class StageEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage";
        public static final String COLUMN_DISTANCE_ID = "distance_id";
        public static final String COLUMN_NAME = "stage_name";
        public static final String COLUMN_DESCRIPTION = "stage_description";
        public static final String COLUMN_PENALTY_INFO = "stage_penalty_info";
        public static final String COLUMN_BITMAP = "stage_bitmap";
    }

    public static final class JudgeEntry implements BaseColumns {
        public static final String TABLE_NAME = "judge";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_JUDGE_RANK = "judge_rank";
        public static final String COLUMN_POSITION = "judge_position";
    }

    public static final class TeamEntry implements BaseColumns {
        public static final String TABLE_NAME = "team";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_NAME = "team_name";
        public static final String COLUMN_PLACE = "team_place";
    }

    public static final class MemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "member";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_TEAM_ID = "team_id";
        public static final String COLUMN_START_NUMBER = "member_start_number";
        public static final String COLUMN_SPORT_RANK = "member_sport_rank";
        public static final String COLUMN_TRAINER = "member_trainer";
        public static final String COLUMN_BIKE = "members_bike";
        public static final String COLUMN_RESULT_TIME = "member_result_time";
        public static final String COLUMN_PLACE = "member_place";
    }

    public static final class AttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "attempt";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_TRY_NUMBER = "attempt_try_number";
        public static final String COLUMN_PENALTY_TOTAL = "attempt_penalty_total";
        public static final String COLUMN_DISTANCE_TIME = "attempt_distance_time";
        public static final String COLUMN_RESULT_TIME = "attempt_result_time";
        public static final String COLUMN_IS_CLOSED = "attempt_is_closed";
    }

    public static final class StageOnCompetitionEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage_on_competition";
        public static final String COLUMN_COMPETITION_ID = "competition_id";
        public static final String COLUMN_STAGE_ID = "stage_id";
        public static final String COLUMN_POSITION = "soc_position";
    }

    public static final class StageOnAttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "stage_on_attempt";
        public static final String COLUMN_ATTEMPT_ID = "attempt_id";
        public static final String COLUMN_STAGE_ON_COMPETITION_ID = "stage_on_competition_id";
        public static final String COLUMN_PENALTY = "soa_penalty";
    }

    public static final String SQL_CREATE_GENDER_TABLE = CREATE_TABLE + GenderEntry.TABLE_NAME + SPACE_BRACKET +
            GenderEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            GenderEntry.COLUMN_GENDER + TEXT + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_TYPE_TABLE = CREATE_TABLE + TypeEntry.TABLE_NAME + SPACE_BRACKET +
            TypeEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            TypeEntry.COLUMN_NAME + TEXT + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_DISTANCE_TABLE = CREATE_TABLE + DistanceEntry.TABLE_NAME + SPACE_BRACKET +
            DistanceEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            DistanceEntry.COLUMN_TYPE_ID + INTEGER + NOT_NULL + REFERENCES + TypeEntry.TABLE_NAME + "(" + TypeEntry._ID + ")" + COMMA_SPACE + //FK OK
            DistanceEntry.COLUMN_NAME + TEXT + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_STAGE_TABLE = CREATE_TABLE + StageEntry.TABLE_NAME + SPACE_BRACKET +
            StageEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            StageEntry.COLUMN_DISTANCE_ID + INTEGER + NOT_NULL + REFERENCES + DistanceEntry.TABLE_NAME + "(" + DistanceEntry._ID + ")" + COMMA_SPACE +
            StageEntry.COLUMN_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            StageEntry.COLUMN_DESCRIPTION + TEXT + COMMA_SPACE +
            StageEntry.COLUMN_PENALTY_INFO + TEXT +
            StageEntry.COLUMN_BITMAP + TEXT + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_COMPETITION_TABLE = CREATE_TABLE + CompetitionEntry.TABLE_NAME + SPACE_BRACKET +
            CompetitionEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            CompetitionEntry.COLUMN_DATE + TEXT + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_PLACE + TEXT + COMMA_SPACE +
            CompetitionEntry.COLUMN_TYPE_ID + INTEGER + NOT_NULL + REFERENCES + TypeEntry.TABLE_NAME + "(" + TypeEntry._ID + ")" + COMMA_SPACE + //FK OK
            CompetitionEntry.COLUMN_DISTANCE_ID + INTEGER + NOT_NULL + REFERENCES + DistanceEntry.TABLE_NAME + "(" + DistanceEntry._ID + ")" + COMMA_SPACE + //FK OK
            CompetitionEntry.COLUMN_RANK + INTEGER + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_PENALTY_COST + INTEGER + NOT_NULL + COMMA_SPACE +
            CompetitionEntry.COLUMN_IS_CLOSED + INTEGER + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_PERSON_TABLE = CREATE_TABLE + PersonEntry.TABLE_NAME + SPACE_BRACKET +
            PersonEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            PersonEntry.COLUMN_LAST_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            PersonEntry.COLUMN_FIRST_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            PersonEntry.COLUMN_MIDDLE_NAME + TEXT + COMMA_SPACE +
            PersonEntry.COLUMN_GENDER_ID + INTEGER + REFERENCES + GenderEntry.TABLE_NAME + "(" + GenderEntry._ID + ")" + COMMA_SPACE + //FK OK
            PersonEntry.COLUMN_BIRTHDAY + TEXT + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_JUDGES_TABLE = CREATE_TABLE + JudgeEntry.TABLE_NAME + SPACE_BRACKET +
            JudgeEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            JudgeEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE +  //FK OK
            JudgeEntry.COLUMN_PERSON_ID + INTEGER + NOT_NULL + REFERENCES + PersonEntry.TABLE_NAME + "(" + PersonEntry._ID + ")" + COMMA_SPACE + //FK OK
            JudgeEntry.COLUMN_JUDGE_RANK + TEXT + COMMA_SPACE +
            JudgeEntry.COLUMN_POSITION + TEXT + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_TEAM_TABLE = CREATE_TABLE + TeamEntry.TABLE_NAME + SPACE_BRACKET +
            TeamEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            TeamEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE +  //FK OK
            TeamEntry.COLUMN_NAME + TEXT + NOT_NULL + COMMA_SPACE +
            TeamEntry.COLUMN_PLACE + INTEGER + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_MEMBERS_TABLE = CREATE_TABLE + MemberEntry.TABLE_NAME + SPACE_BRACKET +
            MemberEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            MemberEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            MemberEntry.COLUMN_PERSON_ID + INTEGER + NOT_NULL + REFERENCES + PersonEntry.TABLE_NAME + "(" + PersonEntry._ID + ")" + COMMA_SPACE + //FK OK
            MemberEntry.COLUMN_TEAM_ID + INTEGER + REFERENCES + TeamEntry.TABLE_NAME  + "(" + TeamEntry._ID + ")" + COMMA_SPACE + //FK OK
            MemberEntry.COLUMN_START_NUMBER + INTEGER + COMMA_SPACE +
            MemberEntry.COLUMN_SPORT_RANK + TEXT + COMMA_SPACE +
            MemberEntry.COLUMN_TRAINER + TEXT + COMMA_SPACE +
            MemberEntry.COLUMN_BIKE + TEXT + COMMA_SPACE +
            MemberEntry.COLUMN_RESULT_TIME + INTEGER + COMMA_SPACE +
            MemberEntry.COLUMN_PLACE + INTEGER + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_ATTEMPT_TABLE = CREATE_TABLE + AttemptEntry.TABLE_NAME + SPACE_BRACKET +
            AttemptEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            AttemptEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            AttemptEntry.COLUMN_MEMBER_ID + INTEGER + NOT_NULL + REFERENCES + MemberEntry.TABLE_NAME + "(" + MemberEntry._ID + ")" + COMMA_SPACE + //FK OK
            AttemptEntry.COLUMN_TRY_NUMBER + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_PENALTY_TOTAL + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_DISTANCE_TIME + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_RESULT_TIME + INTEGER + NOT_NULL + COMMA_SPACE +
            AttemptEntry.COLUMN_IS_CLOSED + INTEGER + NOT_NULL + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_STAGE_ON_COMPETITION_TABLE = CREATE_TABLE + StageOnCompetitionEntry.TABLE_NAME + SPACE_BRACKET +
            StageOnCompetitionEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
            StageOnCompetitionEntry.COLUMN_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + CompetitionEntry.TABLE_NAME + "(" + CompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnCompetitionEntry.COLUMN_STAGE_ID + INTEGER + NOT_NULL + REFERENCES + StageEntry.TABLE_NAME + "(" + StageEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnCompetitionEntry.COLUMN_POSITION + INTEGER + BRACKET_SEMICOLON;

    public static final String SQL_CREATE_STAGE_ON_ATTEMPT_TABLE = CREATE_TABLE + StageOnAttemptEntry.TABLE_NAME + SPACE_BRACKET +
            StageOnAttemptEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + NOT_NULL + COMMA_SPACE +
            StageOnAttemptEntry.COLUMN_STAGE_ON_COMPETITION_ID + INTEGER + NOT_NULL + REFERENCES + StageOnCompetitionEntry.TABLE_NAME + "(" + StageOnCompetitionEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnAttemptEntry.COLUMN_ATTEMPT_ID + INTEGER + NOT_NULL + REFERENCES + AttemptEntry.TABLE_NAME + "(" + AttemptEntry._ID + ")" + COMMA_SPACE + //FK OK
            StageOnAttemptEntry.COLUMN_PENALTY + INTEGER + NOT_NULL + BRACKET_SEMICOLON;
}