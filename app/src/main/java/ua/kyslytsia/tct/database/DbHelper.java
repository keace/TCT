package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ua.kyslytsia.tct.mocks.Competition;
import ua.kyslytsia.tct.mocks.Member;
import ua.kyslytsia.tct.mocks.Person;

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG = "LOG DB-HELPER";

    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "tct.db";

    private static ContentValues cv = new ContentValues();

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        Log.i(LOG, "DB TEST PATH: " + db.getPath());

        Log.i(LOG, Contract.SQL_CREATE_GENDER_TABLE);
        db.execSQL(Contract.SQL_CREATE_GENDER_TABLE);

        cv.put(Contract.GenderEntry._ID, 0);
        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Жен");
        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
        cv.put(Contract.GenderEntry._ID, 1);
        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Муж");
        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
        cv.clear();

        Log.i(LOG, Contract.SQL_CREATE_TYPE_TABLE);
        db.execSQL(Contract.SQL_CREATE_TYPE_TABLE);
        //Type id: 1
        cv.put(Contract.TypeEntry._ID, 1);
        cv.put(Contract.TypeEntry.COLUMN_NAME, "Велосипедный");
        db.insert(Contract.TypeEntry.TABLE_NAME, null, cv);
        //Type id: 2
        cv.clear();

        Log.i(LOG, Contract.SQL_CREATE_DISTANCE_TABLE);
        db.execSQL(Contract.SQL_CREATE_DISTANCE_TABLE);

        cv.put(Contract.DistanceEntry.COLUMN_TYPE_ID, 1);
        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_RALLY_ID);
        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Велоралли"); //distance id: 1
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_FIGURE_ID);
        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Фигурное вождение"); //distance id: 2
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_CROSS_ID);
        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Велокросс"); //distance id: 3
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_TRIAL_ID);
        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Триал"); //distance id: 4
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_COMPLEX_ID);
        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Комплексная дистанция"); //distance id: 5
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.clear();

        /* Create Stages for distances according to their id */
        Log.i(LOG, Contract.SQL_CREATE_STAGE_TABLE);
        db.execSQL(Contract.SQL_CREATE_STAGE_TABLE);
        /* Велоралли, id: 1 */
        cv.put(Contract.StageEntry.COLUMN_DISTANCE_ID, Contract.DISTANCE_RALLY_ID);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Правила дорожного движения");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Оказание первой доврачебной помощи");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Крепление и перевозка груза");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Установка палатки");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Распаливание костра");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Определение азимута");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Ремонт велосипеда");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Движение по бездорожью");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Движение по графику");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Скоростной участок");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Подъем в гору");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Спуск по склону");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Песчаный этап");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Колея");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Движение по азимуту");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Ориентирование");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Преодоление брода");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Вязание узлов");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Переправа через реку или яр по колоде");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.clear();

        /* Фигурное вождение, distance id: 2 */
        cv.put(Contract.StageEntry.COLUMN_DISTANCE_ID, Contract.DISTANCE_FIGURE_ID);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Щель");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Змейка между стоек");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Корридор");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Колея");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Круг");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Восьмерка");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Ворота");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Кольцо");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Перенос предмета");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Зигзаг");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Качеля");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Змейка между фишек");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Стоп-линия");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.clear();

        /* Велокросс, distance id: 3 */
        cv.put(Contract.StageEntry.COLUMN_DISTANCE_ID, Contract.DISTANCE_CROSS_ID);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Велокросс");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.clear();

        /* Триал, distance id: 4 */
        cv.put(Contract.StageEntry.COLUMN_DISTANCE_ID, Contract.DISTANCE_TRIAL_ID);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Триал");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.clear();

        db.execSQL(Contract.SQL_CREATE_COMPETITION_TABLE);
        db.execSQL(Contract.SQL_CREATE_TEAM_TABLE);
        db.execSQL(Contract.SQL_CREATE_PERSON_TABLE);
        db.execSQL(Contract.SQL_CREATE_JUDGES_TABLE);
        db.execSQL(Contract.SQL_CREATE_MEMBERS_TABLE);
        db.execSQL(Contract.SQL_CREATE_ATTEMPT_TABLE);
        db.execSQL(Contract.SQL_CREATE_STAGE_ON_COMPETITION_TABLE);
        db.execSQL(Contract.SQL_CREATE_STAGE_ON_ATTEMPT_TABLE);

//        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.GenderEntry.TABLE_NAME, null);

