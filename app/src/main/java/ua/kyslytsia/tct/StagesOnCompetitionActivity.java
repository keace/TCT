package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import ua.kyslytsia.tct.adapter.StageOnCompetitionCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.utils.dslv.DragSortListView;

public class StagesOnCompetitionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG = "LOG! StageOnComp";

    DragSortListView listView;
    int competitionId;
    StageOnCompetitionCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_on_competition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Этапы на соревновании");
        setSupportActionBar(toolbar);

        competitionId = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSoc);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addStageIntent = new Intent(StagesOnCompetitionActivity.this, AddStageActivity.class);
                    addStageIntent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                    startActivity(addStageIntent);
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                            */
                }
            });
        }

        Button buttonToMembers = (Button) findViewById(R.id.buttonSoCToMembers);
        buttonToMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StagesOnCompetitionActivity.this, MembersActivity.class);
                intent.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });

        Button buttonAddStages = (Button) findViewById(R.id.buttonSoCAddStage);
        buttonAddStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StagesOnCompetitionActivity.this, AddStageActivity.class);
                intent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });

        listView = (DragSortListView) findViewById(R.id.listViewStageOnCompetition);

        //REFACTOR TO CONTENT LOADER c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME + " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + " = " + competitionId + " ORDER BY " + Contract.StageOnCompetitionEntry.COLUMN_POSITION, null);
        adapter = new StageOnCompetitionCursorAdapter(this, null, Contract.STAGE_ON_COMPETITION_LOADER_ID);
        getSupportLoaderManager().initLoader(Contract.STAGE_ON_COMPETITION_LOADER_ID, null, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(StagesOnCompetitionActivity.this, "Id: " + id + ", position = " + position, Toast.LENGTH_SHORT).show();
                //MainActivity.dbHelper.dropStageOnCompetition(id);
            }
        });

        listView.setDropListener(onDrop);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(StagesOnCompetitionActivity.this, ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, null, null, null);
        cursorLoader.setSelection(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=" + competitionId);
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

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            ContentValues cv = new ContentValues();

            int newPosition = to + 1;
            if (from != to) {
                Log.i(LOG, "Adapter getCount = " + adapter.getCount());
                cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(adapter.getItemId(from))});
                Log.i(LOG, "First! Change position item_id = " + adapter.getItemId(from) + "from: " + from + " to position: " + newPosition);

                    /*upstairs element*/
                if (from > to) {
                    for (int i = to; i < adapter.getCount(); i++) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition + 1;
                        cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                        getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(adapter.getItemId(i))});
                        Log.i(LOG, "Change position item_id = " + adapter.getItemId(i) + " to position: " + newPosition);
                    }

                    /*downstairs element*/
                } else {
                    for (int i = to; i > 0; i--) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition - 1;
                        cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                        getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(adapter.getItemId(i))});
                        Log.i(LOG, "Change position item_id = " + adapter.getItemId(i) + " to position: " + newPosition);
                    }
                }
            }
            //getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, null, null);
            adapter.notifyDataSetChanged();
        }
    };
}