package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import ua.kyslytsia.tct.AddStageActivity;
import ua.kyslytsia.tct.MainActivity;
import ua.kyslytsia.tct.mocks.Competition;

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG = "LOG DB-HELPER";

    public static final int DB_VERSION = 4;
    public static final String DB_NAME = "tct.db";

    private static ContentValues cv = new ContentValues();

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG, "DB TEST PATH: " + db.getPath());

        Log.d(LOG, Contract.SQL_CREATE_GENDER_TABLE);
        db.execSQL(Contract.SQL_CREATE_GENDER_TABLE);

        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Муж");
        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Жен");
        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
        cv.clear();

        Log.d(LOG, Contract.SQL_CREATE_TYPE_TABLE);
        db.execSQL(Contract.SQL_CREATE_TYPE_TABLE);
        cv.put(Contract.TypeEntry.COLUMN_NAME, "Велосипедный");
        db.insert(Contract.TypeEntry.TABLE_NAME, null, cv);
        cv.put(Contract.TypeEntry.COLUMN_NAME, "Горный");
        db.insert(Contract.TypeEntry.TABLE_NAME, null, cv);
        cv.clear();

        Log.d(LOG, Contract.SQL_CREATE_DISTANCE_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_STAGE_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_COMPETITION_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_PERSON_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_JUDGES_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_MEMBERS_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_ATTEMPT_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_STAGE_ON_COMPETITION_TABLE);
        Log.d(LOG, Contract.SQL_CREATE_STAGE_ON_ATTEMPT_TABLE);

        db.execSQL(Contract.SQL_CREATE_DISTANCE_TABLE);
        cv.put(Contract.DistanceEntry.COLUMN_DISTANCE_NAME, "Фигурка");
        cv.put(Contract.DistanceEntry.COLUMN_TYPE_ID, 1);
        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
        cv.clear();

        db.execSQL(Contract.SQL_CREATE_STAGE_TABLE);
        cv.put(Contract.StageEntry.COLUMN_DISTANCE_ID, 1);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Восьмерка");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Качеля");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Колея");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.put(Contract.StageEntry.COLUMN_NAME, "Стоп-линия");
        db.insert(Contract.StageEntry.TABLE_NAME, null, cv);
        cv.clear();

        db.execSQL(Contract.SQL_CREATE_COMPETITION_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + Contract.StageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.DistanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TypeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.GenderEntry.TABLE_NAME);
        Log.d(LOG, "ALL TABLE DROPPED");

        onCreate(db);
    }

    public String findTypeNameById (int id) {
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
        String result = c.getString(c.getColumnIndex(Contract.DistanceEntry.COLUMN_DISTANCE_NAME));
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
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.CompetitionEntry.TABLE_NAME + " WHERE " + Contract.CompetitionEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        Competition competition = new Competition(
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE)),
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME)),
                c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_TYPE_ID)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_DISTANCE_ID)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_TIME)),
                c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_IS_CLOSED)));
        Log.d(LOG, competition.toString());
        c.close();
        sqLiteDatabase.close();
        return competition;
    }

    public void addStageOnCompetition (int compId, int stageId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME + " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=" + compId + " AND " + Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID + "=" + stageId + ";", null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            Log.d(LOG, "NOT INSERTED because such record already exists, c.getCount = " + c.getCount());
            //Log.d(LOG, "Содержимое первой строки первого столбца: " + c.getInt(0));
            c.close();
            return;
        } else {
            if (cv.size() != 0){
                cv.clear(); }
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, compId);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID, stageId);
            sqLiteDatabase.insert(Contract.StageOnCompetitionEntry.TABLE_NAME, null, cv);
            Log.d(LOG, "INSERTED CompId = " + compId + ", StageId = " + stageId);
        }
        cv.clear();
    }
}
