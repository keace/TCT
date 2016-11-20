package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import java.util.Arrays;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ua.kyslytsia.tct.adapter.MembersAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.dialog.MembersDialogFragment;
import ua.kyslytsia.tct.utils.Chronometer;
import ua.kyslytsia.tct.utils.WriteExcel;

public class MembersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG = "LOG! Members Activity";

    private MembersAdapter adapter;
    private int competitionIsClosed;
    private long competitionId;
    private String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Список участников");
        toolbar.setNavigationIcon(R.drawable.home_outline);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMainActivity = new Intent(MembersActivity.this, MainActivity.class);
                startActivity(intentToMainActivity);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listViewMembers);

        competitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);
        //competitionIsClosed = PreferenceManager.getDefaultSharedPreferences(this).getInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, 0);
        //SQLiteDatabase sqLiteDatabase = MainActivity.dbHelper.getReadableDatabase();

        Button buttonToNewMember = (Button) findViewById(R.id.buttonMembersToNewMember);
        buttonToNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToNewMember = new Intent(MembersActivity.this, NewMemberActivity.class);
//                intentToNewMember.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToNewMember);
            }
        });

        Button buttonToStages = (Button) findViewById(R.id.buttonMembersToStages);
        buttonToStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStages = new Intent(MembersActivity.this, StagesOnCompetitionActivity.class);
