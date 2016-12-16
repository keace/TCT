package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class NewCompetitionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG = "LOG New Competition";

    private EditText mDate, mName, mPlace, mRank, mPenalty;
    private long mSelectedTypeId, mSelectedDistanceId;
    private SimpleCursorAdapter mAdapterDistance;
    private SimpleCursorAdapter mAdapterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_competition);

        initToolbar();
        initEditTexts();
        initSpinnerType();
        initSpinnerDistance();
        initButtons();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.new_competition_activity_toolbar_subtitle);
        }
        setSupportActionBar(toolbar);
    }

    private void initEditTexts() {
        mDate = (EditText) findViewById(R.id.newCompetitionDate);
        mName = (EditText) findViewById(R.id.newCompetitionName);
        mPlace = (EditText) findViewById(R.id.newCompetitionPlace);
        mRank = (EditText) findViewById(R.id.newDistanceRank);
        mPenalty = (EditText) findViewById(R.id.newCompetitionPenalty);
    }

    private void initSpinnerType() {
        getSupportLoaderManager().initLoader(Contract.TYPES_LOADER_ID, null, this);

        Spinner spinnerType = (Spinner) findViewById(R.id.newCompetitionTypeSpinner);
        String[] fromType = new String[]{Contract.TypeEntry.COLUMN_NAME};
        int[] toType = new int[]{R.id.textViewItemType};
        mAdapterType = new SimpleCursorAdapter(this, R.layout.item_type, null, fromType, toType, Contract.TYPES_LOADER_ID);
        spinnerType.setAdapter(mAdapterType);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedTypeId = id;
                mAdapterDistance.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // stub
            }
        });
    }

    private void initSpinnerDistance() {
        Cursor cursorDistance = getContentResolver().query(ContentProvider.DISTANCE_CONTENT_URI, null, null, null, null);

        Spinner spinnerDistance = (Spinner) findViewById(R.id.newCompetitionDistanceSpinner);
        String[] fromDistance = new String[]{Contract.DistanceEntry.COLUMN_NAME};
        int[] toDistance = new int[]{R.id.textViewItemDistance};
        mAdapterDistance = new SimpleCursorAdapter(this, R.layout.item_distance, cursorDistance, fromDistance, toDistance, 1);
        spinnerDistance.setAdapter(mAdapterDistance);

        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDistanceId = id;
                Log.i(LOG, "Dist id = " + mSelectedDistanceId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // stub
            }
        });
    }

    private void initButtons() {
        Button buttonSaveToMembers = (Button) findViewById(R.id.buttonNewCompetitionSaveToMembers);
        buttonSaveToMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewCompetition();
                Intent intentToMembers = new Intent(NewCompetitionActivity.this, MembersActivity.class);
                startActivity(intentToMembers);
            }
        });

        Button buttonSaveToStagesOnCompetitions = (Button) findViewById(R.id.buttonNewCompetitionSaveToStage);
        buttonSaveToStagesOnCompetitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewCompetition();
                Intent intentToAddStages = new Intent(NewCompetitionActivity.this, AddStageActivity.class);
                startActivity(intentToAddStages);
            }
        });
    }

    public void makeNewCompetition() {
        // not null db fields sets required
        if (mDate.getText().toString().trim().equals("")) {
            mDate.setError(getString(R.string.new_competition_activity_error_no_date));
        } else if (mName.getText().toString().trim().equals("")) {
            mName.setError(getString(R.string.new_competition_activity_error_no_name));
        } else if (mRank.getText().toString().trim().equals("")) {
            mRank.setError(getString(R.string.new_competition_activity_error_no_rank));
        } else if (mPenalty.getText().toString().trim().equals("")) {
            mPenalty.setError(getString(R.string.new_competition_activity_error_no_penalty_cost));

        } else {
            insertNewCompetitionToDb();
        }
    }

    private void insertNewCompetitionToDb() {
        ContentValues cv = new ContentValues();
        cv.put(Contract.CompetitionEntry.COLUMN_DATE, mDate.getText().toString());
        cv.put(Contract.CompetitionEntry.COLUMN_NAME, mName.getText().toString());
        cv.put(Contract.CompetitionEntry.COLUMN_PLACE, mPlace.getText().toString());
        cv.put(Contract.CompetitionEntry.COLUMN_TYPE_ID, mSelectedTypeId);
        cv.put(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, mSelectedDistanceId);
        cv.put(Contract.CompetitionEntry.COLUMN_RANK, mRank.getText().toString());
        cv.put(Contract.CompetitionEntry.COLUMN_PENALTY_COST, Integer.valueOf(mPenalty.getText().toString()));
        cv.put(Contract.CompetitionEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_OPENED);

        long competitionId = Long.parseLong(
                getContentResolver().insert(ContentProvider.COMPETITION_CONTENT_URI, cv).
                        getLastPathSegment());
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, mSelectedDistanceId).apply();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContentProvider.TYPE_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapterType.swapCursor(data);
        initSpinnerDistance();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterType.swapCursor(null);
    }
}