package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import ua.kyslytsia.tct.adapter.StageOnCompetitionCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;
import ua.kyslytsia.tct.utils.dslv.DragSortListView;

public class StagesOnCompetitionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG = "LOG! StageOnComp";

    DragSortListView mDragSortListView;
    private long mCompetitionId;
    private StageOnCompetitionCursorAdapter mSOCCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_on_competition);
        initToolbar();

        mCompetitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);

        initDragSortListView();
        initButtons();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.stage_on_competition_toolbar_subtitle);
        toolbar.setNavigationIcon(R.drawable.home_outline);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMainActivity = new Intent(StagesOnCompetitionActivity.this, MainActivity.class);
                startActivity(intentToMainActivity);
            }
        });
        }
    }

    private void initDragSortListView() {
        mDragSortListView = (DragSortListView) findViewById(R.id.listViewStageOnCompetition);
        mSOCCursorAdapter = new StageOnCompetitionCursorAdapter(this, null, Contract.STAGE_ON_COMPETITION_LOADER_ID);
        getSupportLoaderManager().initLoader(Contract.STAGE_ON_COMPETITION_LOADER_ID, null, this);
        mDragSortListView.setAdapter(mSOCCursorAdapter);
        mDragSortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StagesOnCompetitionActivity.this);
                alertDialog.setTitle(R.string.stage_on_competition_list_item_dialog_title);
                Cursor c = mSOCCursorAdapter.getCursor();
                Log.d(LOG, "stage: " + c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_NAME)) +
                        ", pos = " + c.getString(c.getColumnIndex(Contract.StageOnCompetitionEntry.COLUMN_POSITION)) +
                        ", soc_id = " + c.getInt(c.getColumnIndex(Contract.StageOnCompetitionEntry._ID)));
                String descriptionAndPenaltyInfo = DbHelper.getDescriptionAndPenaltyInfo(mSOCCursorAdapter.getCursor());
                alertDialog.setMessage(descriptionAndPenaltyInfo);
                alertDialog.setNegativeButton(R.string.stage_on_competition_list_item_dialog_negative, null);
                alertDialog.setPositiveButton(R.string.stage_on_competition_list_item_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteStage(position, id);
                    }
                });
                alertDialog.create().show();
            }
        });

        mDragSortListView.setDropListener(onDrop);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            ContentValues cv = new ContentValues();

            int newPosition = to + 1;
            if (from != to) {
                Log.i(LOG, "Adapter getCount = " + mSOCCursorAdapter.getCount());
                cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(mSOCCursorAdapter.getItemId(from))});
                Log.i(LOG, "First! Change position item_id = " + mSOCCursorAdapter.getItemId(from) + "from: " + from + " to position: " + newPosition);

                    /*upstairs element*/
                if (from > to) {
                    for (int i = to; i < mSOCCursorAdapter.getCount(); i++) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition + 1;
                        cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                        getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(mSOCCursorAdapter.getItemId(i))});
                        Log.i(LOG, "Change position item_id = " + mSOCCursorAdapter.getItemId(i) + " to position: " + newPosition);
                    }

                    /*downstairs element*/
                } else {
                    for (int i = to; i > 0; i--) {
                        if (i == from){
                            continue;
                        }
                        newPosition = newPosition - 1;
                        cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, newPosition);
                        getContentResolver().update(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv, Contract.StageOnCompetitionEntry._ID + "=?", new String[] {String.valueOf(mSOCCursorAdapter.getItemId(i))});
                        Log.i(LOG, "Change position item_id = " + mSOCCursorAdapter.getItemId(i) + " to position: " + newPosition);
                    }
                }
            }
            mSOCCursorAdapter.notifyDataSetChanged();
        }
    };

    private void deleteStage (int position, long id){
        Log.d(LOG, "pos = " + position + ", id = " + id);
        String where = Contract.StageOnCompetitionEntry._ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        int rowsDeleted = getContentResolver().delete(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, where, args);

        if (rowsDeleted > 0) {
        //displayed position and position in db = position in adapter + 1
        position = position+1;
        if (mSOCCursorAdapter.getItem(position) != null) {
            for (int i = position; i < mSOCCursorAdapter.getCount(); i++) {
                Uri u = ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI;
                ContentValues cv = new ContentValues();
                cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, i);
                String wherePosition = Contract.StageOnCompetitionEntry.COLUMN_POSITION + "=?";
                String[] selectionPosition= new String[] {String.valueOf(i+1)};
                getContentResolver().update(u, cv, wherePosition, selectionPosition);
            }
        }
    }}

    private void initButtons() {
        Button buttonToMembers = (Button) findViewById(R.id.buttonSoCToMembers);
        if (buttonToMembers != null) {
            buttonToMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StagesOnCompetitionActivity.this, MembersActivity.class);
                    intent.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, mCompetitionId);
                    startActivity(intent);
                }
            });
        }

        Button buttonAddStages = (Button) findViewById(R.id.buttonSoCAddStage);
        if (buttonAddStages != null) {
            buttonAddStages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentToAddStageActivity = new Intent(StagesOnCompetitionActivity.this, AddStageActivity.class);
                    intentToAddStageActivity.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, mCompetitionId);
                    intentToAddStageActivity.putExtra(Contract.StageOnCompetitionEntry.COLUMN_POSITION, mSOCCursorAdapter.getCount());
                    Log.i(LOG, "Put to AddStageActivity Intent mLastElementPosition = " + mSOCCursorAdapter.getCount());
                    startActivity(intentToAddStageActivity);
                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(StagesOnCompetitionActivity.this, ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, null, null, null);
        cursorLoader.setSelection(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=" + mCompetitionId);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSOCCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSOCCursorAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        /* It necessary if addStageActivity in BackStack and we should not be able to go back */
        super.onBackPressed();
        Intent toMembersActivity = new Intent(StagesOnCompetitionActivity.this, MembersActivity.class);
        startActivity(toMembersActivity);
        finish();
    }
}