//                intentToStages.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToStages);
            }
        });

        Button buttonExportToExcel = (Button) findViewById(R.id.buttonMembersExportToExcel);
        buttonExportToExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFullResultToExcel(competitionId);
            }
        });

        String where = Contract.CompetitionEntry.COLUMN_IS_CLOSED + "=?";
        String[] whereArgs = new String[] {String.valueOf(Contract.COMPETITION_CLOSED)};
        Cursor cursor = getContentResolver().query(ContentProvider.COMPETITION_CONTENT_URI, null, where, whereArgs, null);
        Log.i(LOG, "Cursor getCount = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            buttonToNewMember.setEnabled(false);
            buttonToStages.setEnabled(false);
            buttonExportToExcel.setVisibility(View.VISIBLE);
            sortOrder = Contract.MemberEntry.COLUMN_RESULT_TIME + " ASC";
        } else {
            buttonToNewMember.setEnabled(true);
            buttonToStages.setEnabled(true);
            buttonExportToExcel.setVisibility(View.GONE);
            sortOrder = Contract.MemberEntry.COLUMN_START_NUMBER + " ASC";
        }

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

        //TODO onLongClickListener - menu with delete (with confirm) and edit...
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG, "to constructor memberId = " + id);
                android.app.FragmentManager fm = getFragmentManager();
                MembersDialogFragment membersDialogFragment = MembersDialogFragment.newInstance(id);
                membersDialogFragment.show(fm, "membersDialog");
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_competition_complete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Завершить соревнование?");
                alertDialog.setMessage("Поменять уже ничего нельзя будет...");
                alertDialog.setNegativeButton("Отмена", null);
                alertDialog.setPositiveButton("Завершить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.CompetitionEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_CLOSED);
                        String where = Contract.CompetitionEntry._ID + "=?";
                        String[] args = new String[]{String.valueOf(competitionId)};
                        getContentResolver().update(ContentProvider.COMPETITION_CONTENT_URI, cv, where, args);
                        PreferenceManager.getDefaultSharedPreferences(MembersActivity.this).edit().putInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, 1).apply();
                        //getSupportLoaderManager().restartLoader(Contract.MEMBERS_LOADER_ID, null, MembersActivity.this);
                        getContentResolver().notifyChange(ContentProvider.COMPETITION_CONTENT_URI, null);
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(competitionId)};
        CursorLoader cursorLoader = new CursorLoader(MembersActivity.this, ContentProvider.MEMBER_CONTENT_URI, null, selection, selectionArgs, sortOrder);
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

    //TODO realize writeFullResultToExcel
    public void writeFullResultToExcel(long competitionId){
        String where = Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry._ID + "=?";
        String[] args = new String[] {String.valueOf(competitionId)};
        Cursor c = getContentResolver().query(ContentProvider.COMPETITION_CONTENT_URI, null, where, args, null);
        c.moveToFirst();
        String date = c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE));
        String name = c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME));
        String place = c.getString(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE));
        String type = c.getString(c.getColumnIndex(Contract.TYPE_NAME_ADAPTED));
        String distance = c.getString(c.getColumnIndex(Contract.DISTANCE_NAME_ADAPTED));
        int rank = c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK));
        int penaltyCost = c.getInt(c.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_COST));


        try {
            WriteExcel writeExcel= new WriteExcel();
            WritableWorkbook writableWorkbook = writeExcel.createWorkbook("firsWorkBook.xls");
            WritableSheet writableSheet = writeExcel.createSheet(writableWorkbook, "competitionSheet", 0);

            // Шапка по соревнованию
            writeExcel.writeCell(writableSheet, 0, 0, "Протокол соревновний по спортивному туризму", true);
            writeExcel.writeCell(writableSheet, 0, 1, "Дата", true);
            writeExcel.writeCell(writableSheet, 0, 2, "Название", true);
            writeExcel.writeCell(writableSheet, 0, 3, "Место проведения", true);
            writeExcel.writeCell(writableSheet, 0, 4, "Вид туризма", true);
            writeExcel.writeCell(writableSheet, 0, 5, "Дистанция", true);
            writeExcel.writeCell(writableSheet, 0, 6, "Класс", true);
            writeExcel.writeCell(writableSheet, 0, 7, "Штраф, сек", true);

            writeExcel.writeCell(writableSheet, 1, 1, date, false);
            writeExcel.writeCell(writableSheet, 1, 2, name, false);
            writeExcel.writeCell(writableSheet, 1, 3, place, false);
            writeExcel.writeCell(writableSheet, 1, 4, type, false);
            writeExcel.writeCell(writableSheet, 1, 5, distance, false);
            writeExcel.writeCell(writableSheet, 1, 6, String.valueOf(rank), false);
            writeExcel.writeCell(writableSheet, 1, 7, String.valueOf(penaltyCost), false);

            //Шапка по участникам, этапам и результатам
            //Шапка-Участник
            writeExcel.writeCell(writableSheet, 0, 9, "Стартовый номер", true);
            writeExcel.writeCell(writableSheet, 1, 9, "Команда", true);
            writeExcel.writeCell(writableSheet, 2, 9, "ФИО", true);
            writeExcel.writeCell(writableSheet, 3, 9, "Дата рождения", true);

            //Шапка-Этапы
            String whereStageOnComp = Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=?";
            String[] argsStageOnComp = new String[] {String.valueOf(competitionId)};
            Cursor c1 = getContentResolver().query(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, whereStageOnComp, argsStageOnComp, null);
            Log.d(LOG, "Stage on Competition getCount = " + c1.getCount());
            int column = 4;
            c1.moveToFirst();
            for (int i = 0; i < c1.getCount(); i++) {
                Log.d(LOG, "Cursor position = " + c1.getPosition() + ", columnNames = " + Arrays.asList(c1.getColumnNames()));
                String stageName = c1.getString(c1.getColumnIndex(Contract.STAGE_NAME_ADAPTED));
//                String stagePenalty = c1.getString(c1.getColumnIndex(Contract.StageOnAttemptEntry.COLUMN_PENALTY));
                Log.d(LOG, "Try to write to cell: " + column + " row 9, string: " + stageName);
//                Log.d(LOG, "Try to write to cell: " + column + " row 10, string: " + stagePenalty);
                writeExcel.writeCell(writableSheet, column, 9, stageName, true);
//                writeExcel.writeCell(writableSheet, column, 10, stagePenalty, false);
                column += 1;
                c1.moveToNext();
            }

            //Шапка-Результаты
            writeExcel.writeCell(writableSheet, column++, 9, "Время дистанции", true);
            writeExcel.writeCell(writableSheet, column++, 9, "Сумма штрафов", true);
            writeExcel.writeCell(writableSheet, column++, 9, "Результат", true);
            writeExcel.writeCell(writableSheet, column++, 9, "Место", true);

            //Значения-Результаты
            String whereMember = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
//            String whereMember = Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
            String[] whereArgsMember = new String[] {String.valueOf(competitionId)};
            String sortMember = Contract.MemberEntry.COLUMN_RESULT_TIME;
            Cursor cursorMembers = getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, whereMember, whereArgsMember, sortMember);
            Log.i(LOG, "CursorMembers getCount = " + cursorMembers.getCount());
            cursorMembers.moveToFirst();

            int row = 9;
            Log.d(LOG, "Cursor Members count = " + cursorMembers.getCount() + ", position = " + cursorMembers.getPosition() + ", columnNames = " + Arrays.asList(cursorMembers.getColumnNames()));
            cursorMembers.moveToFirst();
            for (int i = 0; i < cursorMembers.getCount(); i++) {
                ++row;
                column = 0;
                String memberId = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.MemberEntry._ID));
                String startNumber = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER));
                String team = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.TEAM_NAME_ADAPTED));
                String lastName = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.PersonEntry.COLUMN_LAST_NAME));
                String firstName = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME));
                String middleName = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.PersonEntry.COLUMN_MIDDLE_NAME));
                String birthday = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.PersonEntry.COLUMN_BIRTHDAY));
                String placeNumber = cursorMembers.getString(cursorMembers.getColumnIndex(Contract.MEMBER_PLACE_ADAPTED));

                StringBuilder sb = new StringBuilder();
                sb.append(lastName).append(" ").append(firstName).append(" ").append(middleName);
                String fullName = sb.toString();
                sb.delete(0, sb.length());
                Log.i(LOG, "Member = " + fullName + ", i = " + i + ", write to row = " + row + ", column from = " + column);
                writeExcel.writeCell(writableSheet, column++, row, startNumber, false);
                writeExcel.writeCell(writableSheet, column++, row, team, false);
                writeExcel.writeCell(writableSheet, column++, row, fullName, false);
                writeExcel.writeCell(writableSheet, column++, row, birthday, false);

                String whereAttempt = Contract.AttemptEntry.COLUMN_COMPETITION_ID + "=? and " + Contract.AttemptEntry.COLUMN_MEMBERS_ID + "=?";
                String[] whereArgsAttempt = new String[] {String.valueOf(competitionId), memberId};
                Cursor cursorAttempt = getContentResolver().query(ContentProvider.ATTEMPT_CONTENT_URI, null, whereAttempt, whereArgsAttempt, null);
                cursorAttempt.moveToFirst();
                Log.d(LOG, "Cursor Attempt count = " + cursorAttempt.getCount() + ", position = " + cursorAttempt.getPosition() + ", columnNames = " + Arrays.asList(cursorAttempt.getColumnNames()));
                String attemptId = "";
                String penaltyTotal = "";
                String distanceTime = "";
                String resultTime = "";
                if (cursorAttempt.getCount() > 0) {
                    Log.i(LOG, "cursorAttempt getCount = " + cursorAttempt.getCount());
                    attemptId = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry._ID));
                    penaltyTotal = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_PENALTY_TOTAL));
                    distanceTime = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_DISTANCE_TIME));
                    long resultTimeMillis = Long.parseLong(cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_RESULT_TIME)));
                    resultTime = new Chronometer(this).timeLongMillisToString(resultTimeMillis);
                    //resultTime = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_RESULT_TIME));

                    //write stages penalty
                    String whereSOA = Contract.StageOnAttemptEntry.COLUMN_ATTEMPT_ID + "=?";
                    Log.i(LOG, "Attempt.id = " + attemptId);
                    String[] whereArgsSOA = new String[] {attemptId};
                    Cursor cursorSOA = getContentResolver().query(ContentProvider.STAGE_ON_ATTEMPT_CONTENT_URI, null, whereSOA, whereArgsSOA, null);
                    cursorSOA.moveToFirst();
                    for (int j = 0; j < cursorSOA.getCount(); j++ ) {
                        Log.d(LOG, "Cursor SOA count = " + cursorSOA.getCount() + ", position = " + cursorSOA.getPosition() + ", columnNames = " + Arrays.asList(cursorSOA.getColumnNames()));
                        String stagePenalty = cursorSOA.getString(cursorSOA.getColumnIndex(Contract.StageOnAttemptEntry.COLUMN_PENALTY));
                        writeExcel.writeCell(writableSheet, column++, row, stagePenalty, false);
                        Log.i(LOG, "Stage penalty = " + stagePenalty);
                        cursorSOA.moveToNext();
                    }

                    //write result
                    writeExcel.writeCell(writableSheet, column++, row, distanceTime, false);
                    writeExcel.writeCell(writableSheet, column++, row, penaltyTotal, false);
                    writeExcel.writeCell(writableSheet, column++, row, resultTime, false);
                    writeExcel.writeCell(writableSheet, column++, row, placeNumber, false);

                    //Запись
//                    writableWorkbook.write();
                    Log.d(LOG, "Write to excel");
                }
//                row += 1;
//                column = 0;
//                writableWorkbook.write();
                cursorMembers.moveToNext();
            }
            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO realize writeEachMemberResultToExcel
    public void writeEachMemberResultToExcel(long memberId){
        // stub
    }
}
