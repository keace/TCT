package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class NewMemberActivity extends AppCompatActivity {
    EditText lastName, firstName, middleName, birthday, startNumber, team;
    RadioGroup gender;
    long competitionId, memberId;
    public static final String LOG = "Log! NewMemberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Добавить участника");
        setSupportActionBar(toolbar);

        competitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        lastName = (EditText) findViewById(R.id.editTextAddMemberLastName);
        firstName = (EditText) findViewById(R.id.editTextAddMemberFirstName);
        middleName = (EditText) findViewById(R.id.editTextAddMemberMiddleName);
        birthday = (EditText) findViewById(R.id.editTextAddMemberBirthday);
        startNumber = (EditText) findViewById(R.id.editTextAddMemberNumber);
        team = (EditText) findViewById(R.id.editTextAddMemberTeam);
        gender = (RadioGroup) findViewById(R.id.radioGroupAddMemberGender);

        if (getIntent().hasExtra(Contract.MEMBER_ID_ADAPTED)) {
            memberId = getIntent().getLongExtra(Contract.MEMBER_ID_ADAPTED, 0);
            String whereMember = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry._ID + "=?";
            String[] whereMemberArgs = new String[] {String.valueOf(memberId)};
            Cursor cursorMember = getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, whereMember, whereMemberArgs, null);
            cursorMember.moveToFirst();
            Log.i(LOG, "Cursor position = " + cursorMember.getPosition() + ", Column names: " + Arrays.asList(cursorMember.getColumnNames()).toString());

            lastName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_LAST_NAME)));
            firstName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME)));
            middleName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_MIDDLE_NAME)));
            birthday.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_BIRTHDAY)));
            startNumber.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)));
            team.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.TEAM_NAME_ADAPTED)));
        }

