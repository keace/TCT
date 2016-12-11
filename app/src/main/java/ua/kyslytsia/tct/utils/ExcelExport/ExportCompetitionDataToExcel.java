package ua.kyslytsia.tct.utils.excelExport;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.utils.Chronometer;

public class ExportCompetitionDataToExcel {
    public static final String LOG = "Log! WriteDataToExcel";

    private Context mContext;
    private int mColumn = 0;
    private int mRow = 0;

    private WriteExcelBasic mWriteExcelBasic = new WriteExcelBasic();
    private WritableWorkbook mWritableWorkbook = mWriteExcelBasic.createWorkbook("firstWorkBook.xls");
    private WritableSheet mWritableSheet = mWriteExcelBasic.createSheet(mWritableWorkbook, "competitionSheet", 0);

    private LinkedHashMap<String, String> mResultMap = new LinkedHashMap<>();

    public long competitionId;

    public ExportCompetitionDataToExcel(Context mContext) throws IOException, WriteException {
        this.mContext = mContext;
        competitionId = PreferenceManager.getDefaultSharedPreferences(mContext).getLong(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);
    }

    public void writeDataToExcel() throws IOException, WriteException {
            mResultMap.clear();

            fillCompetitionHeaderKeys();
            fillCompetitionHeaderValues();
            fillResultsHeader();
            fillResultsValues();

            mWritableWorkbook.write();
            mWritableWorkbook.close();
    }

    private void fillCompetitionHeaderKeys() throws WriteException {
        Log.i(LOG, "fillCompetitionHeaderKeys() START");
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, mContext.getResources().getString(R.string.excel_sheet_title), true);