//        Log.d(LOG, c.getString(0));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.StageOnAttemptEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.StageOnCompetitionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.AttemptEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.MemberEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.JudgeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.PersonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.CompetitionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TeamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.StageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.DistanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TypeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.GenderEntry.TABLE_NAME);
        Log.d(LOG, "ALL TABLE DROPPED");

        onCreate(db);
    }




    /*public String findTypeNameById (int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.TypeEntry.TABLE_NAME + " WHERE " + Contract.TypeEntry._ID + "=" + id + ";", null);

        // Only for Training:
        Cursor c = sqLiteDatabase.query(Contract.TypeEntry.TABLE_NAME, null, Contract.TypeEntry._ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(Contract.TypeEntry.COLUMN_NAME));
        c.close();
        sqLiteDatabase.close();
        return result;
    }

    public String findDistanceById(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.DistanceEntry.TABLE_NAME + " WHERE " + Contract.DistanceEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(Contract.DistanceEntry.COLUMN_NAME));
        c.close();
        sqLiteDatabase.close();
        return result;
    }

    public String findStageNameById(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageEntry.TABLE_NAME + " WHERE " + Contract.StageEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_NAME));
        c.close();
        sqLiteDatabase.close();
        return result;
    }

    public Competition findCompetitionById (int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.CompetitionEntry.TABLE_NAME + " WHERE " + Contract.CompetitionEntry._ID + "=?;", new String[] {String.valueOf(id)});
        c.moveToFirst();
        Competition competition = new Competition(
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE)),
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME)),
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_TYPE_ID)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_DISTANCE_ID)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_COST)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_IS_CLOSED)));
        Log.d(LOG, competition.toString());
        c.close();
        sqLiteDatabase.close();
        return competition;
    }

    public void addStageOnCompetition (long compId, long stageId, int lastPosition) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME + " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=" + compId + " AND " + Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID + "=" + stageId + ";", null);
        int position = lastPosition + 1;
        if (c.getCount() > 0) {
            c.moveToFirst();
            Log.d(LOG, "NOT INSERTED because such record already exists, c.getCount = " + c.getCount());
            c.close();
            return;
        } else {
            if (cv.size() != 0){
                cv.clear(); }
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, compId);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID, stageId);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, position);
            sqLiteDatabase.insert(Contract.StageOnCompetitionEntry.TABLE_NAME, null, cv);
            Log.d(LOG, "INSERTED CompId = " + compId + ", StageId = " + stageId + ", on position = " + position);
            sqLiteDatabase.close();
        }
        cv.clear();
    }

    public void updateStageOnCompetitionPosition (long id, int newPosition) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
        sqLiteDatabase.update(Contract.StageOnCompetitionEntry.TABLE_NAME, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(id)});
        sqLiteDatabase.close();
        cv.clear();
    }

    public void dropStageOnCompetition (long id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(Contract.StageOnCompetitionEntry.TABLE_NAME, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(id)});
        sqLiteDatabase.close();
    }

    public Member findMemberById (int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.MemberEntry.TABLE_NAME + " WHERE " + Contract.MemberEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        Member member = new Member(
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_COMPETITION_ID)),
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_PERSON_ID)),
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_TEAM_ID)),
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)),
                c.getString(c.getColumnIndex(Contract.MemberEntry.COLUMN_SPORT_RANK)),
                c.getString(c.getColumnIndex(Contract.MemberEntry.COLUMN_BIKE)),
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_RESULT_TIME)),
                c.getInt(c.getColumnIndex(Contract.MemberEntry.COLUMN_PLACE)));
        Log.d(LOG, member.toString());
        c.close();
        sqLiteDatabase.close();
        return member;
    }

    public Person findPersonById (int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.PersonEntry.TABLE_NAME + " WHERE " + Contract.PersonEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        Person person = new Person(
                c.getString(c.getColumnIndex(Contract.PersonEntry.COLUMN_LAST_NAME)),
                c.getString(c.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME)),
                c.getString(c.getColumnIndex(Contract.PersonEntry.COLUMN_MIDDLE_NAME)),
                c.getInt(c.getColumnIndex(Contract.PersonEntry.COLUMN_GENDER_ID)),
                c.getString(c.getColumnIndex(Contract.PersonEntry.COLUMN_BIRTHDAY)));
        Log.d(LOG, person.toString());
        c.close();
        sqLiteDatabase.close();
        return person;
    }

    public String findTeamNameById (int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.TeamEntry.TABLE_NAME + " WHERE " + Contract.TeamEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(Contract.TeamEntry.COLUMN_NAME));
        c.close();
        sqLiteDatabase.close();
        return result;
    }*/
}