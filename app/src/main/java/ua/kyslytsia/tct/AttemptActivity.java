package ua.kyslytsia.tct;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.utils.Chronometer;


public class AttemptActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
        SimpleCursorAdapter adapter;

    TextView textViewTime, textViewPenaltySum, textViewPenaltyCost, textViewResult;
    TextView minutes, seconds, millis;
    ListView listView;
    int penaltyCost;

    int competition_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Заезд");
        setSupportActionBar(toolbar);

        //get Member_id from Intent
        long memberId = getIntent().getLongExtra(Contract.MemberEntry._ID, 0);
        competition_id = getIntent().getIntExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        textViewTime = (TextView) findViewById(R.id.textViewAttemptTime);
        textViewPenaltyCost = (TextView) findViewById(R.id.textViewAttemptPenaltyCost);
        textViewPenaltySum = (TextView) findViewById(R.id.textViewAttemptPenaltySum);
        textViewResult = (TextView) findViewById(R.id.textViewAttemptResultTime);

        minutes = (TextView) findViewById(R.id.textViewAttemptMinutes);
        seconds = (TextView) findViewById(R.id.textViewAttemptSeconds);
        millis = (TextView) findViewById(R.id.textViewAttemptMillis);

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
                textViewTime.setText(ch.getText());

                millis.setText(String.valueOf(ch.getMilliseconds()));
                seconds.setText(String.valueOf(ch.getSeconds()));
                minutes.setText(String.valueOf(ch.getMinutes()));
                int total = 0;
                for(int i=0; i<listView.getChildCount(); i++){
                    View view = listView.getChildAt(i);
                    EditText editText = (EditText) view.findViewById(R.id.editTextSOAPenaltyOnStage);
                    String string = editText.getText().toString();
                    if(!string.equals(""))
                        total+=Integer.parseInt(string);
                }
                textViewPenaltySum.setText(String.valueOf(total));

                long penaltyWithCost = Long.valueOf(textViewPenaltyCost.getText().toString()) * Long.valueOf(textViewPenaltySum.getText().toString()) * 1000L + ch.getTimeElapsed();
                String result = ch.timeLongMillisToString(penaltyWithCost);
                textViewResult.setText(result);
            }
        });

        Button resetAll = (Button) findViewById(R.id.buttonAttemptResetAll);
        resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPenaltySum.setText("");
                textViewTime.setText("");
                textViewResult.setText("");
                ch.setBase(SystemClock.elapsedRealtime());
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
