package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class ContentProvider extends android.content.ContentProvider {

    private final String LOG = "Log! Content Provider";
    private DbHelper dbHelper;

    private static final String SLASH_LATTICE = "/#";

    private static final String AUTHORITY = "ua.kyslytsia.tct.database";

    public static final Uri GENDER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.GenderEntry.TABLE_NAME);
    public static final Uri PERSON_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.PersonEntry.TABLE_NAME);
    public static final Uri TYPE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.TypeEntry.TABLE_NAME);
    public static final Uri DISTANCE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.DistanceEntry.TABLE_NAME);
    public static final Uri COMPETITION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.CompetitionEntry.TABLE_NAME);
    public static final Uri STAGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.StageEntry.TABLE_NAME);
    public static final Uri JUDGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.JudgeEntry.TABLE_NAME);
    public static final Uri TEAM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.TeamEntry.TABLE_NAME);
    public static final Uri MEMBER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.MemberEntry.TABLE_NAME);
    public static final Uri ATTEMPT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.AttemptEntry.TABLE_NAME);
    public static final Uri STAGE_ON_COMPETITION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.StageOnCompetitionEntry.TABLE_NAME);
    public static final Uri STAGE_ON_ATTEMPT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.StageOnAttemptEntry.TABLE_NAME);

    // used for UriMatcher
    private static final int GENDERS = 1000;
    private static final int GENDER_ID = 1001;

    private static final int PERSONS = 2000;
    private static final int PERSON_ID = 2001;

    private static final int TYPES = 3000;
    private static final int TYPE_ID = 3001;

    private static final int DISTANCES = 4000;
    private static final int DISTANCE_ID = 4001;

    private static final int COMPETITIONS = 5000;
    private static final int COMPETITION_ID = 5001;

    private static final int STAGES = 6000;
    private static final int STAGE_ID = 6001;

    private static final int JUDGES = 7000;
    private static final int JUDGE_ID = 7001;

    private static final int TEAMS = 8000;
    private static final int TEAM_ID = 8001;

    private static final int MEMBERS = 9000;
    private static final int MEMBER_ID = 9001;

    private static final int ATTEMPTS = 1100;
    private static final int ATTEMPT_ID = 1101;

    private static final int STAGES_ON_COMPETITIONS = 1200;
    private static final int STAGE_ON_COMPETITION_ID = 1201;

    private static final int STAGES_ON_ATTEMPTS = 1300;
    private static final int STAGE_ON_ATTEMPT_ID = 1301;

    private static final UriMatcher sURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURI_MATCHER.addURI(AUTHORITY, Contract.GenderEntry.TABLE_NAME, GENDERS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.GenderEntry.TABLE_NAME + SLASH_LATTICE, GENDER_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.PersonEntry.TABLE_NAME, PERSONS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.PersonEntry.TABLE_NAME + SLASH_LATTICE, PERSON_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.TypeEntry.TABLE_NAME, TYPES);
        sURI_MATCHER.addURI(AUTHORITY, Contract.TypeEntry.TABLE_NAME + SLASH_LATTICE, TYPE_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.DistanceEntry.TABLE_NAME, DISTANCES);
        sURI_MATCHER.addURI(AUTHORITY, Contract.DistanceEntry.TABLE_NAME + SLASH_LATTICE, DISTANCE_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.CompetitionEntry.TABLE_NAME, COMPETITIONS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.CompetitionEntry.TABLE_NAME + SLASH_LATTICE, COMPETITION_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.StageEntry.TABLE_NAME, STAGES);
        sURI_MATCHER.addURI(AUTHORITY, Contract.StageEntry.TABLE_NAME + SLASH_LATTICE, STAGE_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.JudgeEntry.TABLE_NAME, JUDGES);
        sURI_MATCHER.addURI(AUTHORITY, Contract.JudgeEntry.TABLE_NAME + SLASH_LATTICE, JUDGE_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.TeamEntry.TABLE_NAME, TEAMS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.TeamEntry.TABLE_NAME + SLASH_LATTICE, TEAM_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.MemberEntry.TABLE_NAME, MEMBERS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.MemberEntry.TABLE_NAME + SLASH_LATTICE, MEMBER_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.AttemptEntry.TABLE_NAME, ATTEMPTS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.AttemptEntry.TABLE_NAME + SLASH_LATTICE, ATTEMPT_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.StageOnCompetitionEntry.TABLE_NAME, STAGES_ON_COMPETITIONS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.StageOnCompetitionEntry.TABLE_NAME + SLASH_LATTICE, STAGE_ON_COMPETITION_ID);

        sURI_MATCHER.addURI(AUTHORITY, Contract.StageOnAttemptEntry.TABLE_NAME, STAGES_ON_ATTEMPTS);
        sURI_MATCHER.addURI(AUTHORITY, Contract.StageOnAttemptEntry.TABLE_NAME + SLASH_LATTICE, STAGE_ON_ATTEMPT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        //checkColumns(projection);
        int uriType = sURI_MATCHER.match(uri);
        switch (uriType) {
            case PERSONS:
                Log.i(LOG, "Case: PERSONS");
                queryBuilder.setTables(Contract.PersonEntry.TABLE_NAME);
                break;

            case DISTANCES:
                Log.i(LOG, "Case: DISTANCES");
                queryBuilder.setTables(Contract.DistanceEntry.TABLE_NAME);
                break;

            case PERSON_ID:
                Log.i(LOG, "Case: PERSON_ID");
                queryBuilder.setTables(Contract.PersonEntry.TABLE_NAME);
                queryBuilder.appendWhere(Contract.PersonEntry._ID + "=" + uri.getLastPathSegment());
                break;

            case COMPETITIONS:
                Log.i(LOG, "Query COMPETITIONS");
                projection = new String[] {
                        Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry._ID,
                        Contract.CompetitionEntry.COLUMN_DATE,
                        Contract.CompetitionEntry.COLUMN_NAME,
                        Contract.CompetitionEntry.COLUMN_PLACE,
                        Contract.TypeEntry.COLUMN_NAME,
                        Contract.DistanceEntry.COLUMN_NAME,
                        Contract.CompetitionEntry.COLUMN_RANK,
                        Contract.CompetitionEntry.COLUMN_PENALTY_COST,
                        Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry.COLUMN_TYPE_ID,
                        Contract.CompetitionEntry.COLUMN_DISTANCE_ID,
                        Contract.CompetitionEntry.COLUMN_IS_CLOSED,
                };
                queryBuilder.setTables(Contract.CompetitionEntry.TABLE_NAME +
                        " INNER JOIN " + Contract.TypeEntry.TABLE_NAME +
                        " ON " + Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry.COLUMN_TYPE_ID + " = " + Contract.TypeEntry.TABLE_NAME + "." + Contract.TypeEntry._ID +
                        " INNER JOIN " + Contract.DistanceEntry.TABLE_NAME +
                        " ON " + Contract.CompetitionEntry.COLUMN_DISTANCE_ID + " = " + Contract.DistanceEntry.TABLE_NAME + "." + Contract.DistanceEntry._ID);
                break;

            case COMPETITION_ID:
                Log.i(LOG, "Query COMPETITION_ID");
                queryBuilder.setTables(Contract.CompetitionEntry.TABLE_NAME);
                queryBuilder.appendWhere(Contract.CompetitionEntry._ID + "=" + uri.getLastPathSegment());
                break;

            case STAGES_ON_COMPETITIONS:
                Log.i(LOG, "Case: STAGE_ON_COMPETITION");
                projection = new String[] {
                        Contract.StageOnCompetitionEntry.TABLE_NAME + "." + Contract.StageOnCompetitionEntry._ID,
                        Contract.StageOnCompetitionEntry.COLUMN_POSITION,
                        Contract.StageEntry.COLUMN_NAME,
                        Contract.StageEntry.COLUMN_DESCRIPTION,
                        Contract.StageEntry.COLUMN_PENALTY_INFO
                };

                queryBuilder.setTables(Contract.StageOnCompetitionEntry.TABLE_NAME +
                        " INNER JOIN " + Contract.StageEntry.TABLE_NAME +
                        " ON " + Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID + "=" + Contract.StageEntry.TABLE_NAME + "." + Contract.StageEntry._ID
                );
                sortOrder = Contract.StageOnCompetitionEntry.COLUMN_POSITION;
                break;

            case STAGE_ON_COMPETITION_ID:
                Log.i(LOG, "Case: STAGE_ON_COMPETITION_ID");
                break;

            case STAGES:
                Log.i(LOG, "Query: STAGES");
                queryBuilder.setTables(Contract.StageEntry.TABLE_NAME);
                long competitionId = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);
                queryBuilder.appendWhere(
                        Contract.StageEntry._ID + " Not IN " +
                        " (SELECT DISTINCT " + Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID + " FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME +
                        " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=" + competitionId + ")");
                break;

            case MEMBERS:
                Log.i(LOG, "Case: MEMBERS");
                projection = new String[] {
                        Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry._ID,
                        Contract.MemberEntry.COLUMN_START_NUMBER,
                        Contract.MemberEntry.COLUMN_PLACE,
                        Contract.MemberEntry.COLUMN_RESULT_TIME,
                        Contract.MemberEntry.COLUMN_SPORT_RANK,
                        Contract.PersonEntry.TABLE_NAME + "." + Contract.PersonEntry._ID + " AS personId",
                        Contract.PersonEntry.COLUMN_LAST_NAME,
                        Contract.PersonEntry.COLUMN_FIRST_NAME,
                        Contract.PersonEntry.COLUMN_MIDDLE_NAME,
                        Contract.PersonEntry.COLUMN_BIRTHDAY,
                        Contract.TeamEntry.TABLE_NAME + "." + Contract.TeamEntry._ID + " AS teamId",
                        Contract.TeamEntry.COLUMN_NAME,
                        Contract.GenderEntry.TABLE_NAME + "." + Contract.GenderEntry._ID + " AS genderId",
                        Contract.GenderEntry.COLUMN_GENDER
                };

                queryBuilder.setTables(Contract.MemberEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + Contract.TeamEntry.TABLE_NAME +
                " ON " + Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_TEAM_ID + "=" + Contract.TeamEntry.TABLE_NAME + "." + Contract.TeamEntry._ID +
                " LEFT OUTER JOIN " + Contract.PersonEntry.TABLE_NAME +
                " ON " + Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_PERSON_ID + "=" + Contract.PersonEntry.TABLE_NAME + "." + Contract.PersonEntry._ID +
                " LEFT OUTER JOIN " + Contract.GenderEntry.TABLE_NAME +
                " ON " + Contract.PersonEntry.TABLE_NAME + "." + Contract.PersonEntry.COLUMN_GENDER_ID + "=" + Contract.GenderEntry.TABLE_NAME + "." + Contract.GenderEntry._ID);
                break;

            case MEMBER_ID:
                Log.i(LOG, "Case: MEMBER_ID");
                break;

            case ATTEMPTS:
                Log.i(LOG, "Query Attempts");
                queryBuilder.setTables(Contract.AttemptEntry.TABLE_NAME);
                break;

            case STAGES_ON_ATTEMPTS:
                Log.i(LOG, "Query STAGES_ON_ATTEMPTS");
                queryBuilder.setTables(Contract.StageOnAttemptEntry.TABLE_NAME +
                " INNER JOIN " + Contract.StageOnCompetitionEntry.TABLE_NAME +
                " ON " + Contract.StageOnAttemptEntry.COLUMN_STAGE_ON_COMPETITION_ID + "=" + Contract.StageOnCompetitionEntry.TABLE_NAME + "." + Contract.StageOnCompetitionEntry._ID +
                " INNER JOIN " + Contract.StageEntry.TABLE_NAME +
                " ON " + Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID + "=" + Contract.StageEntry.TABLE_NAME + "." + Contract.StageEntry._ID);
                break;

            case TYPES:
                Log.i(LOG, "Query TYPES");
                queryBuilder.setTables(Contract.TypeEntry.TABLE_NAME);
                break;

            case TEAMS:
                Log.i(LOG, "Query TEAMS");
                queryBuilder.setTables(Contract.TeamEntry.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Log.i(LOG, "QUERY: " + queryBuilder.buildQuery(projection, selection, null, null, sortOrder, null));
        Cursor cursor = queryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int uriType = sURI_MATCHER.match(uri);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        long id;
        Uri resultUri;

        switch (uriType) {
            case COMPETITIONS:
                Log.i(LOG, "Insert COMPETITIONS");
                id = sqLiteDatabase.insert(Contract.CompetitionEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(COMPETITION_CONTENT_URI + "/" + id);
                break;

            case PERSONS:
                Log.i(LOG, "Insert PERSONS");
                id = sqLiteDatabase.insert(Contract.PersonEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(PERSON_CONTENT_URI + "/" + id);
                break;

            case MEMBERS:
                Log.i(LOG, "Insert MEMBERS");
                id = sqLiteDatabase.insert(Contract.MemberEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(MEMBER_CONTENT_URI + "/" + id);
                break;

            case STAGES_ON_COMPETITIONS:
                Log.i(LOG, "Insert STAGES_ON_COMPETITIONS");
                id = sqLiteDatabase.insert(Contract.StageOnCompetitionEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(STAGE_ON_COMPETITION_CONTENT_URI + "/" + id);
                break;

            case TEAMS:
                Log.i(LOG, "Insert TEAMS");
                id = sqLiteDatabase.insert(Contract.TeamEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(STAGE_ON_COMPETITION_CONTENT_URI + "/" + id);
                break;

            case ATTEMPTS:
                Log.i(LOG, "Insert ATTEMPTS");
                id = sqLiteDatabase.insert(Contract.AttemptEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(ATTEMPT_CONTENT_URI + "/" + id);
                break;

            case STAGES_ON_ATTEMPTS:
                Log.i(LOG, "Insert STAGES_ON_ATTEMPTS");
                id = sqLiteDatabase.insert(Contract.StageOnAttemptEntry.TABLE_NAME, null, values);
                resultUri = Uri.parse(STAGE_ON_ATTEMPT_CONTENT_URI + "/" + id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.i(LOG, "Inserted uri: " + resultUri.getPath());
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURI_MATCHER.match(uri);
        int rowsDeleted;
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        switch (uriType) {
            case STAGES_ON_COMPETITIONS:
                Log.i(LOG, "Delete STAGES_ON_COMPETITIONS");
                rowsDeleted = sqLiteDatabase.delete(Contract.StageOnCompetitionEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case COMPETITIONS:
                Log.i(LOG, "Delete COMPETITIONS");
                rowsDeleted = sqLiteDatabase.delete(Contract.CompetitionEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MEMBERS:
                Log.i(LOG, "Delete MEMBERS");
                rowsDeleted = sqLiteDatabase.delete(Contract.MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.i(LOG, "Rows Deleted = " + rowsDeleted);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURI_MATCHER.match(uri);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType){
            case STAGES_ON_COMPETITIONS:
                Log.i(LOG, "Update STAGES_ON_COMPETITIONS");
                rowsUpdated = sqLiteDatabase.update(Contract.StageOnCompetitionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MEMBER_ID:
                Log.i(LOG, "Update MEMBER_ID");
                rowsUpdated = sqLiteDatabase.update(Contract.MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MEMBERS:
                Log.i(LOG, "Update MEMBERS");
                rowsUpdated = sqLiteDatabase.update(Contract.MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TEAMS:
                Log.i(LOG, "Update TEAMS");
                rowsUpdated = sqLiteDatabase.update(Contract.TeamEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case PERSONS:
                Log.i(LOG, "Update PERSONS");
                rowsUpdated = sqLiteDatabase.update(Contract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case COMPETITIONS:
                Log.i(LOG, "Update COMPETITIONS");
                rowsUpdated = sqLiteDatabase.update(Contract.CompetitionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.i(LOG, "Rows Updated = " + rowsUpdated);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
