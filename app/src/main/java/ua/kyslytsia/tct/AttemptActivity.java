package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import ua.kyslytsia.tct.adapter.AttemptCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.mocks.StageOnAttempt;
import ua.kyslytsia.tct.utils.Chronometer;


public class AttemptActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    AttemptCursorAdapter adapter;
    private static final String LOG = "Log! Attempt Activity";

    TextView textViewTime, textViewPenaltySum, textViewPenaltyCost, textViewResult;
    int penaltyTotal;
    String timeString, resultTimeString;
    long timeLong, resultTimeLong;
    long competitionId, memberId;
    Button buttonStart, buttonStop, buttonWriteResults;

    GridView mGridViewStages;
    private ArrayList<StageOnAttempt> mStageOnAttemptList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt);
        initToolbar();

        memberId = getIntent().getLongExtra(Contract.MemberEntry._ID, 0);
        competitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        textViewTime = (TextView) findViewById(R.id.textViewAttemptTime);
        textViewPenaltyCost = (TextView) findViewById(R.id.textViewAttemptPenaltyCost);
        textViewPenaltySum = (TextView) findViewById(R.id.textViewAttemptPenaltySum);
        textViewResult = (TextView) findViewById(R.id.textViewAttemptResultTime);

        String penaltyCost = getPenaltyCost();
        textViewPenaltyCost.setText(penaltyCost);

        initGridViewWithStages();
        initChronometerWithButtons();

        buttonWriteResults = (Button) findViewById(R.id.buttonAttemptWriteResults);
        buttonWriteResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStop.performClick();  /* if user not click "Stop" button before clicking "Save results" */
                long attemptId = insertAttemptToDb();
                insertAllStagesOnAttemptToDb(attemptId);
                updateResultTimeInMember();
                Intent toMembersIntent = new Intent(AttemptActivity.this, MembersActivity.class);
                startActivity(toMembersIntent);
            }
        });
    }

    private void updateResultTimeInMember() {
        ContentValues cv = new ContentValues();
        cv.put(Contract.MemberEntry.COLUMN_RESULT_TIME, resultTimeLong);
        Log.i(LOG, "Try to update Member with id = " + memberId + ", add resultTimeLong = " + resultTimeLong);
        getContentResolver().update(Uri.parse(ContentProvider.MEMBER_CONTENT_URI + "/" + memberId), cv, Contract.MemberEntry._ID + "=" + memberId, null);
    }

    private void insertAllStagesOnAttemptToDb(long attemptId) {
        ContentValues cv = new ContentValues();
        Log.d(LOG, "StageOnAttempt size " + mStageOnAttemptList.size());

        for (int i = 0; i < mStageOnAttemptList.size(); i++) {
            cv.put(Contract.StageOnAttemptEntry.COLUMN_ATTEMPT_ID, attemptId);
            cv.put(Contract.StageOnAttemptEntry.COLUMN_STAGE_ON_COMPETITION_ID, mStageOnAttemptList.get(i).getStage_on_competition_id());
            cv.put(Contract.StageOnAttemptEntry.COLUMN_PENALTY, mStageOnAttemptList.get(i).getPenalty());
            Log.i(LOG, "Try to insert Stage on attempt: " + cv.toString());
            getContentResolver().insert(ContentProvider.STAGE_ON_ATTEMPT_CONTENT_URI, cv);
            cv.clear();
        }
    }

    private void initChronometerWithButtons() {
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);

        buttonStart = (Button) findViewById(R.id.buttonAttemptStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        buttonStop = (Button) findViewById(R.id.buttonAttemptStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                mGridViewStages.clearFocus(); /* Clear focus is important! For work with onChangeFocusListener in Adapter with ViewHolder (editText in listView or gridView is amazing) */

                penaltyTotal = 0;
                textViewTime.setText(chronometer.getText());

                processThePenaltiesResults();
                textViewPenaltySum.setText(String.valueOf(penaltyTotal));

                timeLong = chronometer.getTimeElapsed();
                timeString = chronometer.timeLongMillisToString(timeLong);

                resultTimeLong = Long.valueOf(textViewPenaltyCost.getText().toString()) * Long.valueOf(textViewPenaltySum.getText().toString()) * 1000L + timeLong;
                resultTimeString = chronometer.timeLongMillisToString(resultTimeLong);

                textViewResult.setText(resultTimeString);
            }
        });
    }

    private void initGridViewWithStages() {
        mGridViewStages = (GridView) findViewById(R.id.gridViewAttempt);
        getSupportLoaderManager().initLoader(Contract.ATTEMPT_LOADER_ID, null, this);
        adapter = new AttemptCursorAdapter(this, null, Contract.ATTEMPT_LOADER_ID);
        mGridViewStages.setAdapter(adapter);
    }

    private String getPenaltyCost() {
        Cursor c = getContentResolver().query(Uri.parse(ContentProvider.COMPETITION_CONTENT_URI + "/" + competitionId), new String[]{Contract.CompetitionEntry.COLUMN_PENALTY_COST}, null, null, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_COST));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.attempt_activity_toolbar_subtitle);
        }
        setSupportActionBar(toolbar);
    }

    public long insertAttemptToDb() {
        long attemptId;
        ContentValues cv = new ContentValues();
        cv.put(Contract.AttemptEntry.COLUMN_COMPETITION_ID, competitionId);
        cv.put(Contract.AttemptEntry.COLUMN_MEMBERS_ID, memberId);
        cv.put(Contract.AttemptEntry.COLUMN_TRY_NUMBER, 1);  /* Try number not handling yet */
        cv.put(Contract.AttemptEntry.COLUMN_PENALTY_TOTAL, penaltyTotal);
        cv.put(Contract.AttemptEntry.COLUMN_DISTANCE_TIME, timeString);
        cv.put(Contract.AttemptEntry.COLUMN_RESULT_TIME, resultTimeLong);
        cv.put(Contract.AttemptEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_CLOSED);
        Log.i(LOG, "Try to insert Attempt: " + cv.toString());
        attemptId = Long.parseLong(getContentResolver().insert(ContentProvider.ATTEMPT_CONTENT_URI, cv).getLastPathSegment());
        Log.i(LOG, "Insert ok. attemptId = " + attemptId);
        return attemptId;
    }

    private void processThePenaltiesResults() {

        Iterator<Map.Entry<Integer, Integer>> iterator = adapter.getInputValues().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            saveStageAndPenaltyToList(entry.getKey(), entry.getValue());
            Log.i(LOG, "Add to List position: " + entry.getKey() + " penalty: " + entry.getValue());
            penaltyTotal += entry.getValue();
        }
    }

    private void saveStageAndPenaltyToList(int position, int penaltyOnStage) {
        long socId = mGridViewStages.getAdapter().getItemId(position-1); //first position = 0

        String selection = Contract.StageOnCompetitionEntry.TABLE_NAME + "." + Contract.StageOnCompetitionEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(socId)};
        Cursor c = getContentResolver().query(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, selection, selectionArgs, null);
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(Contract.STAGE_NAME_ADAPTED));

        mStageOnAttemptList.add(new StageOnAttempt(socId, penaltyOnStage, name));
        Log.d(LOG, "StageOnAttempt position: " + position + ", id: " + mGridViewStages.getAdapter().getItemId(position) + ", penalty: " + penaltyOnStage + "name: " + name);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectCompetitionId = Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(competitionId)};
        return new CursorLoader(AttemptActivity.this, ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, selectCompetitionId, selectArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
