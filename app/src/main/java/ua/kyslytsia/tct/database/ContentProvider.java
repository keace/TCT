package ua.kyslytsia.tct.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ContentProvider extends android.content.ContentProvider {

    private final String LOG = "LOG! " + getClass().getSimpleName();
    private DbHelper dbHelper;

    private static final String SLASH_LATTICE = "/#";

    private static final String AUTHORITY = "ua.kyslytsia.tct.database";

//    private static final String PERSON_BASE_PATH = Contract.PersonEntry.TABLE_NAME;
//    private static final String COMPETITION_BASE_PATH = Contract.CompetitionEntry.TABLE_NAME;

    public static final Uri GENDER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.GenderEntry.TABLE_NAME);
    public static final Uri PERSON_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.PersonEntry.TABLE_NAME);
    public static final Uri TYPE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.TypeEntry.TABLE_NAME);
    public static final Uri DISTANCE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.DistanceEntry.TABLE_NAME);
    public static final Uri COMPETITION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.CompetitionEntry.TABLE_NAME);
    public static final Uri STAGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.StageEntry.TABLE_NAME);
    public static final Uri JUDGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.JudgeEntry.TABLE_NAME);
    public static final Uri TEAM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.TeamEntry.TABLE_NAME);
    public static final Uri MEMBER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.MemberEntry.TABLE_NAME);
    public static final Uri ATTEMPN_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + Contract.AttemptEntry.TABLE_NAME);
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

//    private static final int PERSON_LASTNAME = 1002;
//    private static final int PERSON_FIRST_NAME = 1003;
//    private static final int PERSON_MIDDLE_NAME = 1004;
//    private static final int PERSON_GENDER_ID = 1005;
//    private static final int PERSON_BIRTHDAY = 1006;

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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        //checkColumns(projection);
        int uriType = sURI_MATCHER.match(uri);
        switch (uriType) {
            case PERSONS:
                queryBuilder.setTables(Contract.PersonEntry.TABLE_NAME);
                break;
            case PERSON_ID:
                queryBuilder.setTables(Contract.PersonEntry.TABLE_NAME);
                queryBuilder.appendWhere(Contract.PersonEntry._ID + "=" + uri.getLastPathSegment());
                break;
            case COMPETITIONS:
                projection = new String[] {
                        Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry._ID,
                        Contract.CompetitionEntry.COLUMN_DATE,
                        Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry.COLUMN_NAME,
                        Contract.CompetitionEntry.COLUMN_PLACE,
                        Contract.CompetitionEntry.COLUMN_RANK,
                        Contract.CompetitionEntry.COLUMN_PENALTY_TIME,
                        Contract.TypeEntry.TABLE_NAME + "." + Contract.TypeEntry.COLUMN_NAME + " AS " + Contract.TYPE_NAME_ADAPTED,
                        Contract.DistanceEntry.TABLE_NAME + "." + Contract.DistanceEntry.COLUMN_NAME + " AS " + Contract.DISTANCE_NAME_ADAPTED
                };
                queryBuilder.setTables(Contract.CompetitionEntry.TABLE_NAME +
                        " INNER JOIN " + Contract.TypeEntry.TABLE_NAME +
                        " ON " + Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry.COLUMN_TYPE_ID + " = " + Contract.TypeEntry.TABLE_NAME + "." + Contract.TypeEntry._ID +
                        " INNER JOIN " + Contract.DistanceEntry.TABLE_NAME +
                        " ON " + Contract.CompetitionEntry.COLUMN_DISTANCE_ID + " = " + Contract.DistanceEntry.TABLE_NAME + "." + Contract.DistanceEntry._ID);
                break;
            case COMPETITION_ID:
                queryBuilder.setTables(Contract.CompetitionEntry.TABLE_NAME);
                queryBuilder.appendWhere(Contract.CompetitionEntry._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Log.i(LOG, queryBuilder.buildQuery(projection, selection, null, null, sortOrder, null));
        Cursor cursor = queryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURI_MATCHER.match(uri);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case PERSONS:
                id = sqLiteDatabase.insert(Contract.PersonEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PERSON_CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
