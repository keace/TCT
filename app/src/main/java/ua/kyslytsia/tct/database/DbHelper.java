package ua.kyslytsia.tct.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.util.Log;

import ua.kyslytsia.tct.R;

public class DbHelper extends SQLiteOpenHelper {
    private static final String LOG = "Log! DbHelper";

    private static final int DB_VERSION = 9;
    private static final String DB_NAME = "tct.db";
    private Context mContext = null;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG, "DB TEST PATH: " + db.getPath());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPreferencesEditor = mSharedPreferences.edit();

        createAndFillGenderTable(db);
        createAndFillTypeTable(db);
        createAndFillDistanceTable(db);
        createAndFillStageTable(db);

        db.execSQL(Contract.SQL_CREATE_COMPETITION_TABLE);
        db.execSQL(Contract.SQL_CREATE_TEAM_TABLE);
        db.execSQL(Contract.SQL_CREATE_PERSON_TABLE);
        db.execSQL(Contract.SQL_CREATE_JUDGES_TABLE);
        db.execSQL(Contract.SQL_CREATE_MEMBERS_TABLE);
        db.execSQL(Contract.SQL_CREATE_ATTEMPT_TABLE);
        db.execSQL(Contract.SQL_CREATE_STAGE_ON_COMPETITION_TABLE);
        db.execSQL(Contract.SQL_CREATE_STAGE_ON_ATTEMPT_TABLE);
    }

    private void createAndFillGenderTable(SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE_GENDER_TABLE);
        Log.i(LOG, Contract.SQL_CREATE_GENDER_TABLE);

        try {
            String sql = "INSERT INTO " + Contract.GenderEntry.TABLE_NAME + " (" +
                    Contract.GenderEntry._ID + ", " +
                    Contract.GenderEntry.COLUMN_GENDER + ") " +
                    " VALUES (?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            String[] genders = mContext.getResources().getStringArray(R.array.import_to_db_genders);

            db.beginTransaction();
            for (int i = 0; i < genders.length; i++) {
                statement.bindString(1, String.valueOf(i));
                statement.bindString(2, genders[i]);
                statement.execute();
                statement.clearBindings();
                Log.i(LOG, "Insert Gender:" + i + ", " + genders[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            Log.i(LOG, "Gender inserted");
        }
    }

    private void createAndFillTypeTable(SQLiteDatabase db) {
        Log.i(LOG, Contract.SQL_CREATE_TYPE_TABLE);
        db.execSQL(Contract.SQL_CREATE_TYPE_TABLE);

        try {
            String sql = "INSERT INTO " + Contract.TypeEntry.TABLE_NAME + " (" +
                    Contract.TypeEntry._ID + ", " +
                    Contract.TypeEntry.COLUMN_NAME + ") " +
                    " VALUES (?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            String[] typesOfTourism = mContext.getResources().getStringArray(R.array.import_to_db_types_of_tourism);

            db.beginTransaction();
            for (int i = 0; i < typesOfTourism.length; i++) {
                statement.bindString(1, String.valueOf(i));
                statement.bindString(2, typesOfTourism[i]);
                statement.execute();
                statement.clearBindings();
                mSharedPreferencesEditor.putLong(typesOfTourism[i], i); //save ids to SharedPreferences
                Log.i(LOG, "Insert Type:" + i + ", " + typesOfTourism[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mSharedPreferencesEditor.apply();
            Log.i(LOG, "Type inserted");
        }
    }

    private void createAndFillDistanceTable(SQLiteDatabase db) {
        Log.i(LOG, Contract.SQL_CREATE_DISTANCE_TABLE);
        db.execSQL(Contract.SQL_CREATE_DISTANCE_TABLE);

        try {
            String sql = "INSERT INTO " + Contract.DistanceEntry.TABLE_NAME + " (" +
                    Contract.DistanceEntry._ID + ", " +
                    Contract.DistanceEntry.COLUMN_TYPE_ID + ", " +
                    Contract.DistanceEntry.COLUMN_NAME + ") " +
                    " VALUES (?, ?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            String[] distancesForBikeTourism = mContext.getResources().getStringArray(R.array.import_to_db_distances_for_bike);

            String typeName = mContext.getString(R.string.type_bike);
            long typeId = mSharedPreferences.getLong(typeName, 0);
            Log.i(LOG, "distanceId: " + typeId);

            db.beginTransaction();
            for (int i = 0; i < distancesForBikeTourism.length; i++) {
                stmt.bindString(1, String.valueOf(i));
                stmt.bindString(2, String.valueOf(typeId));
                stmt.bindString(3, distancesForBikeTourism[i]);
                stmt.execute();
                stmt.clearBindings();
                mSharedPreferencesEditor.putLong(distancesForBikeTourism[i], i); //save ids to SharedPreferences
                Log.i(LOG, "Insert Distance: " + i + ", " + distancesForBikeTourism[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mSharedPreferencesEditor.apply();
            Log.i(LOG, "Inserting distances completed");
        }
    }

    private void createAndFillStageTable (SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE_STAGE_TABLE);
        Log.i(LOG, Contract.SQL_CREATE_STAGE_TABLE);

        fillStagesToRally(db);
        fillStagesToFigureRide(db);
        fillStagesToCross(db);
        fillStagesToTrial(db);
    }

    private void fillStagesToRally (SQLiteDatabase db){
        String[] rallyStages = mContext.getResources().getStringArray(R.array.import_to_db_rally_stages);
        String distanceName = mContext.getString(R.string.bike_distance_rally);

        fillStagesToDistance(db, rallyStages, distanceName);
    }

    private void fillStagesToFigureRide (SQLiteDatabase db) {
        String[] figureStages = mContext.getResources().getStringArray(R.array.import_to_db_figure_ride_stages);
        String distanceName = mContext.getString(R.string.bike_distance_figure_ride);

        fillStagesToDistance(db, figureStages, distanceName);
    }

    private void fillStagesToCross (SQLiteDatabase db){
        String[] crossStages = mContext.getResources().getStringArray(R.array.import_to_db_cross_stages);
        String distanceName = mContext.getString(R.string.bike_distance_cross);

        fillStagesToDistance(db, crossStages, distanceName);
    }

    private void fillStagesToTrial (SQLiteDatabase db){
        String[] trialStages = mContext.getResources().getStringArray(R.array.import_to_db_trial_stages);
        String distanceName = mContext.getString(R.string.bike_distance_trial);

        fillStagesToDistance(db, trialStages, distanceName);
    }
    private void fillStagesToDistance(SQLiteDatabase db, String[] stages, String distanceName) {
        try {
            String sql = "INSERT INTO " + Contract.StageEntry.TABLE_NAME + " (" + Contract.StageEntry.COLUMN_DISTANCE_ID + ", " + Contract.StageEntry.COLUMN_NAME + ") " + " VALUES (?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            long distanceId = mSharedPreferences.getLong(distanceName, 0);
            Log.i(LOG, "distance: Id= " + distanceId + ", name = " + distanceName);

            db.beginTransaction();
            for (int i = 0; i < stages.length; i++) {
                stmt.bindString(1, String.valueOf(distanceId));
                stmt.bindString(2, stages[i]);
                stmt.execute();
                stmt.clearBindings();
                Log.i(LOG, "Insert Stage: " + i + ", " + stages[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            Log.i(LOG, "Inserting stages completed");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Upgrade not work as upgrade */
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