//        PreferenceManager.getDefaultSharedPreferences(this).edit().remove(Contract.MEMBER_ID_ADAPTED).apply();

        final Button buttonAddNewMember = (Button) findViewById(R.id.buttonAddNewMember);
        buttonAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewMember()) {
                    Intent intentToMembersActivity = new Intent(NewMemberActivity.this, MembersActivity.class);
                    intentToMembersActivity.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                    startActivity(intentToMembersActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "Новый участник не добавлен", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean addNewMember() {
        boolean isCreated = false;
        int genderId = Contract.GENDER_MALE;
        long personId, teamId = 0;
        ContentValues cv = new ContentValues();

        switch (gender.getCheckedRadioButtonId()){
            case R.id.radioButtonAddMemberGenderM:
                genderId = Contract.GENDER_MALE;
                break;
            case R.id.radioButtonAddMemberGenderF:
                genderId = Contract.GENDER_FEMALE;
                break;
        }

        /* not null fields handling */
        if (lastName.getText().toString().trim().equals("")){
            lastName.setError("Фамилия обязательна!");
        } else if (firstName.getText().toString().trim().equals("")) {
            firstName.setError("Имя обязательно!");
//        } else if (startNumber.getText().toString().trim().equals("")) {
//            startNumber.setError("Стартовый номер нужно ввести!");
        } else {
            //TODO Refactor to use ContentResolver
            SQLiteDatabase sqLiteDatabase = MainActivity.dbHelper.getWritableDatabase();

            String selection = Contract.PersonEntry.COLUMN_LAST_NAME + "=? AND " + Contract.PersonEntry.COLUMN_FIRST_NAME + "=?";
            String[] selectionArgs = new String[] {lastName.getText().toString(), firstName.getText().toString()};
            Cursor c = getContentResolver().query(ContentProvider.PERSON_CONTENT_URI, null, selection, selectionArgs, null);
                cv.clear();
            /* New Person creating if not exists with some lastName and firstName */
                cv.put(Contract.PersonEntry.COLUMN_LAST_NAME, lastName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_FIRST_NAME, firstName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_MIDDLE_NAME, middleName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_GENDER_ID, genderId);
                cv.put(Contract.PersonEntry.COLUMN_BIRTHDAY, birthday.getText().toString());
            if (c.getCount() == 0){
                Log.d(LOG, "Person creating... c.getCount = " + c.getCount());
                personId = Long.parseLong(getContentResolver().insert(ContentProvider.PERSON_CONTENT_URI, cv).getLastPathSegment());
//                sqLiteDatabase.insert(Contract.PersonEntry.TABLE_NAME, null, cv);
                Log.d(LOG, "New Person Create. c.getCount = " + c.getCount() + "; newPersonId = " + personId);
//                cv.clear(); NOT CLEAR FOR UPDATE
                isCreated = true;

                /* If exists then update */
            } else {
                Log.d(LOG, "Person exists. c.getCount = " + c.getCount());
                c.moveToFirst();
                personId = c.getInt(c.getColumnIndex(Contract.PersonEntry._ID));
                String wherePerson = Contract.PersonEntry._ID + "=?";
                String[] wherePersonArgs = new String[] {String.valueOf(personId)};
                getContentResolver().update(ContentProvider.PERSON_CONTENT_URI, cv, wherePerson, wherePersonArgs);
                Log.d(LOG, "Person exists. PersonId = " + personId);
            }
            cv.clear();
            /* New Team Creating if user wrote it and if not exists*/
            if (!team.getText().toString().trim().equals("")) {
                c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.TeamEntry.TABLE_NAME + " WHERE " + Contract.TeamEntry.COLUMN_COMPETITION_ID + "=? AND " + Contract.TeamEntry.COLUMN_NAME + "=?;", new String[] {String.valueOf(competitionId), team.getText().toString()});
                Log.d(LOG, "Field team not clear, getting teamId");
                if (c.getCount() == 0) {
                    Log.d(LOG, "Creating new team");
                    cv.put(Contract.TeamEntry.COLUMN_COMPETITION_ID, competitionId);
                    cv.put(Contract.TeamEntry.COLUMN_NAME, team.getText().toString());
//                    teamId = sqLiteDatabase.insert(Contract.TeamEntry.TABLE_NAME, null, cv);
                    teamId = Long.parseLong(getContentResolver().insert(ContentProvider.TEAM_CONTENT_URI, cv).getLastPathSegment());
//                    cv.clear(); CV NOT CLEAR FOR UPDATE
                    Log.d(LOG, "Created new team with teamId = " + teamId);
                } else {
                    Log.d(LOG, "Team already exists");
                    c.moveToFirst();
                    teamId = c.getInt(c.getColumnIndex(Contract.TeamEntry._ID));
                    String whereTeam = Contract.TeamEntry._ID + "=?";
                    String[] whereTeamArgs = new String[] {String.valueOf(teamId)};
                    getContentResolver().update(ContentProvider.TEAM_CONTENT_URI, cv, whereTeam, whereTeamArgs);
                    Log.d(LOG, "Team with this name already exists with teamId = " + teamId);
                }
            }
            cv.clear();
            /* New Member Creating */
            //TODO Refactor to use ContentResolver
            c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.MemberEntry.TABLE_NAME + " WHERE " + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=? AND " + Contract.MemberEntry.COLUMN_PERSON_ID + "=?;", new String[]{String.valueOf(competitionId), String.valueOf(personId)});
            if (c.getCount() == 0) {
                cv.put(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                cv.put(Contract.MemberEntry.COLUMN_PERSON_ID, personId);
                memberId = Long.parseLong(getContentResolver().insert(ContentProvider.MEMBER_CONTENT_URI, cv).getLastPathSegment());
                Log.d(LOG, "Добавлен новый участник");
            }  else {
                c.moveToFirst();
                memberId = Long.parseLong(c.getString(c.getColumnIndex(Contract.MemberEntry._ID)));
            }
            if (teamId != 0) {
                cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, teamId);
            }
            //TODO При редактировании профиля убрать команду полностью не получается
            if (!startNumber.getText().toString().trim().equals("")){
                cv.put(Contract.MemberEntry.COLUMN_START_NUMBER, startNumber.getText().toString());
            }
            String whereMember = Contract.MemberEntry._ID + "=?";
            String[] whereMembersArgs = new String[] {String.valueOf(memberId)};
            getContentResolver().update(ContentProvider.MEMBER_CONTENT_URI, cv, whereMember, whereMembersArgs);
            cv.clear();
            c.close();
            sqLiteDatabase.close();
            isCreated = true;
//                Toast.makeText(getApplicationContext(), "Участник с такой фамилией и именем уже есть", Toast.LENGTH_SHORT).show();
//                Log.d(LOG, "Member already exists and c.getCount = " + c.getCount());
            }

        return isCreated;
    }
}