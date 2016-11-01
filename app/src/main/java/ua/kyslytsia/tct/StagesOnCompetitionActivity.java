package ua.kyslytsia.tct;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import ua.kyslytsia.tct.adapter.StageOnCompetitionCursorAdapter;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;
import ua.kyslytsia.tct.utils.dslv.DragSortController;
import ua.kyslytsia.tct.utils.dslv.DragSortListView;
import ua.kyslytsia.tct.utils.dslv.SimpleFloatViewManager;

public class StagesOnCompetitionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG = "SOC LOG!";

    DragSortListView listView;
    int competitionId;

    int dragPosition;
    long dragId;
    Cursor c;

    StageOnCompetitionCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_on_competition);
        getSupportLoaderManager().initLoader(1, null, this);

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
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME + " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + " = " + competitionId + " ORDER BY " + Contract.StageOnCompetitionEntry.COLUMN_POSITION, null);
        adapter = new StageOnCompetitionCursorAdapter(this, c, true);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(StagesOnCompetitionActivity.this, "Id: " + id + ", position = " + position, Toast.LENGTH_SHORT).show();
                //MainActivity.dbHelper.dropStageOnCompetition(id);
            }
        });

        listView.setDropListener(onDrop);

          //DragSortController controller = new DragSortController(listView);
//        controller.setRemoveEnabled(false);
//        controller.setSortEnabled(true);
//        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        //SimpleFloatViewManager viewManager = new SimpleFloatViewManager(listView);

        //listView.setFloatViewManager(viewManager);
//        listView.setOnTouchListener(controller);
//        listView.setDragEnabled(true);

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                ClipData clipData = ClipData.newPlainText("", "");
//                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
//                dragPosition = position;
//                dragId = id;
//                parent.startDrag(clipData, shadow, null, 0);
//                return false;
//            }
//        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            int newPosition = to + 1;
            if (from != to) {
                DbHelper dbHelper = new DbHelper(StagesOnCompetitionActivity.this);
                Log.i(LOG, "Adapter getCount = " + adapter.getCount());
                dbHelper.updateStageOnCompetitionPosition(adapter.getItemId(from), newPosition);
                Log.i(LOG, "First! Change position item_id = " + adapter.getItemId(from) + "from: " + from + " to position: " + newPosition);

                    /*upstairs element*/
                if (from > to) {
                    for (int i = to; i < adapter.getCount(); i++) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition + 1;
                        dbHelper.updateStageOnCompetitionPosition(adapter.getItemId(i), newPosition);
                        Log.i(LOG, "Change position item_id = " + adapter.getItemId(i) + " to position: " + newPosition);
                    }

                    /*downstairs element*/
                } else {
                    for (int i = to; i > 0; i--) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition - 1;
                        dbHelper.updateStageOnCompetitionPosition(adapter.getItemId(i), newPosition);
                        Log.i(LOG, "Change position item_id = " + adapter.getItemId(i) + " to position: " + newPosition);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    };
}