        String[] competitionHeader = mContext.getResources().getStringArray(R.array.competition_header);
        for (int i = 0; i < competitionHeader.length; i++) {
            Log.i(LOG, "Write to mColumn " + mColumn + ", mRow " + mRow + ": " + competitionHeader[i]);
            mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, competitionHeader[i], true);
        }
        Log.i(LOG, "fillCompetitionHeaderKeys() END. mColumn = " + mColumn + ", mRow = " + mRow);
    }

    private void fillCompetitionHeaderValues() throws WriteException {
        Log.i(LOG, "fillCompetitionHeaderValues() START");

        mColumn++;
        mRow = 1; // Row after main title

        String where = Contract.CompetitionEntry.TABLE_NAME + "." + Contract.CompetitionEntry._ID + "=?";
        String[] args = new String[]{String.valueOf(competitionId)};
        Cursor cursorCompetition = mContext.getContentResolver().query(ContentProvider.COMPETITION_CONTENT_URI, null, where, args, null);
        cursorCompetition.moveToFirst();

        String date = cursorCompetition.getString(cursorCompetition.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE));
        String name = cursorCompetition.getString(cursorCompetition.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME));
        String place = cursorCompetition.getString(cursorCompetition.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE));
        String type = cursorCompetition.getString(cursorCompetition.getColumnIndex(Contract.TYPE_NAME_ADAPTED));
        String distance = cursorCompetition.getString(cursorCompetition.getColumnIndex(Contract.DISTANCE_NAME_ADAPTED));
        int rank = cursorCompetition.getInt(cursorCompetition.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK));
        int penaltyCost = cursorCompetition.getInt(cursorCompetition.getColumnIndex(Contract.CompetitionEntry.COLUMN_PENALTY_COST));

        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, date, false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, name, false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, place, false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, type, false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, distance, false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, String.valueOf(rank), false);
        mWriteExcelBasic.writeCell(mWritableSheet, mColumn, mRow++, String.valueOf(penaltyCost), false);
        Log.i(LOG, "fillCompetitionHeaderValues() END, values: " + date + ", " + name + ", " + place + ", " + type + ", " + distance + "...");
        cursorCompetition.close();
    }

    private void fillResultsHeader() throws WriteException {
        Log.i(LOG, "fillResultsHeader() START");

        mResultMap.put(ExportContract.START_NUMBER, mContext.getString(R.string.excel_result_start_number));
        mResultMap.put(ExportContract.TEAM, mContext.getString(R.string.excel_result_team));
        mResultMap.put(ExportContract.FULL_NAME, mContext.getString(R.string.excel_result_full_name));
        mResultMap.put(ExportContract.BIRTHDAY, mContext.getString(R.string.excel_result_birthday));

        ArrayList<String> stageNames = findStageNamesForCompetition();
        for (int i = 0; i < stageNames.size(); i++) {
            mResultMap.put(stageNames.get(i), stageNames.get(i));
        }
        mResultMap.put(ExportContract.DISTANCE_TIME, mContext.getString(R.string.excel_result_distance_time));
        mResultMap.put(ExportContract.PENALTY_TOTAL, mContext.getString(R.string.excel_result_penalty_total));
        mResultMap.put(ExportContract.RESULT_TIME, mContext.getString(R.string.excel_result_result_time));
        mResultMap.put(ExportContract.PLACE, mContext.getString(R.string.excel_result_place_number));

        mColumn = 0;

        Iterator<Map.Entry<String, String>> iterator = mResultMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> headerValue = iterator.next();
            Log.i(LOG, "ITERATOR Write to mColumn " + mColumn + ", mRow " + mRow + ": " + headerValue.getValue());
            mWriteExcelBasic.writeCell(mWritableSheet, mColumn++, mRow, headerValue.getValue(), true);
            mResultMap.put(headerValue.getKey(), "0"); //Clear the values to properly export
        }
        mRow++;
        Log.i(LOG, "fillResultsHeader() END");
    }

    private ArrayList<String> findStageNamesForCompetition() throws WriteException {
        Log.i(LOG, "findStageNamesForCompetition() START");
        ArrayList<String> stageNamesArrayList = new ArrayList<>();

        String whereStageOnComp = Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "=?";
        String[] argsStageOnComp = new String[]{String.valueOf(competitionId)};

        Cursor cursorStageOnComp = mContext.getContentResolver().query(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, null, whereStageOnComp, argsStageOnComp, null);
        Log.d(LOG, "Stage on Competition getCount = " + cursorStageOnComp.getCount() + ", columnNames = " + Arrays.asList(cursorStageOnComp.getColumnNames()));

        cursorStageOnComp.moveToFirst();
        for (int i = 0; i < cursorStageOnComp.getCount(); i++) {
            String stageName = cursorStageOnComp.getString(cursorStageOnComp.getColumnIndex(Contract.STAGE_NAME_ADAPTED));
            stageNamesArrayList.add(stageName);
            mColumn++;
            cursorStageOnComp.moveToNext();
        }
        cursorStageOnComp.close();
        Log.i(LOG, "findStageNamesForCompetition() END. stageNames = " + stageNamesArrayList.toString());
        return stageNamesArrayList;
    }

    private void fillResultsValues() throws WriteException {
        Log.i(LOG, "fillResultValues() START");

        String whereMember = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
        String[] whereArgsMember = new String[]{String.valueOf(competitionId)};
        String sortMember = Contract.MemberEntry.COLUMN_RESULT_TIME;
        Cursor cursorMembers = mContext.getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, whereMember, whereArgsMember, sortMember);

        Log.d(LOG, "Cursor Members count = " + cursorMembers.getCount() + ", columnNames = " + Arrays.asList(cursorMembers.getColumnNames()));
        cursorMembers.moveToFirst();
        for (int i = 0; i < cursorMembers.getCount(); i++) {
            long memberId = cursorMembers.getLong(cursorMembers.getColumnIndex(Contract.MemberEntry._ID));
            findAllDataForMember(memberId);

            mColumn = 0;
            Iterator<Map.Entry<String, String>> iterator = mResultMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> valueToWrite = iterator.next();
                Log.i(LOG, "ITERATOR RESULTS write to mColumn " + mColumn + ", mRow " + mRow + " value: " + valueToWrite.getValue());
                mWriteExcelBasic.writeCell(mWritableSheet, mColumn++, mRow, valueToWrite.getValue(), false);
                mResultMap.put(valueToWrite.getKey(), "0"); // Clear the value for future
            }
                mRow++;
            cursorMembers.moveToNext();
        }
        cursorMembers.close();
        Log.i(LOG, "fillResultValues() END");
    }

    private void findAllDataForMember(long memberId) {
        findMainDataForMember(memberId);
        long attemptId = findCompetitionResultsForMember(memberId);
        findStagesResultsForMember(attemptId);
    }

    private void findMainDataForMember(long memberId) {
        Log.i(LOG, "findMainDataForMember() START");
        String where = Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry._ID + "=? AND " + Contract.MemberEntry.TABLE_NAME + "." + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(memberId), String.valueOf(competitionId)};
        Cursor cursorMembers = mContext.getContentResolver().query(ContentProvider.MEMBER_CONTENT_URI, null, where, whereArgs, null);
        Log.i(LOG, "CursorMembers getCount = " + cursorMembers.getCount());
        cursorMembers.moveToFirst();

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

        Log.i(LOG, "findMainDataForMember() data: " + startNumber + ", " + ", " + team + ", " + fullName + ", " + birthday);
        mResultMap.put(ExportContract.START_NUMBER, startNumber);
        mResultMap.put(ExportContract.TEAM, team);
        mResultMap.put(ExportContract.FULL_NAME, fullName);
        mResultMap.put(ExportContract.BIRTHDAY, birthday);
        mResultMap.put(ExportContract.PLACE, placeNumber);
        cursorMembers.close();
        Log.i(LOG, "findMainDataForMember() END");
    }

    private long findCompetitionResultsForMember(long memberId) {
        Log.i(LOG, "findCompetitionResultsForMember() START, memberId = " + memberId);
        long attemptId = 0;

        String whereAttempt = Contract.AttemptEntry.COLUMN_COMPETITION_ID + "=? and " + Contract.AttemptEntry.COLUMN_MEMBERS_ID + "=?";
        String[] whereArgsAttempt = new String[]{String.valueOf(competitionId), String.valueOf(memberId)};
        Cursor cursorAttempt = mContext.getContentResolver().query(ContentProvider.ATTEMPT_CONTENT_URI, null, whereAttempt, whereArgsAttempt, null);
        cursorAttempt.moveToFirst();
        Log.d(LOG, "Cursor Attempt count = " + cursorAttempt.getCount() + ", position = " + cursorAttempt.getPosition() + ", columnNames = " + Arrays.asList(cursorAttempt.getColumnNames()));

        if (cursorAttempt.getCount() > 0) {
            attemptId = cursorAttempt.getLong(cursorAttempt.getColumnIndex(Contract.AttemptEntry._ID));
            String penaltyTotal = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_PENALTY_TOTAL));
            String distanceTime = cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_DISTANCE_TIME));
            long resultTimeMillis = Long.parseLong(cursorAttempt.getString(cursorAttempt.getColumnIndex(Contract.AttemptEntry.COLUMN_RESULT_TIME)));
            String resultTime = new Chronometer(mContext).timeLongMillisToString(resultTimeMillis);

            mResultMap.put(ExportContract.PENALTY_TOTAL, penaltyTotal);
            mResultMap.put(ExportContract.DISTANCE_TIME, distanceTime);
            mResultMap.put(ExportContract.RESULT_TIME, resultTime);
        }
        cursorAttempt.close();
        Log.i(LOG, "findCompetitionResultsForMember() END, attemptId = " + attemptId);
        return attemptId;
    }

    private void findStagesResultsForMember(long attemptId) {
        Log.i(LOG, "findStagesResultsForMember() START, attemptId = " + attemptId);

        /* Handle only 1 attempt !! */
        String whereSOA = Contract.StageOnAttemptEntry.COLUMN_ATTEMPT_ID + "=?";
        String[] whereArgsSOA = new String[]{String.valueOf(attemptId)};
        Cursor cursorSOA = mContext.getContentResolver().query(ContentProvider.STAGE_ON_ATTEMPT_CONTENT_URI, null, whereSOA, whereArgsSOA, null);
        Log.d(LOG, "Cursor SOA count = " + cursorSOA.getCount() + ", columnNames = " + Arrays.asList(cursorSOA.getColumnNames()));
        cursorSOA.moveToFirst();
        for (int j = 0; j < cursorSOA.getCount(); j++) {
            String stageName = cursorSOA.getString(cursorSOA.getColumnIndex(Contract.StageEntry.COLUMN_NAME));
            String stagePenalty = cursorSOA.getString(cursorSOA.getColumnIndex(Contract.StageOnAttemptEntry.COLUMN_PENALTY));

            mResultMap.put(stageName, stagePenalty);
            Log.i(LOG, "Write to Map: StageName = " + stageName + ", StagePenalty = " + stagePenalty);
            cursorSOA.moveToNext();
        }
        cursorSOA.close();
        Log.i(LOG, "findStagesResultsForMember() END");
    }

}