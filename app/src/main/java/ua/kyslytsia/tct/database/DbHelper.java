package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.util.Log;

import ua.kyslytsia.tct.R;

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG = "LOG DB-HELPER";

    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "tct.db";
    public  Context context = null;

    private static ContentValues cv = new ContentValues();

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {

        Log.i(LOG, "DB TEST PATH: " + db.getPath());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        Log.i(LOG, Contract.SQL_CREATE_GENDER_TABLE);
        db.execSQL(Contract.SQL_CREATE_GENDER_TABLE);

        /* Create and fill GENDER table */
        try {
            String sql = "INSERT INTO " + Contract.GenderEntry.TABLE_NAME + " (" + Contract.GenderEntry.COLUMN_GENDER + ") " + " VALUES (?)";
            SQLiteStatement statement = db.compileStatement(sql);
            String[] genders = context.getResources().getStringArray(R.array.import_to_db_genders);

            db.beginTransaction();
            for (int i = 0; i < genders.length; i++) {
                statement.bindString(1, genders[i]);
                Log.i(LOG, "Insert Gender:" + i + ", " + genders[i]);
                statement.execute();
                statement.clearBindings();
            }
            db.setTransactionSuccessful();
        } finally {
            Log.i(LOG, "Gender inserted");
            db.endTransaction();
        }

//        cv.put(Contract.GenderEntry._ID, 0);
//        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Жен");
//        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
//        cv.put(Contract.GenderEntry._ID, 1);
//        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Муж");
//        db.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
//        cv.clear();

        /* CREATE AND FILL TYPE TABLE */
        Log.i(LOG, Contract.SQL_CREATE_TYPE_TABLE);
        db.execSQL(Contract.SQL_CREATE_TYPE_TABLE);

        try {
            String sql = "INSERT INTO " + Contract.TypeEntry.TABLE_NAME + " (" + Contract.TypeEntry.COLUMN_NAME + ") " + " VALUES (?)";
            SQLiteStatement statement = db.compileStatement(sql);
            String[] typesOfTourism = context.getResources().getStringArray(R.array.import_to_db_types_of_tourism);

            db.beginTransaction();
            for (int i = 0; i < typesOfTourism.length; i++) {
                statement.bindString(1, typesOfTourism[i]);
                statement.execute();
                statement.clearBindings();
                Log.i(LOG, "Insert Type:" + i + ", " + typesOfTourism[i]);
                sharedPreferencesEditor.putInt(typesOfTourism[i], i); //save ids to SharedPreferences
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            sharedPreferencesEditor.apply();
            Log.i(LOG, "Type inserted");
        }


        Log.i(LOG, Contract.SQL_CREATE_DISTANCE_TABLE);
        db.execSQL(Contract.SQL_CREATE_DISTANCE_TABLE);

        try {
            String sql = "INSERT INTO " + Contract.DistanceEntry.TABLE_NAME + " (" + Contract.DistanceEntry.COLUMN_TYPE_ID + ", " + Contract.DistanceEntry.COLUMN_NAME + ") " + " VALUES (?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            String[] distancesForBikeTourism = context.getResources().getStringArray(R.array.import_to_db_distances_for_bike);

            String typeName = context.getString(R.string.type_bike);
            int distanceId = sharedPreferences.getInt(typeName, 0);
            Log.i(LOG, "distanceId: " + distanceId);

            db.beginTransaction();
            for (int i = 0; i < distancesForBikeTourism.length; i++) {
                stmt.bindString(1, String.valueOf(distanceId));
                stmt.bindString(2, distancesForBikeTourism[i]);
                stmt.execute();
                stmt.clearBindings();
                sharedPreferencesEditor.putInt(distancesForBikeTourism[i], i); //save ids to SharedPreferences
                Log.i(LOG, "Insert Distance:" + i + ", " + distancesForBikeTourism[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            Log.i(LOG, "Insert distance complete");
        }

//        cv.put(Contract.DistanceEntry.COLUMN_TYPE_ID, 1);
//        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_RALLY_ID);
//        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Велоралли"); //distance id: 1
//        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
//        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_FIGURE_ID);
//        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Фигурное вождение"); //distance id: 2
//        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
//        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_CROSS_ID);
//        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Велокросс"); //distance id: 3
//        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
//        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_TRIAL_ID);
//        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Триал"); //distance id: 4
//        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
//        cv.put(Contract.DistanceEntry._ID, Contract.DISTANCE_COMPLEX_ID);
//        cv.put(Contract.DistanceEntry.COLUMN_NAME, "Комплексная дистанция"); //distance id: 5
//        db.insert(Contract.DistanceEntry.TABLE_NAME, null, cv);
//        cv.clear();

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
}