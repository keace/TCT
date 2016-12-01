package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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

import java.io.IOException;

import jxl.write.WriteException;
import ua.kyslytsia.tct.adapter.MembersAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.dialog.MembersDialogFragment;
import ua.kyslytsia.tct.utils.ExcelExport.ExportCompetitionDataToExcel;

public class MembersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG = "LOG! Members Activity";

    private MembersAdapter adapter;
    private long competitionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        initToolbar();

        competitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        initButtons();
        initListView();
    }

    private void initListView() {
        ListView listViewMembers = (ListView) findViewById(R.id.listViewMembers);
        getSupportLoaderManager().initLoader(Contract.MEMBERS_LOADER_ID, null, this);
        adapter = new MembersAdapter(this, null, Contract.MEMBERS_LOADER_ID);
        listViewMembers.setAdapter(adapter);

        listViewMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (timeIsNull(view)) {
                    startAttemptActivityWithParameters(id);
                } else {
                    Toast.makeText(MembersActivity.this, R.string.members_activity_toast_error_already_finished, Toast.LENGTH_SHORT).show();
                    Log.i(LOG, "Time not null. Can't start activity");
                }
            }
        });

        listViewMembers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogForMember(id);
                return true;
            }
        });
    }

    private void showDialogForMember(long id) {
        Log.i(LOG, "Dialog for member with Id = " + id);
        android.app.FragmentManager fm = getFragmentManager();
        MembersDialogFragment membersDialogFragment = MembersDialogFragment.newInstance(id);
        membersDialogFragment.show(fm, "membersDialog");
    }

    private void initButtons() {
        Button buttonToNewMember = (Button) findViewById(R.id.buttonMembersToNewMember);
        Button buttonToStages = (Button) findViewById(R.id.buttonMembersToStages);
        Button buttonExportToExcel = (Button) findViewById(R.id.buttonMembersExportToExcel);

        if (competitionIsClosed()) {
            buttonToNewMember.setEnabled(false);
            buttonToStages.setEnabled(false);
            buttonExportToExcel.setVisibility(View.VISIBLE);
        } else {
            buttonToNewMember.setEnabled(true);
            buttonToStages.setEnabled(true);
            buttonExportToExcel.setVisibility(View.GONE);
        }
    }

    public void startNewMemberActivity(View buttonView) {
        Intent intentToNewMember = new Intent(MembersActivity.this, NewMemberActivity.class);
        startActivity(intentToNewMember);
    }

    public void startStagesOnCompetitionActivity(View buttonView) {
        Intent intentToStagesOnCompetition = new Intent(MembersActivity.this, StagesOnCompetitionActivity.class);
        startActivity(intentToStagesOnCompetition);
    }

    private boolean timeIsNull(View currentMemberView) {
        TextView textViewMembersTime = (TextView) currentMemberView.findViewById(R.id.textViewMembersTime);
        return textViewMembersTime.getText().toString().equals("");
    }

    private void startAttemptActivityWithParameters(long memberId) {
        Log.i(LOG, "Time is null. Starting Attempt Activity");
        Intent intentToAttempt = new Intent(MembersActivity.this, AttemptActivity.class);
        intentToAttempt.putExtra(Contract.MemberEntry._ID, memberId);
        intentToAttempt.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
        startActivity(intentToAttempt);
    }

    private boolean competitionIsClosed(){
        String where = Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry._ID + "=? AND " + Contract.CompetitionEntry.COLUMN_IS_CLOSED + "=?";
        String[] whereArgs = new String[] {String.valueOf(competitionId), String.valueOf(Contract.COMPETITION_CLOSED)};
        Cursor cursor = getContentResolver().query(ContentProvider.COMPETITION_CONTENT_URI, null, where, whereArgs, null);
        Log.i(LOG, "Cursor getCount = " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.members_activity_toolbar_subtitle);
        toolbar.setNavigationIcon(R.drawable.home_outline);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMainActivity = new Intent(MembersActivity.this, MainActivity.class);
                startActivity(intentToMainActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_competition_complete:
                showCompetitionCompleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCompetitionCompleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.competition_complete_dialog_title);
        alertDialog.setMessage(R.string.competition_complete_dialog_message);
        alertDialog.setNegativeButton(R.string.competition_complete_dialog_negative, null);
        alertDialog.setPositiveButton(R.string.main_activity_alert_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentValues cv = new ContentValues();
                cv.put(Contract.CompetitionEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_CLOSED);
                String where = Contract.CompetitionEntry._ID + "=?";
                String[] args = new String[]{String.valueOf(competitionId)};
                getContentResolver().update(ContentProvider.COMPETITION_CONTENT_URI, cv, where, args);
                PreferenceManager.getDefaultSharedPreferences(MembersActivity.this).edit().putInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, 1).apply();

                //TODO refresh list not work
                //getSupportLoaderManager().restartLoader(Contract.MEMBERS_LOADER_ID, null, MembersActivity.this);
                //getContentResolver().notifyChange(ContentProvider.COMPETITION_CONTENT_URI, null);
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(competitionId)};
        String sortOrder;
        if (competitionIsClosed()) {
            sortOrder = Contract.MemberEntry.COLUMN_RESULT_TIME + " ASC";
        } else {
            sortOrder = Contract.MemberEntry.COLUMN_START_NUMBER + " ASC";
        }
        return new CursorLoader(this, ContentProvider.MEMBER_CONTENT_URI, null, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void writeFullResultToExcel(View buttonView) throws IOException, WriteException {
        try {
        ExportCompetitionDataToExcel exportToExcel = new ExportCompetitionDataToExcel(MembersActivity.this);
        exportToExcel.writeDataToExcel();
        } catch (IOException e) {
            Log.e(LOG, "IOException");
            e.printStackTrace();
        } catch (WriteException e) {
            Log.e(LOG, "WriteException");
            e.printStackTrace();
        }

    //TODO realize writeEachMemberResultToExcel
    // stub
    }
}
