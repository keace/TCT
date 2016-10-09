package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ua.kyslytsia.tct.MainActivity;

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG = "LOG! DbHelper";

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
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.TypeEntry.TABLE_NAME + " WHERE " + Contract.TypeEntry._ID + "=" + id + ";", null);

        // Only for Training:
        Cursor c = sqLiteDatabase.query(Contract.TypeEntry.TABLE_NAME, null, Contract.TypeEntry._ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex(Contract.TypeEntry.COLUMN_NAME));
    }

    public String findDistanceById(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.DistanceEntry.TABLE_NAME + " WHERE " + Contract.DistanceEntry._ID + "=" + id + ";", null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex(Contract.DistanceEntry.COLUMN_DISTANCE_NAME));
    }
}
