package ua.kyslytsia.tct.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import ua.kyslytsia.tct.R;

public class DbHelper extends SQLiteOpenHelper {
    private static final String LOG = "Log! DbHelper";

    private static final int DB_VERSION = 10;
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
        String[] rallyStagesNames = mContext.getResources().getStringArray(R.array.import_to_db_rally_stages);
        String[] rallyStagesDescriptions = mContext.getResources().getStringArray(R.array.import_to_db_rally_stages_description);
        String[] rallyStagesPenaltyInfo = mContext.getResources().getStringArray(R.array.import_to_db_rally_stages_penalty_info);
        String distanceName = mContext.getString(R.string.bike_distance_rally);

        fillStagesToDistance(db, distanceName, rallyStagesNames, rallyStagesDescriptions, rallyStagesPenaltyInfo);
    }

    private void fillStagesToFigureRide (SQLiteDatabase db) {
        String[] figureStagesNames = mContext.getResources().getStringArray(R.array.import_to_db_figure_ride_stages);
        String[] figureStagesDescriptions = mContext.getResources().getStringArray(R.array.import_to_db_figure_ride_stages_description);
        String[] figureStagesPenaltyInfo = new String[figureStagesNames.length+1];
        for (int i = 0; i < figureStagesPenaltyInfo.length; i++){
            figureStagesPenaltyInfo[i] = mContext.getResources().getString(R.string.import_to_db_bike_figure_ride_penalties);
        }
        String distanceName = mContext.getString(R.string.bike_distance_figure_ride);

        fillStagesToDistance(db, distanceName, figureStagesNames, figureStagesDescriptions, figureStagesPenaltyInfo);
    }

    private void fillStagesToCross (SQLiteDatabase db){
        String[] crossStagesNames = mContext.getResources().getStringArray(R.array.import_to_db_bike_cross_stages);
        String[] crossStagesDescriptions = new String[]{mContext.getResources().getString(R.string.import_to_db_bike_cross_and_trial_description)};
        String[] crossStagesPenaltyInfo = new String[]{mContext.getResources().getString(R.string.import_to_db_bike_cross_and_trial_penalty_info)};
        String distanceName = mContext.getString(R.string.bike_distance_cross);

        fillStagesToDistance(db, distanceName, crossStagesNames, crossStagesDescriptions, crossStagesPenaltyInfo);
    }

    private void fillStagesToTrial (SQLiteDatabase db){
        String[] trialStagesNames = mContext.getResources().getStringArray(R.array.import_to_db_bike_trial_stages);
        String[] trialStagesDescriptions = new String[]{mContext.getResources().getString(R.string.import_to_db_bike_cross_and_trial_description)};
        String[] trialStagesPenaltyInfo = new String[]{mContext.getResources().getString(R.string.import_to_db_bike_cross_and_trial_penalty_info)};
        String distanceName = mContext.getString(R.string.bike_distance_trial);

        fillStagesToDistance(db, distanceName, trialStagesNames, trialStagesDescriptions, trialStagesPenaltyInfo);
    }
    private void fillStagesToDistance(SQLiteDatabase db, String distanceName, String[] stagesNames, String[] stagesDescriptions, String[] stagesPenaltyInfo) {
        try {
            String sql = "INSERT INTO " + Contract.StageEntry.TABLE_NAME + " (" +
                    Contract.StageEntry.COLUMN_DISTANCE_ID + ", " +
                    Contract.StageEntry.COLUMN_NAME + "," +
                    Contract.StageEntry.COLUMN_DESCRIPTION + "," +
                    Contract.StageEntry.COLUMN_PENALTY_INFO + ") " +
                    " VALUES (?, ?, ?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            long distanceId = mSharedPreferences.getLong(distanceName, 0);
            Log.i(LOG, "distance: Id= " + distanceId + ", name = " + distanceName);

            db.beginTransaction();
            for (int i = 0; i < stagesNames.length; i++) {
                stmt.bindString(1, String.valueOf(distanceId));
                stmt.bindString(2, stagesNames[i]);
                stmt.bindString(3, stagesDescriptions[i]);
                stmt.bindString(4, stagesPenaltyInfo[i]);
                stmt.execute();
                stmt.clearBindings();
                Log.i(LOG, "Insert Stage: " + i + ", " + stagesNames[i]);
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

    @NonNull
    public static String getDescriptionAndPenaltyInfo(Cursor c) {
        String description = c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_DESCRIPTION));
        String penalty = c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_PENALTY_INFO));
        StringBuilder sb = new StringBuilder();
        sb.append(description).append("\n\n").append(penalty);
        return sb.toString();
    }

    public long findTeamIdByName (long competitionId, String teamName) {
        long teamId = 0;

        String where = Contract.TeamEntry.COLUMN_COMPETITION_ID + " =? AND " +
                Contract.TeamEntry.COLUMN_NAME + " =?";
        String[] whereArgs = new String[]{String.valueOf(competitionId), teamName};
        Cursor cursor = mContext.getContentResolver().query(ContentProvider.TEAM_CONTENT_URI, null, where, whereArgs, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            teamId = cursor.getLong(cursor.getColumnIndex(Contract.TeamEntry._ID));
            cursor.close();
        }
        return teamId;
    }
}