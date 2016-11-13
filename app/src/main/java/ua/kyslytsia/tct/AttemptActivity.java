package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.mocks.StageOnAttempt;
import ua.kyslytsia.tct.utils.Chronometer;


public class AttemptActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
        SimpleCursorAdapter adapter;

    private static final String LOG = "Log Attempt Activity";
    TextView textViewTime, textViewPenaltySum, textViewPenaltyCost, textViewResult;
    TextView minutes, seconds, millis;
    ListView listView;
    int penaltyCost, penaltyTotal = 0;
    String timeString, resultTimeString;
    long timeLong, resultTimeLong;
    long competition_id;
    private ArrayList<StageOnAttempt> stageOnAttemptList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Заезд");
        setSupportActionBar(toolbar);

        //get Member_id from Intent
        final long memberId = getIntent().getLongExtra(Contract.MemberEntry._ID, 0);
        competition_id = getIntent().getLongExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        textViewTime = (TextView) findViewById(R.id.textViewAttemptTime);
        textViewPenaltyCost = (TextView) findViewById(R.id.textViewAttemptPenaltyCost);
        textViewPenaltySum = (TextView) findViewById(R.id.textViewAttemptPenaltySum);
        textViewResult = (TextView) findViewById(R.id.textViewAttemptResultTime);

        Cursor c = getContentResolver().query(Uri.parse(ContentProvider.COMPETITION_CONTENT_URI + "/" + competition_id), new String[]{Contract.CompetitionEntry.COLUMN_PENALTY_TIME}, null, null, null);
        //Cursor c = MainActivity.dbHelper.getReadableDatabase().query(Contract.CompetitionEntry.TABLE_NAME, null, Contract.CompetitionEntry._ID, new String[]{String.valueOf(competition_id)}, null, null, null, null);
        c.moveToFirst();
        String s = c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_TIME));
        textViewPenaltyCost.setText(s);

        listView = (ListView) findViewById(R.id.listViewAttempt);
        getSupportLoaderManager().initLoader(Contract.ATTEMPT_LOADER_ID, null, this);
        String[] from = new String[] {Contract.StageOnCompetitionEntry.COLUMN_POSITION, Contract.STAGE_NAME_ADAPTED};
        int[] to = new int[] {R.id.textViewSOAStagePosition, R.id.textViewSOAStageName};
        adapter = new SimpleCursorAdapter(AttemptActivity.this, R.layout.item_stage_on_attempt, null, from, to, Contract.ATTEMPT_LOADER_ID);
        listView.setAdapter(adapter);

        final Chronometer ch = (Chronometer) findViewById(R.id.chronometer);

        Button buttonStart = (Button) findViewById(R.id.buttonAttemptStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.setBase(SystemClock.elapsedRealtime());
                ch.start();
            }
        });

        Button buttonStop = (Button) findViewById(R.id.buttonAttemptStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.stop();
                penaltyTotal = 0;
                String penaltyOnStage;
                textViewTime.setText(ch.getText());

                for (int i=0; i<listView.getChildCount(); i++) {

                    View view = listView.getChildAt(i);
                    EditText editTextPenaltyOnStage = (EditText) view.findViewById(R.id.editTextSOAPenaltyOnStage);
                    if (editTextPenaltyOnStage.getText().toString().equals("")) {
                        penaltyOnStage = "0";
                    } else {
                        penaltyOnStage = editTextPenaltyOnStage.getText().toString();
                    }

                    stageOnAttemptList.add(new StageOnAttempt(listView.getAdapter().getItemId(i), Long.parseLong(penaltyOnStage)));
                    Log.d(LOG, "StageOnAttempt: " + stageOnAttemptList.get(i));
                    if(!penaltyOnStage.equals(""))
                        penaltyTotal+=Integer.parseInt(penaltyOnStage);
                }
                textViewPenaltySum.setText(String.valueOf(penaltyTotal));

                timeLong = ch.getTimeElapsed();
                timeString = ch.timeLongMillisToString(timeLong);

                resultTimeLong = Long.valueOf(textViewPenaltyCost.getText().toString()) * Long.valueOf(textViewPenaltySum.getText().toString()) * 1000L + timeLong;
                resultTimeString = ch.timeLongMillisToString(resultTimeLong);

                textViewResult.setText(resultTimeString);
            }
        });

        Button resetAllButton = (Button) findViewById(R.id.buttonAttemptResetAll);
        resetAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPenaltySum.setText("");
                textViewTime.setText("");
                textViewResult.setText("");
                ch.setBase(SystemClock.elapsedRealtime());
            }
        });

        Button writeButton = (Button) findViewById(R.id.buttonAttemptWrite);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long attemptId;

                ContentValues cv = new ContentValues();
                cv.put(Contract.AttemptEntry.COLUMN_COMPETITION_ID, competition_id);
                cv.put(Contract.AttemptEntry.COLUMN_MEMBERS_ID, memberId);
                cv.put(Contract.AttemptEntry.COLUMN_TRY_NUMBER, 1);  //TRY NUMBER NOT HANDLING
                cv.put(Contract.AttemptEntry.COLUMN_PENALTY_TOTAL, penaltyTotal);
                cv.put(Contract.AttemptEntry.COLUMN_TIME, timeString);
                cv.put(Contract.AttemptEntry.COLUMN_RESULT_TIME, resultTimeLong);
                cv.put(Contract.AttemptEntry.COLUMN_IS_CLOSED, 1);
                Log.i(LOG, "Try to insert Attempt: " + cv.toString());
                attemptId = Long.parseLong(getContentResolver().insert(ContentProvider.ATTEMPT_CONTENT_URI, cv).getLastPathSegment());
                Log.i(LOG, "Insert ok. attemptId = " + attemptId);
                cv.clear();
                for (int i = 0; i < stageOnAttemptList.size(); i++){
                    cv.put(Contract.StageOnAttemptEntry.COLUMN_ATTEMPT_ID, attemptId);
                    cv.put(Contract.StageOnAttemptEntry.COLUMN_STAGE_ON_COMPETITION_ID, stageOnAttemptList.get(i).getStage_on_competition_id());
                    cv.put(Contract.StageOnAttemptEntry.COLUMN_PENALTY, stageOnAttemptList.get(i).getPenalty());
                    Log.i(LOG, "Try to insert Stage on attempt: " + cv.toString());
                    getContentResolver().insert(ContentProvider.STAGE_ON_ATTEMPT_CONTENT_URI, cv);
                    cv.clear();
                }
                cv.put(Contract.MemberEntry.COLUMN_TIME, resultTimeLong);
                Log.i(LOG, "Try to update Member with id = " + memberId + ", add resultTimeLong = " + resultTimeLong);
                getContentResolver().update(Uri.parse(ContentProvider.MEMBER_CONTENT_URI + "/" + memberId), cv, Contract.MemberEntry._ID + "=" + memberId, null);
                cv.clear();
                Intent toMembersIntent = new Intent(AttemptActivity.this, MembersActivity.class);
                toMembersIntent.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competition_id);
                startActivity(toMembersIntent);
            }
        });

//        Button buttonReset = (Button) findViewById(R.id.buttonAttemptReset);
//        buttonReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ch.setBase(SystemClock.elapsedRealtime());
//                ch.stop();
//            }
//        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case Contract.ATTEMPT_LOADER_ID:
                cursorLoader = new CursorLoader(AttemptActivity.this, ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, null, null, null);
                break;

//            case Contract.COMPETITIONS_LOADER_ID:
//                cursorLoader = new CursorLoader(AttemptActivity.this, ContentProvider.COMPETITION_CONTENT_URI, null, null, null, null);
//                break;
        }

        return cursorLoader;
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
