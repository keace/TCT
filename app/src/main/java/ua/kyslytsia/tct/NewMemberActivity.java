package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Arrays;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class NewMemberActivity extends AppCompatActivity {
    public static final String LOG = "Log! NewMemberActivity";
    private static final int CREATE_NEW_MEMBER = 1;
    private static final int EDIT_EXISTING_MEMBER = 2;
    int createOrEditFlag;
    private EditText lastName, firstName, middleName, birthday, startNumber, team;
    private RadioGroup gender;
    private long personId, teamId, genderId;
    private long mCompetitionId, memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);
        initToolbar();

        mCompetitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);
        Log.i(LOG, "competitionId = " + mCompetitionId);

        lastName = (EditText) findViewById(R.id.editTextAddMemberLastName);
        firstName = (EditText) findViewById(R.id.editTextAddMemberFirstName);
        middleName = (EditText) findViewById(R.id.editTextAddMemberMiddleName);
        birthday = (EditText) findViewById(R.id.editTextAddMemberBirthday);
        startNumber = (EditText) findViewById(R.id.editTextAddMemberNumber);
        team = (EditText) findViewById(R.id.editTextAddMemberTeam);
        gender = (RadioGroup) findViewById(R.id.radioGroupAddMemberGender);

        /* Handle Edit Member */
        if (isEditMember()) {
            Log.d(LOG, "Edit member");
            createOrEditFlag = EDIT_EXISTING_MEMBER;
            editMember();
        } else {
            createOrEditFlag = CREATE_NEW_MEMBER;
        }

        initButton();
    }

    private void editMember() {
        memberId = getIntent().getLongExtra(Contract.AttemptEntry.COLUMN_MEMBER_ID, 0);
        String whereMember = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry._ID + "=?";
        String[] whereMemberArgs = new String[]{String.valueOf(memberId)};
        Cursor cursorMember = getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, whereMember, whereMemberArgs, null);

        if (cursorMember != null) {
            cursorMember.moveToFirst();
            Log.i(LOG, "Cursor position = " + cursorMember.getPosition() + ", Column names: " + Arrays.asList(cursorMember.getColumnNames()).toString());
            lastName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_LAST_NAME)));
            firstName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME)));
            middleName.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_MIDDLE_NAME)));
            birthday.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.PersonEntry.COLUMN_BIRTHDAY)));
            startNumber.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)));
            team.setText(cursorMember.getString(cursorMember.getColumnIndex(Contract.TeamEntry.COLUMN_NAME)));

            genderId = cursorMember.getLong(cursorMember.getColumnIndex("genderId"));
            if (genderId == 0) {
                gender.check(R.id.radioButtonAddMemberGenderF);
            } else {
                gender.check(R.id.radioButtonAddMemberGenderM);
            }

            personId = cursorMember.getLong(cursorMember.getColumnIndex("personId"));
            teamId = cursorMember.getLong(cursorMember.getColumnIndex("teamId"));
            cursorMember.close();
        }
    }

    private void initButton() {
        final Button buttonAddNewMember = (Button) findViewById(R.id.buttonAddNewMember);
        if (buttonAddNewMember != null) {

            switch (createOrEditFlag) {
                case CREATE_NEW_MEMBER: {
                    buttonAddNewMember.setText(R.string.new_members_activity_button_new_member);
                    break;
                }
                case EDIT_EXISTING_MEMBER: {
                    buttonAddNewMember.setText(R.string.new_members_activity_button_edit_member);
                    break;
                }
            }

            buttonAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (createOrEditFlag) {
                    case CREATE_NEW_MEMBER: {
                        addNewMember();
                        Intent intentToMembersActivity = new Intent(NewMemberActivity.this, MembersActivity.class);
                        intentToMembersActivity.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, mCompetitionId);
                        startActivity(intentToMembersActivity);
                        break;
                    }
                    case EDIT_EXISTING_MEMBER: {
                        updatePersonTableWithNewData();

                        /* update member */
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.MemberEntry.COLUMN_START_NUMBER, startNumber.getText().toString());

                        String newTeamName = team.getText().toString();
                        if (memberWithoutTeam() && !newTeamName.equals("")) {
                            long newTeamId = createNewTeamWithName(newTeamName);
                            cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, newTeamId);

                        } else { // member With Team
                            long existsTeamId = getTeamIdIfExists(newTeamName);

                            // team with same name already exists?
                            if (existsTeamId > 0) {
                                cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, existsTeamId);
                            } else {
                                long newTeamId = createNewTeamWithName(newTeamName);
                                cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, newTeamId);
                            }
                        }
                        String whereMember = Contract.MemberEntry._ID + "=?";
                        String[] whereMemberArgs = new String[]{String.valueOf(memberId)};
                        getContentResolver().update(ContentProvider.MEMBER_CONTENT_URI, cv, whereMember, whereMemberArgs);

                        Intent intentToMembers = new Intent(NewMemberActivity.this, MembersActivity.class);
                        startActivity(intentToMembers);
                        break;
                    }
                }
            }
                                              }

        );
    }}

    private long getTeamIdIfExists(String newTeamName) {
        DbHelper dbHelper = new DbHelper(NewMemberActivity.this);
        return dbHelper.findTeamIdByName(mCompetitionId, newTeamName);
    }

    private void updatePersonTableWithNewData() {
        ContentValues cv = new ContentValues();
        cv.put(Contract.PersonEntry.COLUMN_FIRST_NAME, firstName.getText().toString());
        cv.put(Contract.PersonEntry.COLUMN_MIDDLE_NAME, middleName.getText().toString());
        cv.put(Contract.PersonEntry.COLUMN_LAST_NAME, lastName.getText().toString());
        cv.put(Contract.PersonEntry.COLUMN_BIRTHDAY, birthday.getText().toString());
        cv.put(Contract.PersonEntry.COLUMN_GENDER_ID, getGenderId());
        String wherePerson = Contract.PersonEntry._ID + "=?";
        String[] wherePersonArgs = new String[]{String.valueOf(personId)};
        getContentResolver().update(ContentProvider.PERSON_CONTENT_URI, cv, wherePerson, wherePersonArgs);
    }

    private boolean memberWithoutTeam() {
        return teamId == 0;
    }

    private long createNewTeamWithName(String newTeamName) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TeamEntry.COLUMN_NAME, newTeamName);
        cv.put(Contract.TeamEntry.COLUMN_COMPETITION_ID, mCompetitionId);
        return Long.parseLong(getContentResolver().insert(ContentProvider.TEAM_CONTENT_URI, cv).getLastPathSegment());
    }

    private boolean isEditMember() {
        return getIntent().hasExtra(Contract.AttemptEntry.COLUMN_MEMBER_ID);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.new_members_activity_toolbar_subtitle);
        }
        setSupportActionBar(toolbar);
    }

    public boolean addNewMember() {
        boolean isCreated = false;
        int genderId = getGenderId();
        ContentValues cv = new ContentValues();

        /* not null fields handling */
        if (lastNameFieldIsEmpty()) {
            lastName.setError(getResources().getString(R.string.new_members_activity_error_last_name_required));
        } else if (firstNameFieldIsEmpty()) {
            firstName.setError(getResources().getString(R.string.new_members_activity_error_first_name_required));
        } else {
            String selection = Contract.PersonEntry.COLUMN_LAST_NAME + "=? AND " + Contract.PersonEntry.COLUMN_FIRST_NAME + "=?";
            String[] selectionArgs = new String[]{lastName.getText().toString(), firstName.getText().toString()};
            Cursor cursor = getContentResolver().query(ContentProvider.PERSON_CONTENT_URI, null, selection, selectionArgs, null);
            cv.clear();

            /* New Person creating if not exists with same lastName and firstName */
            cv.put(Contract.PersonEntry.COLUMN_LAST_NAME, lastName.getText().toString());
            cv.put(Contract.PersonEntry.COLUMN_FIRST_NAME, firstName.getText().toString());
            cv.put(Contract.PersonEntry.COLUMN_MIDDLE_NAME, middleName.getText().toString());
            cv.put(Contract.PersonEntry.COLUMN_GENDER_ID, genderId);
            cv.put(Contract.PersonEntry.COLUMN_BIRTHDAY, birthday.getText().toString());

            if (cursor.getCount() == 0) { //if person not exists - create
                personId = Long.parseLong(getContentResolver().insert(ContentProvider.PERSON_CONTENT_URI, cv).getLastPathSegment());
                Log.d(LOG, "New Person Create. cursor.getCount = " + cursor.getCount() + "; newPersonId = " + personId);
                /* If Member with same FirstName and LastName exists then update */
            } else { // if person exists - update
                cursor.moveToFirst();
                personId = cursor.getInt(cursor.getColumnIndex(Contract.PersonEntry._ID));
                String wherePerson = Contract.PersonEntry._ID + "=?";
                String[] wherePersonArgs = new String[]{String.valueOf(personId)};
                getContentResolver().update(ContentProvider.PERSON_CONTENT_URI, cv, wherePerson, wherePersonArgs);
                Log.d(LOG, "Person exists. PersonId = " + personId);
            }
            cv.clear();

            /* New Team Creating if user wrote team name and if not exists*/
            String teamName = team.getText().toString();
            if (!teamName.trim().equals("")) {
                long existsTeamId = getTeamIdIfExists(teamName);
                // team with same name already exists?
                if (existsTeamId > 0) {
                    cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, existsTeamId);
                } else {
                    long newTeamId = createNewTeamWithName(teamName);
                    cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, newTeamId);
                }
            }
            /* New Member Creating */
                String where = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=? AND " +
                        Contract.MemberEntry.COLUMN_PERSON_ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(mCompetitionId), String.valueOf(personId)};
                cursor = getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, where, whereArgs, null);

                if (cursor.getCount() == 0) { // if member not exists create it an getId
                    cv.put(Contract.MemberEntry.COLUMN_COMPETITION_ID, mCompetitionId);
                    cv.put(Contract.MemberEntry.COLUMN_PERSON_ID, personId);
                    memberId = Long.parseLong(getContentResolver().insert(ContentProvider.MEMBER_CONTENT_URI, cv).getLastPathSegment());
                    Log.d(LOG, "Добавлен новый участник");
                } else { // if member exists getId
                    cursor.moveToFirst();
                    memberId = Long.parseLong(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry._ID)));
                }

                if (!startNumber.getText().toString().trim().equals("")) {
                    cv.put(Contract.MemberEntry.COLUMN_START_NUMBER, startNumber.getText().toString());
                }

                String whereMember = Contract.MemberEntry._ID + "=?";
                String[] whereMembersArgs = new String[]{String.valueOf(memberId)};
                getContentResolver().update(ContentProvider.MEMBER_CONTENT_URI, cv, whereMember, whereMembersArgs);
                cv.clear();
                cursor.close();
                isCreated = true;
            }

        return isCreated;
    }

    private int getGenderId() {
        int genderId = Contract.GENDER_MALE;

        switch (gender.getCheckedRadioButtonId()) {
            case R.id.radioButtonAddMemberGenderM:
                genderId = Contract.GENDER_MALE;
                break;
            case R.id.radioButtonAddMemberGenderF:
                genderId = Contract.GENDER_FEMALE;
                break;
        }
        return genderId;
    }

    private boolean firstNameFieldIsEmpty() {
        return firstName.getText().toString().trim().equals("");
    }

    private boolean lastNameFieldIsEmpty() {
        return lastName.getText().toString().trim().equals("");
    }
}