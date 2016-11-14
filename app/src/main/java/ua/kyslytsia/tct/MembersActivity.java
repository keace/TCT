package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ua.kyslytsia.tct.adapter.MembersAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.mocks.Person;

public class MembersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG = "LOG! Members Activity";

    MembersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Список участников");
        toolbar.setNavigationIcon(R.drawable.home);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMainActivity = new Intent(MembersActivity.this, MainActivity.class);
                startActivity(intentToMainActivity);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listViewMembers);

        final long competitionId = getIntent().getLongExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        //SQLiteDatabase sqLiteDatabase = MainActivity.dbHelper.getReadableDatabase();

        //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.MemberEntry.TABLE_NAME + " WHERE " + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?;", new String[]{String.valueOf(competitionId)});
        //Cursor c = sqLiteDatabase.query(Contract.MemberEntry.TABLE_NAME, null, null, null, null, null, null);
        getSupportLoaderManager().initLoader(Contract.MEMBERS_LOADER_ID, null, this);
        adapter = new MembersAdapter(this, null, Contract.MEMBERS_LOADER_ID);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.textViewMembersTime);
                if (textView.getText().toString() == "") {
                    Log.i(LOG, "Time is null. Starting Attempt Activity");
                    Intent intentToAttempt = new Intent(MembersActivity.this, AttemptActivity.class);
                    intentToAttempt.putExtra(Contract.MemberEntry._ID, id);
                    intentToAttempt.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                    startActivity(intentToAttempt);
                    //intentToAttempt.putExtra(Contract.MemberEntry.COLUMN_PERSON_ID, id);
                } else {
                    Toast.makeText(MembersActivity.this, "Этот участник уже прошел дистанцию", Toast.LENGTH_SHORT).show();
                    Log.i(LOG, "Time not null. Can't start activity");
                }
            }
        });

        Button buttonToNewMember = (Button) findViewById(R.id.buttonMembersToNewMember);
        buttonToNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToNewMember = new Intent(MembersActivity.this, NewMemberActivity.class);
                intentToNewMember.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToNewMember);
            }
        });

        Button buttonToStages = (Button) findViewById(R.id.buttonMembersToStages);
        buttonToStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStages = new Intent(MembersActivity.this, StagesOnCompetitionActivity.class);
                intentToStages.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToStages);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.secondary_activities, menu);
//        return true;
//    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

        //noinspection SimplifiableIfStatement
        case R.id.home:
            Intent intentToMainActivity = new Intent(MembersActivity.this, MainActivity.class);
            startActivity(intentToMainActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(MembersActivity.this, ContentProvider.MEMBER_CONTENT_URI, null, null, null, null);
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
